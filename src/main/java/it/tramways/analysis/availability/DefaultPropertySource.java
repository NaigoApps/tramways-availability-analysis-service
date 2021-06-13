package it.tramways.analysis.availability;

import it.tramways.projects.api.v1.model.Property;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultPropertySource implements PropertySource {

    private final List<Property> properties;

    public DefaultPropertySource() {
        this.properties = new ArrayList<>();
    }

    public DefaultPropertySource(Collection<Property> properties) {
        this.properties = new ArrayList<>(properties);
    }

    public void addProperty(Property property) {
        this.properties.add(property);
    }

    public void addProperties(Collection<Property> properties) {
        this.properties.addAll(properties);
    }

    @Override
    public Property findProperty(String name) {
        return properties.stream()
            .filter(property -> PropertyFilter
                .of(property, property.getClass()).matches(PropertyFilter.of(name, null)))
            .findFirst()
            .orElse(null);
    }

    @Override
    public <T extends Property> T findProperty(String name, Class<T> type) {
        return properties.stream()
            .filter(property -> PropertyFilter.of(property, property.getClass())
                .matches(PropertyFilter.of(name, type)))
            .map(type::cast)
            .findFirst()
            .orElse(null);
    }
}
