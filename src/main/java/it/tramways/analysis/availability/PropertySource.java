package it.tramways.analysis.availability;


import it.tramways.projects.api.v1.model.Property;

public interface PropertySource {

    Property findProperty(String name);

    <T extends Property> T findProperty(String name, Class<T> type);
}
