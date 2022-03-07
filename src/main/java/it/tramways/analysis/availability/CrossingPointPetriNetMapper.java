package it.tramways.analysis.availability;

import it.tramways.analysis.Configurable;
import it.tramways.analysis.availability.builder.ChoicePropertyBuilder;
import it.tramways.analysis.availability.distributions.DistributionMapper;
import it.tramways.analysis.roadmap.NetworkMap;
import it.tramways.analysis.roadmap.NetworkPoint;
import it.tramways.analysis.roadmap.RoadMapNetworkMapper;
import it.tramways.projects.api.v1.model.*;
import org.oristool.models.pn.Priority;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Transition;

import java.math.BigDecimal;

import static it.tramways.analysis.availability.AvailabilityAnalysisProperties.*;

public class CrossingPointPetriNetMapper {

    private PetriNet net;
    private Marking marking;

    private Integer period;

    public PetriNet map(RoadMap source, PropertySource properties) {
        net = new PetriNet();
        // Places
        Place incoming = net.addPlace("incoming");
        Place sensor = net.addPlace("sensor");
        Place crossing = net.addPlace("crossing");
        Place red = net.addPlace("red");

        // Transitions
        Transition periodTransition = net.addTransition("period");
        Transition delayT = net.addTransition("delay");
        Transition trafficLightAnticipationT = net.addTransition("anticipation");
        Transition leavingT = net.addTransition("leaving");

        // Links
        net.addPostcondition(periodTransition, incoming);
        net.addPrecondition(incoming, delayT);
        net.addPostcondition(delayT, sensor);
        net.addPostcondition(delayT, red);
        net.addPrecondition(sensor, trafficLightAnticipationT);
        net.addPostcondition(trafficLightAnticipationT, crossing);
        net.addPrecondition(red, leavingT);
        net.addPrecondition(crossing, leavingT);

        // Tokens (here comes the initial marking)
        marking = new Marking();
        marking.setTokens(incoming, 1);
        marking.setTokens(crossing, 0);
        marking.setTokens(sensor, 0);
        marking.setTokens(red, 0);

        RoadMapNetworkMapper networkMapper = new RoadMapNetworkMapper(source);
        NetworkMap map = networkMapper.map();

        NetworkPoint sourcePoint = grabTramSourcePoint(map, properties);

        period = getIntegerValue(sourcePoint, PERIOD);

        NetworkPoint crossingPoint = sourcePoint.getDestinations().iterator().next().getDestination();

        Integer anticipation = getIntegerValue(crossingPoint, ANTICIPATION);

        Distribution delay = getDistributionValue(crossingPoint, DELAY);
        Integer fixedDelay = getIntegerValue(crossingPoint, DELAY);

        Distribution leaving = getDistributionValue(crossingPoint, LEAVING);

        periodTransition.addFeature(
                StochasticTransitionFeature.newDeterministicInstance(BigDecimal.valueOf(period)));
        if (delay != null) {
            delayT.addFeature(DistributionMapper.map(delay));
        } else {
            delayT.addFeature(
                    StochasticTransitionFeature.newDeterministicInstance(String.valueOf(fixedDelay)));
        }
        trafficLightAnticipationT.addFeature(
                StochasticTransitionFeature.newDeterministicInstance(BigDecimal.valueOf(anticipation)));
        trafficLightAnticipationT.addFeature(new Priority(0));
        leavingT.addFeature(DistributionMapper.map(leaving));

        return net;
    }

    private Distribution getDistributionValue(Configurable target, Enum<?> delay) {
        DistributionProperty property = target.getProperty(delay.name(), DistributionProperty.class);
        if (property != null) {
            return property.getValue();
        }
        return null;
    }

    private Integer getIntegerValue(Configurable target, Enum<?> delay) {
        IntegerProperty property = target.getProperty(delay.name(), IntegerProperty.class);
        if (property != null) {
            return property.getValue();
        }
        return null;
    }

    private String getStringValue(Configurable target, Enum<?> delay) {
        StringProperty property = target.getProperty(delay.name(), StringProperty.class);
        if (property != null) {
            return property.getValue();
        }
        return null;
    }

    private NetworkPoint grabTramSourcePoint(NetworkMap map, PropertySource properties) {
        ChoiceProperty property = properties.findProperty(TRAM_SOURCE.name(), ChoiceProperty.class);
        if (property != null) {
            NetworkPoint result = map.findPoint(property.getValue());
            if (result == null) {
                throw new InvalidPropertyException("Selezionare un nodo di partenza valdo", createTramSourcePointProperty(map));
            }
            return result;
        } else {
            throw new InvalidPropertyException("Selezionare un nodo di partenza valdo", createTramSourcePointProperty(map));
        }
    }

    private Property createTramSourcePointProperty(NetworkMap map) {
        ChoicePropertyBuilder builder = new ChoicePropertyBuilder();
        builder
                .name(TRAM_SOURCE.name())
                .description("Punto di partenza del tram");
        for (NetworkPoint point : map.listPoints()) {
            builder.option(point.getUuid(), point.getUuid());
        }
        ChoiceProperty result = builder.build();
        result.setValid(false);
        return result;
    }

    public PetriNet getNet() {
        return net;
    }

    public Marking getMarking() {
        return marking;
    }

    public Integer getPeriod() {
        return period;
    }
}
