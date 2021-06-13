package it.tramways.analysis.availability.inbound;

import it.tramways.analysis.availability.AvailabilityAnalysis;
import it.tramways.analysis.availability.AvailabilityAnalysisType;
import it.tramways.analysis.availability.DefaultMessagesCollector;
import it.tramways.analysis.availability.DefaultPropertiesCollector;
import it.tramways.analysis.availability.DefaultPropertySource;
import it.tramways.analysis.availability.MessagesCollector;
import it.tramways.analysis.availability.PropertiesCollector;
import it.tramways.analysis.roadmap.NetworkMap;
import it.tramways.commons.analysis.model.XYAnalysisResult;
import it.tramways.projects.api.ApiException;
import it.tramways.projects.api.v1.ProjectsApi;
import it.tramways.projects.api.v1.model.Property;
import it.tramways.projects.api.v1.model.RoadMap;
import java.util.Collections;
import java.util.List;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

public class AnalysisKafkaListener {

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("analysis").build();
    }

    @KafkaListener(id = "myId", topics = "analysis")
    public void listen(String input) throws ApiException {
        ProjectsApi api = new ProjectsApi();
        RoadMap map = api.getMap("a", "b");
        AvailabilityAnalysis analysis = new AvailabilityAnalysis(new DefaultPropertySource());
        analysis.run();
    }

    public static void main(String[] args) {
        PropertiesCollector propertiesCollector = new DefaultPropertiesCollector();
        MessagesCollector messagesCollector = new DefaultMessagesCollector();
        AvailabilityAnalysisType type = new AvailabilityAnalysisType();
        RoadMap map = new RoadMap();
        map.setPoints(Collections.emptyList());
        map.setLanes(Collections.emptyList());
        type.prepareAnalysis(map, Collections.emptyList(), propertiesCollector, messagesCollector);
        if(messagesCollector.listMessages().isEmpty() && propertiesCollector.listProperties().isEmpty()) {
            AvailabilityAnalysis analysis = type
                .createAnalysis(new NetworkMap(), Collections.emptyList());
            XYAnalysisResult result = analysis.run();
            System.out.println(result);
        }else{
            messagesCollector.listMessages().forEach(System.out::println);
            propertiesCollector.listProperties().forEach(System.out::println);
        }
    }

    @Autowired
    private KafkaTemplate<String, String> template;

    public void send() {
        template.send("analysis", "data");
    }
}
