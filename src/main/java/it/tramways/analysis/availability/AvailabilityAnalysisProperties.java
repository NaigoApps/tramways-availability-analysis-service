package it.tramways.analysis.availability;

public enum AvailabilityAnalysisProperties {
    LANE_TYPE("Lane type"),
    PERIOD("Period"),
    DELAY("Delay"),
    ANTICIPATION("Anticipation"),
    LEAVING("Leaving"),

    TRAM_SOURCE("Tram source"),
    CROSSING_POINT("Crossing point"),
    TRAM_DESTINATION("Tram destination");

    private final String description;

    AvailabilityAnalysisProperties(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
