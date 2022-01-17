package it.tramways.analysis.availability.inbound;

import it.tramways.analysis.api.v1.model.AnalysisResult;
import it.tramways.analysis.api.v1.model.AnalysisStatus;
import it.tramways.analysis.api.v1.model.StringAnalysisResult;
import it.tramways.analysis.availability.AvailabilityAnalysisLauncher;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityAnalysisRequestListener implements MessageListener<Integer, Object> {

    private final AvailabilityAnalysisLauncher launcher;
    private final KafkaTemplate<Integer, AnalysisStatus> statusNotifier;
    private final KafkaTemplate<Integer, AnalysisResult> resultNotifier;

    @Autowired
    public AvailabilityAnalysisRequestListener(
            AvailabilityAnalysisLauncher launcher,
            KafkaTemplate<Integer, AnalysisStatus> statusNotifier,
            KafkaTemplate<Integer, AnalysisResult> resultNotifier
    ) {
        this.launcher = launcher;
        this.statusNotifier = statusNotifier;
        this.resultNotifier = resultNotifier;
    }

    @Override
    public void onMessage(ConsumerRecord<Integer, Object> record) {
        new Thread(() -> {
            try {
                statusNotifier.sendDefault(AnalysisStatus.IN_PROGRESS);
                Thread.sleep(5000);
                // TODO
                launcher.launch();
                StringAnalysisResult stringAnalysisResult = new StringAnalysisResult();
                stringAnalysisResult.setMessage("Test analysis completed :D");
                resultNotifier.sendDefault(stringAnalysisResult);
                Thread.sleep(5000);
                statusNotifier.sendDefault(AnalysisStatus.DONE);
            } catch (InterruptedException e) {
                LoggerFactory.getLogger(getClass()).error("Error", e);
            }
        }).start();
    }

}
