package it.tramways.analysis.availability.inbound;

import it.tramways.analysis.api.v1.model.AnalysisRequest;
import it.tramways.analysis.commons.kafka.AnalysisKafkaTopicsUtility;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class InboundAvailabilityAnalysisKafkaConfiguration {

    private final ApplicationConfig applicationConfig;
    private final AvailabilityAnalysisRequestListener requestListener;

    @Autowired
    public InboundAvailabilityAnalysisKafkaConfiguration(
            ApplicationConfig applicationConfig,
            AvailabilityAnalysisRequestListener requestListener
    ) {
        this.applicationConfig = applicationConfig;
        this.requestListener = requestListener;
    }

    @Bean
    public NewTopic analysisRequestsTopic() {
        return TopicBuilder.name(getAnalysisLaunchTopic()).build();
    }

    private String getAnalysisLaunchTopic() {
        return AnalysisKafkaTopicsUtility.getAnalysisLaunchTopic(applicationConfig.getName());
    }

    @Bean
    public KafkaMessageListenerContainer<Integer, AnalysisRequest> messageListenerContainer() {
        ContainerProperties containerProps = new ContainerProperties(getAnalysisLaunchTopic());
        containerProps.setMessageListener(requestListener);
        containerProps.setGroupId(applicationConfig.getName());

        Map<String, Object> props = consumerProps();
        DefaultKafkaConsumerFactory<Integer, AnalysisRequest> cf =
                new DefaultKafkaConsumerFactory<>(props, new IntegerDeserializer(), new JsonDeserializer<>(AnalysisRequest.class));
        return new KafkaMessageListenerContainer<>(cf, containerProps);
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return props;
    }

}
