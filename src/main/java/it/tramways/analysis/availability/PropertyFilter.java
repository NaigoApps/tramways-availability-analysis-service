package it.tramways.analysis.availability;

import it.tramways.projects.api.v1.model.Property;

class PropertyFilter<T extends Property> {

    String name;
    Class<T> type;

    public PropertyFilter(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public static PropertyFilter<Property> of(String name) {
        return new PropertyFilter<>(name, null);
    }

    public static <T extends Property> PropertyFilter<T> of(String name, Class<T> type) {
        return new PropertyFilter<>(name, type);
    }

    public static <T extends Property> PropertyFilter<T> of(Property property,
        Class<T> propertyClass) {
        return new PropertyFilter<>(property.getName(), propertyClass);
    }

    public boolean matches(PropertyFilter<?> filter) {
        if (filter.name != null) {
            if (filter.type != null) {
                return filter.name.equals(name) && filter.type.equals(type);
            } else {
                return filter.name.equals(name);
            }
        } else {
            if (filter.type != null) {
                return filter.type.equals(type);
            } else {
                return true;
            }
        }
    }
}
