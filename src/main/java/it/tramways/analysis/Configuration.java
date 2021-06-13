package it.tramways.analysis;

import it.tramways.projects.api.v1.model.Property;
import java.util.List;

public class Configuration {

    private String category;
    private String name;
    private List<Property> properties;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
