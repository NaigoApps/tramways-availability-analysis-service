package it.tramways.analysis.roadmap;

import it.tramways.analysis.AbstractConfigurable;
import java.util.List;

/*
 * Example properties: weight of the link, crossingTime, ...
 */
public class LaneSegmentLink extends AbstractConfigurable {

    private LaneSegment source;
    private LaneSegment destination;
    private List<PriorityManager> priorityManagers;

    public LaneSegmentLink() {
        //Nothing to do
    }

    public LaneSegment getSource() {
        return source;
    }

    public LaneSegment getDestination() {
        return destination;
    }

    public void setSource(LaneSegment source) {
        this.source = source;
    }

    public void setDestination(LaneSegment destination) {
        this.destination = destination;
    }

}
