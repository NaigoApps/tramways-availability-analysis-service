package it.tramways.analysis.availability;

import it.tramways.analysis.api.v1.model.XYAnalysisResult;
import it.tramways.analysis.commons.DefaultAnalysisProperties;
import it.tramways.analysis.roadmap.LaneSegment;
import it.tramways.analysis.roadmap.NetworkMap;
import it.tramways.analysis.roadmap.NetworkPoint;
import it.tramways.projects.api.v1.model.Property;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AvailabilityAnalysisTest {

    private NetworkPoint t1;
    private NetworkPoint cp;
    private NetworkPoint t2;

    @Test
    public void testEmpty() {
        PropertiesCollector propertiesCollector = new DefaultPropertiesCollector();
        MessagesCollector messagesCollector = new DefaultMessagesCollector();

        NetworkMap map = new NetworkMap();

        AvailabilityAnalysisType type = new AvailabilityAnalysisType();
        type.prepareAnalysis(map, Collections.emptyList(), propertiesCollector, messagesCollector);

        assertEquals(DefaultAnalysisProperties.ANALYSIS_NAME.name(), propertiesCollector.listProperties().iterator().next().getName());
    }

    @Test
    public void testOk() {
        PropertiesCollector propertiesCollector = new DefaultPropertiesCollector();
        MessagesCollector messagesCollector = new DefaultMessagesCollector();

        NetworkMap map = createRoadmap();
        List<Property> properties = createProperties();

        AvailabilityAnalysisType type = new AvailabilityAnalysisType();
        type.prepareAnalysis(map, properties, propertiesCollector, messagesCollector);

        AvailabilityAnalysis analysis = type.createAnalysis(map, propertiesCollector.listProperties());
        XYAnalysisResult result = analysis.run();

        assertNotNull(result);
    }

    private List<Property> createProperties() {
        List<Property> result = new ArrayList<>();
        result.add(Properties.stringProperty(DefaultAnalysisProperties.ANALYSIS_NAME.name(), "Test analysis"));
        result.add(Properties.choiceProperty(AvailabilityAnalysisProperties.TRAM_SOURCE.name(), t1.getUuid()));
        result.add(Properties.choiceProperty(AvailabilityAnalysisProperties.CROSSING_POINT.name(), cp.getUuid()));
        result.add(Properties.choiceProperty(AvailabilityAnalysisProperties.TRAM_DESTINATION.name(), t2.getUuid()));

        result.add(Properties.intProperty(AvailabilityAnalysisProperties.PERIOD.name(), 60));
        result.add(Properties.uniformDistributionProperty(AvailabilityAnalysisProperties.DELAY.name(), BigDecimal.ZERO, BigDecimal.valueOf(30)));
        result.add(Properties.intProperty(AvailabilityAnalysisProperties.ANTICIPATION.name(), 0));
        result.add(Properties.uniformDistributionProperty(AvailabilityAnalysisProperties.LEAVING.name(), BigDecimal.valueOf(5), BigDecimal.valueOf(10)));
        return result;
    }

    private NetworkMap createRoadmap() {
        NetworkMap map = new NetworkMap();

        t1 = new NetworkPoint();
        cp = new NetworkPoint();
        t2 = new NetworkPoint();

        LaneSegment t1cp = new LaneSegment();
        LaneSegment cpt2 = new LaneSegment();

        t1.startLane(t1cp).linkTo(cp).createLink(t1cp, cpt2).linkTo(t2).endLane(cpt2);

        map.addPoints(t1, cp, t2);
        return map;
    }

}
