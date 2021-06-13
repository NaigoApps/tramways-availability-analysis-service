package it.tramways.analysis.availability.builder;

import it.tramways.projects.api.v1.model.StringProperty;

public class PropertyBuilder {

    public static ChoicePropertyBuilder choice() {
        return new ChoicePropertyBuilder();
    }

    public static ChoicePropertyBuilder choice(Enum<?> name, String description) {
        return choice(name.name(), description);
    }

    public static ChoicePropertyBuilder choice(String name, String description) {
        ChoicePropertyBuilder builder = new ChoicePropertyBuilder();
        builder.name(name);
        builder.description(description);
        return builder;
    }

    public static StringProperty string(Enum<?> name, String description) {
        return string(name.name(), description);
    }

    public static StringProperty string(String name, String description) {
        StringProperty result = new StringProperty();
        result.setName(name);
        result.setDescription(description);
        result.setPropertyType(StringProperty.class.getSimpleName());
        return result;
    }
}
