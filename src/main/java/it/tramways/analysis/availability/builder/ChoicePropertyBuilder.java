package it.tramways.analysis.availability.builder;

import it.tramways.projects.api.v1.model.ChoiceElement;
import it.tramways.projects.api.v1.model.ChoiceProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChoicePropertyBuilder extends
    BasePropertyBuilder<ChoicePropertyBuilder, ChoiceProperty> {

    private final List<ChoiceElement> options;

    public ChoicePropertyBuilder() {
        options = new ArrayList<>();
    }

    public <O> ChoicePropertyBuilder options(Collection<O> options,
        Function<O, ChoiceElement> mapper) {
        this.options.addAll(options.stream().map(mapper).collect(Collectors.toList()));
        return this;
    }

    public ChoicePropertyBuilder option(String id, String label) {
        ChoiceElement element = new ChoiceElement();
        element.setId(id);
        element.setLabel(label);
        options.add(element);
        return this;
    }

    public ChoicePropertyBuilder option(ChoiceElement option) {
        options.add(option);
        return this;
    }

    public ChoiceElementBuilder option() {
        return new ChoiceElementBuilder(this);
    }

    @Override
    protected String getPropertyType() {
        return ChoiceProperty.class.getSimpleName();
    }

    @Override
    protected void buildImpl(ChoiceProperty result) {
        result.setChoices(options);
    }

    @Override
    protected ChoiceProperty createProperty() {
        return new ChoiceProperty();
    }

    @Override
    protected ChoicePropertyBuilder getBuilder() {
        return this;
    }

}
