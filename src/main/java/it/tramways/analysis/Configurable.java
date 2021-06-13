package it.tramways.analysis;

import it.tramways.projects.api.v1.model.Property;
import java.util.List;

public interface Configurable {

    void apply(Configuration conf);

    void apply(List<Property> properties);

    List<Property> listProperties();

    Property getProperty(String name);

    <T extends Property> T getProperty(String name, Class<T> propertyClass);

}
