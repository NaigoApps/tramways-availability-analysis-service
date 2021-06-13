package it.tramways.analysis.availability;

import it.tramways.projects.api.v1.model.Property;
import java.util.List;

public interface PropertiesCollector {

    void collectProperty(Property property);

    List<Property> listProperties();

}
