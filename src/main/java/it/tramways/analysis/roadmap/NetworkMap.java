package it.tramways.analysis.roadmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NetworkMap {

    private final List<NetworkPoint> points;

    public NetworkMap() {
        this.points = new ArrayList<>();
    }

    public List<NetworkPoint> listPoints() {
        return new ArrayList<>(points);
    }

    public void addPoints(Collection<NetworkPoint> points) {
        this.points.addAll(points);
    }

    public void addPoints(NetworkPoint... points) {
        this.points.addAll(Arrays.asList(points));
    }

    public Collection<LaneSegment> listLanes() {
        return points.stream()
            .flatMap(networkPoint -> networkPoint.getLinks().keySet().stream())
            .collect(Collectors.toSet());
    }

    public Collection<LaneSegmentLink> listLinks() {
        return points.stream()
            .flatMap(networkPoint -> networkPoint.getLinks().values().stream())
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    public NetworkPoint findPoint(String id) {
        return points.stream()
            .filter(point -> point.getUuid().equals(id))
            .findFirst()
            .orElse(null);
    }
}
