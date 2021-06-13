package it.tramways.analysis.availability.builder;

import it.tramways.projects.api.v1.model.Property;

public abstract class BasePropertyBuilder<B extends BasePropertyBuilder<B, T>, T extends Property> {

    private String name;
    private String description;

    public B name(Enum<?> name) {
        this.name = name.name();
        return getBuilder();
    }

    public B name(String name) {
        this.name = name;
        return getBuilder();
    }

    public B description(String description) {
        this.description = description;
        return getBuilder();
    }


    public T build() {
        T result = createProperty();
        result.setName(name);
        result.setDescription(description);
        result.setPropertyType(getPropertyType());
        buildImpl(result);
        return result;
    }

    protected abstract String getPropertyType();

    protected abstract void buildImpl(T result);

    protected abstract T createProperty();

    protected abstract B getBuilder();

}
