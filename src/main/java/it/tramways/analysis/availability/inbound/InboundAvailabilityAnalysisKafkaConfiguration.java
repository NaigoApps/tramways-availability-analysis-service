package it.tramways.analysis.availability.inbound;

import it.tramways.analysis.commons.kafka.AnalysisKafkaTopicsUtility;
import it.tramways.analysis.commons.kafka.messages.AnalysisRequestMessage;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class InboundAvailabilityAnalysisKafkaConfiguration {

    private final String applicationName;
    private final AvailabilityAnalysisRequestListener requestListener;

    @Autowired
    public InboundAvailabilityAnalysisKafkaConfiguration(
            @Value("${spring.application.name}") String applicationName,
            AvailabilityAnalysisRequestListener requestListener
    ) {
        this.applicationName = applicationName;
        this.requestListener = requestListener;
    }

    @Bean
    public NewTopic analysisRequestsTopic() {
        return TopicBuilder.name(getAnalysisLaunchTopic()).build();
    }

    private String getAnalysisLaunchTopic() {
        return AnalysisKafkaTopicsUtility.getAnalysisLaunchTopic(applicationName);
    }

    @Bean
    public KafkaMessageListenerContainer<Integer, AnalysisRequestMessage> messageListenerContainer() {
        ContainerProperties containerProps = new ContainerProperties(getAnalysisLaunchTopic());
        containerProps.setMessageListener(requestListener);
        containerProps.setGroupId(applicationName);

        Map<String, Object> props = consumerProps();
        DefaultKafkaConsumerFactory<Integer, AnalysisRequestMessage> cf =
                new DefaultKafkaConsumerFactory<>(props, new IntegerDeserializer(), createDeserializer(AnalysisRequestMessage.class));
        return new KafkaMessageListenerContainer<>(cf, containerProps);
    }

    private <T> ErrorHandlingDeserializer<T> createDeserializer(Class<T> targetClass) {
        return new ErrorHandlingDeserializer<>(new JsonDeserializer<>(targetClass));
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://192.168.1.249:9092");
        return props;
    }

}
