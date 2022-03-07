package it.tramways.analysis.availability;

import it.tramways.projects.api.v1.model.Property;

public class InvalidPropertyException extends RuntimeException {

    private final String message;
    private final Property suggestion;

    public InvalidPropertyException(String message, Property suggestion) {
        this.message = message;
        this.suggestion = suggestion;
    }
}
