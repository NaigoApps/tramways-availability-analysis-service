package it.tramways.analysis.roadmap;

import it.tramways.analysis.Configurable;
import it.tramways.analysis.availability.AvailabilityAnalysisProperties;
import it.tramways.projects.api.v1.model.ChoiceProperty;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RoadMapUtilities {

    private RoadMapUtilities() {
    }

    public static boolean linked(NetworkPoint s, NetworkPoint d) {
        return s.getDestinations().stream()
            .anyMatch(laneSegment -> d.equals(laneSegment.getDestination()));
    }

    public static List<NetworkPoint> findTramDestinations(List<NetworkPoint> points) {
        return findDestinations(points, LaneType.TRAM);
    }

    public static List<NetworkPoint> findCarDestinations(List<NetworkPoint> points) {
        return findDestinations(points, LaneType.CAR);
    }

    private static List<NetworkPoint> findDestinations(List<NetworkPoint> points, LaneType type) {
        return points.stream()
            .filter(point -> point.getSources().size() == 1)
            .filter(
                point -> hasChoiceProperty(AvailabilityAnalysisProperties.LANE_TYPE, type.name())
                    .test(point.getSources().iterator().next()))
            .collect(Collectors.toList());
    }

    public static Predicate<Configurable> hasChoiceProperty(AvailabilityAnalysisProperties name,
        String value) {
        return configurable -> configurable.listProperties().stream()
            .filter(property -> property.getName().equals(name.name()))
            .filter(property -> property instanceof ChoiceProperty)
            .map(ChoiceProperty.class::cast)
            .anyMatch(choiceProperty -> choiceProperty.getValue().equals(value));
    }

    public static List<NetworkPoint> findTramSources(List<NetworkPoint> points) {
        return findSources(points, LaneType.TRAM);
    }

    public static List<NetworkPoint> findCarSources(List<NetworkPoint> points) {
        return findSources(points, LaneType.CAR);
    }

    private static List<NetworkPoint> findSources(List<NetworkPoint> points, LaneType type) {
        return points.stream()
            .filter(point -> point.getDestinations().size() == 1)
            .filter(
                point -> hasChoiceProperty(AvailabilityAnalysisProperties.LANE_TYPE, type.name())
                    .test(point.getDestinations().iterator().next()))
            .collect(Collectors.toList());
    }

    public static NetworkPoint findNetworkPoint(NetworkMap map, String id) {
        return map.listPoints().stream()
            .filter(point -> id.equals(point.getUuid()))
            .findFirst()
            .orElse(null);
    }
}
