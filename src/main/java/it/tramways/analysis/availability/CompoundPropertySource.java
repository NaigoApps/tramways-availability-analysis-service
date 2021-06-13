package it.tramways.analysis.availability;

import it.tramways.projects.api.v1.model.Property;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CompoundPropertySource implements PropertySource {

    private Set<PropertySource> sources;

    public CompoundPropertySource() {
        this.sources = new HashSet<>();
    }

    public CompoundPropertySource addSource(PropertySource source) {
        this.sources.add(source);
        return this;
    }

    public CompoundPropertySource addSources(Collection<PropertySource> sources) {
        this.sources.addAll(sources);
        return this;
    }

    @Override
    public Property findProperty(String name) {
        return sources.stream()
                .map(source -> source.findProperty(name))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public <T extends Property> T findProperty(String name, Class<T> type) {
        return sources.stream()
                .map(source -> source.findProperty(name, type))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
