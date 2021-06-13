package it.tramways.analysis.availability;

import it.tramways.projects.api.v1.model.Property;
import java.util.ArrayList;
import java.util.List;

public class DefaultPropertiesCollector implements PropertiesCollector {

    private final List<Property> properties;

    public DefaultPropertiesCollector() {
        properties = new ArrayList<>();
    }

    @Override
    public void collectProperty(Property property) {
        for (int i = 0; i < properties.size(); i++) {
            if (properties.get(i).getName().equals(property.getName())) {
                properties.set(i, property);
                return;
            }
        }
        properties.add(property);
    }

    @Override
    public List<Property> listProperties() {
        return new ArrayList<>(properties);
    }
}
