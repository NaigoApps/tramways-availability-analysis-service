package it.tramways.analysis;

import it.tramways.projects.api.v1.model.Property;
import java.util.ArrayList;
import java.util.List;

public class AbstractConfigurable extends AbstractIdentifiable implements Configurable {

    private final List<Property> properties;

    public AbstractConfigurable() {
        properties = new ArrayList<>();
    }

    public void apply(Configuration conf) {
        for (Property prop : conf.getProperties()) {
            applyImpl(prop);
        }
    }

    @Override
    public void apply(List<Property> properties) {
        for (Property prop : properties) {
            applyImpl(prop);
        }
    }

    public void apply(Property prop) {
        applyImpl(prop);
    }

    private void applyImpl(Property prop) {
        properties.stream()
                .filter(property -> property.getName().equalsIgnoreCase(prop.getName()))
                .findFirst()
                .ifPresent(properties::remove);
        properties.add(prop);
    }

    @Override
    public List<Property> listProperties() {
        return new ArrayList<>(properties);
    }

    @Override
    public Property getProperty(String name) {
        return properties.stream().filter(property -> property.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public <T extends Property> T getProperty(String name, Class<T> propertyClass) {
        return properties.stream()
                .filter(property -> property.getName().equals(name))
                .filter(propertyClass::isInstance)
                .map(propertyClass::cast)
                .findFirst()
                .orElse(null);
    }
}
