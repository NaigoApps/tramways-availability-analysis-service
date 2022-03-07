package it.tramways.analysis.availability.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.tramways.analysis.api.v1.model.AnalysisRequest;
import it.tramways.analysis.availability.AvailabilityAnalysisRunner;
import it.tramways.analysis.commons.kafka.messages.AnalysisRequestMessage;
import it.tramways.projects.api.v1.model.Property;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AvailabilityAnalysisRequestListener implements MessageListener<Integer, AnalysisRequestMessage> {

    private final AvailabilityAnalysisRunner launcher;

    @Autowired
    public AvailabilityAnalysisRequestListener(
            AvailabilityAnalysisRunner launcher
    ) {
        this.launcher = launcher;
    }

    @Override
    public void onMessage(ConsumerRecord<Integer, AnalysisRequestMessage> record) {
        new Thread(() -> {
            String analysisUuid = record.value().getAnalysisUuid();
            AnalysisRequest request = record.value().getBody();

            launcher.run(request.getProjectId(), request.getMapId(), analysisUuid, convert(request.getParameters()));
        }).start();
    }

    private List<Property> convert(List<it.tramways.analysis.api.v1.model.Property> parameters) {
        return parameters.stream().map(this::convert).collect(Collectors.toList());
    }

    private Property convert(it.tramways.analysis.api.v1.model.Property property) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String serialized = mapper.writeValueAsString(property);
            return mapper.readValue(serialized, Property.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
