package it.tramways.analysis.roadmap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import it.tramways.projects.api.v1.model.CrossingLink;
import it.tramways.projects.api.v1.model.Lane;
import it.tramways.projects.api.v1.model.RelevantPoint;
import it.tramways.projects.api.v1.model.RoadMap;
import org.apache.commons.lang3.StringUtils;

public class RoadMapNetworkMapper {

    private final RoadMap mapContent;

    private Map<RelevantPoint, NetworkPoint> pointsMap;

    private Map<String, LaneSegment> lanesMap;

    public RoadMapNetworkMapper(RoadMap mapContent) {
        this.mapContent = mapContent;
    }

    public NetworkMap map(){
        pointsMap = new HashMap<>();
        lanesMap = new HashMap<>();
        List<NetworkPoint> points = mapPoints();
        NetworkMap map = new NetworkMap();
        map.addPoints(points);
        return map;
    }

    private List<NetworkPoint> mapPoints() {
        return mapContent.getPoints().stream()
                .map(this::mapPoint)
                .collect(Collectors.toList());
    }

    private NetworkPoint mapPoint(RelevantPoint relevantPoint) {
        return pointsMap.computeIfAbsent(relevantPoint, point -> {
            NetworkPoint networkPoint = new NetworkPoint();
            networkPoint.setUuid(relevantPoint.getId());
            networkPoint.apply(relevantPoint.getProps());
            List<LaneSegment> leavingLanes = findLeavingLanes(relevantPoint);
            List<LaneSegment> incomingLanes = findIncomingLanes(relevantPoint);
            for(LaneSegment lane : leavingLanes){
                lane.setSource(networkPoint);
            }
            for(LaneSegment lane : incomingLanes){
                lane.setDestination(networkPoint);
            }
            Map<LaneSegment, Set<LaneSegmentLink>> linksMap = new HashMap<>();
            for (CrossingLink link : point.getLinks()) {
                LaneSegmentLink segmentLink = new LaneSegmentLink();
                segmentLink.apply(link.getProps());
                segmentLink.setSource(findOrCreateLane(link.getSourceId()));
                segmentLink.setDestination(findOrCreateLane(link.getDestinationId()));
                Set<LaneSegmentLink> linksSet = linksMap.computeIfAbsent(findOrCreateLane(link.getSourceId()), l -> new HashSet<>());
                linksSet.add(segmentLink);
            }
            networkPoint.setLinks(linksMap);

            return networkPoint;
        });
    }

    private List<LaneSegment> findLeavingLanes(RelevantPoint relevantPoint) {
        return mapContent.getLanes().stream()
                .filter(lane -> lane.getSourceId().equalsIgnoreCase(relevantPoint.getId()))
                .map(this::findOrCreateLane)
                .collect(Collectors.toList());
    }

    private List<LaneSegment> findIncomingLanes(RelevantPoint relevantPoint) {
        return mapContent.getLanes().stream()
                .filter(lane -> lane.getDestinationId().equalsIgnoreCase(relevantPoint.getId()))
                .map(this::findOrCreateLane)
                .collect(Collectors.toList());
    }

    private LaneSegment findOrCreateLane(Lane lane) {
        return findOrCreateLane(lane.getId());
    }

    private LaneSegment findOrCreateLane(String laneId) {
        if(StringUtils.isEmpty(laneId)){
            return NetworkPoint.VOID;
        }
        return lanesMap.computeIfAbsent(laneId, uuid -> {
            Lane targetLane = mapContent.getLanes().stream()
                    .filter(lane -> uuid.equals(lane.getId()))
                    .findFirst()
                    .orElse(null);
            if (targetLane != null){
                LaneSegment segment = new LaneSegment();
                segment.setUuid(uuid);
                segment.apply(targetLane.getProps());
                return segment;
            }
            return NetworkPoint.VOID;
        });
    }
}
