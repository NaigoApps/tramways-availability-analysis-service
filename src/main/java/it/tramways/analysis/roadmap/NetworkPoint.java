package it.tramways.analysis.roadmap;

import it.tramways.analysis.AbstractConfigurable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NetworkPoint extends AbstractConfigurable implements Node<LaneSegment> {

    public static final LaneSegment VOID = new LaneSegment();

    private Map<LaneSegment, Set<LaneSegmentLink>> links;

    public NetworkPoint() {
        links = new HashMap<>();
    }

    public Map<LaneSegment, Set<LaneSegmentLink>> getLinks() {
        return links;
    }

    public void setLinks(Map<LaneSegment, Set<LaneSegmentLink>> links) {
        this.links = links;
    }

    public Set<LaneSegmentLink> getLinks(LaneSegment segment) {
        return links.get(segment);
    }

    public LaneSegment startLane(LaneSegment target) {
        LaneSegmentLink link = new LaneSegmentLink();
        link.setSource(VOID);
        link.setDestination(target);
        target.setSource(this);
        links.computeIfAbsent(VOID, l -> new HashSet<>()).add(link);
        return target;
    }

    public LaneSegment createLink(LaneSegment from, LaneSegment to) {
        LaneSegmentLink link = new LaneSegmentLink();
        link.setSource(from);
        link.setDestination(to);

        from.setDestination(this);
        to.setSource(this);

        links.computeIfAbsent(from, l -> new HashSet<>()).add(link);

        return to;
    }

    public void endLane(LaneSegment target) {
        LaneSegmentLink link = new LaneSegmentLink();
        link.setSource(target);
        link.setDestination(VOID);
        target.setDestination(this);
        links.computeIfAbsent(target, l -> new HashSet<>()).add(link);
    }

    public void addLink(LaneSegment source, LaneSegment destination) {
        LaneSegmentLink link = new LaneSegmentLink();
        link.setSource(source);
        source.setDestination(this);
        link.setDestination(destination);
        destination.setSource(this);
        links.computeIfAbsent(source, l -> new HashSet<>()).add(link);
    }

    @Override
    public List<LaneSegment> getSources() {
        Set<LaneSegment> sources = new HashSet<>(links.keySet());
        sources.remove(VOID);
        return new ArrayList<>(sources);
    }

    @Override
    public List<LaneSegment> getDestinations() {
        return new ArrayList<>(links.values()).stream()
                .flatMap(Collection::stream)
                .map(LaneSegmentLink::getDestination)
                .distinct()
                .filter(segment -> !segment.equals(VOID))
                .collect(Collectors.toList());
    }
}
