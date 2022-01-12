package it.tramways.analysis.availability.inbound;

import it.tramways.analysis.api.v1.model.AnalysisRequest;
import it.tramways.analysis.api.v1.model.AnalysisResult;
import it.tramways.analysis.api.v1.model.StringAnalysisResult;
import it.tramways.analysis.availability.AvailabilityAnalysisLauncher;
import it.tramways.analysis.commons.kafka.AnalysisKafkaTopics;
import it.tramways.analysis.commons.kafka.AnalysisKafkaTopicsUtility;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityAnalysisRequestListener implements MessageListener<Integer, Object> {

    private final AvailabilityAnalysisLauncher launcher;
    private final KafkaTemplate<Integer, AnalysisResult> resultNotifier;

    @Autowired
    public AvailabilityAnalysisRequestListener(
            AvailabilityAnalysisLauncher launcher,
            KafkaTemplate<Integer, AnalysisResult> resultNotifier
    ) {
        this.launcher = launcher;
        this.resultNotifier = resultNotifier;
    }

    @Override
    public void onMessage(ConsumerRecord<Integer, Object> record) {
        Object value = record.value();
        //TODO
        launcher.launch();
        StringAnalysisResult stringAnalysisResult = new StringAnalysisResult();
        stringAnalysisResult.setMessage("Test analysis completed :D");
        resultNotifier.send(AnalysisKafkaTopicsUtility.getAnalysisResultTopic(), stringAnalysisResult);
    }

}
