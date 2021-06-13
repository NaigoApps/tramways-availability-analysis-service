package it.tramways.analysis.roadmap;

import java.util.Set;

public class SensorPriorityManager extends PriorityManager {

    private Set<LaneSegment> activators;

    public Set<LaneSegment> getActivators() {
        return activators;
    }

    public void setActivators(Set<LaneSegment> activators) {
        this.activators = activators;
    }

}
