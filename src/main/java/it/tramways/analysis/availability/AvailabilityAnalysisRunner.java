package it.tramways.analysis.availability;

import it.tramways.analysis.api.v1.model.AnalysisStatus;
import it.tramways.analysis.api.v1.model.XYAnalysisResult;
import it.tramways.analysis.commons.kafka.messages.AnalysisResultMessage;
import it.tramways.analysis.commons.kafka.messages.AnalysisStatusMessage;
import it.tramways.analysis.commons.kafka.templates.AnalysisResultTemplate;
import it.tramways.analysis.commons.kafka.templates.AnalysisStatusTemplate;
import it.tramways.projects.api.ApiClient;
import it.tramways.projects.api.ApiException;
import it.tramways.projects.api.v1.ProjectsApi;
import it.tramways.projects.api.v1.model.Property;
import it.tramways.projects.api.v1.model.RoadMap;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static it.tramways.analysis.api.v1.model.AnalysisStatus.DONE;
import static it.tramways.analysis.api.v1.model.AnalysisStatus.IN_PROGRESS;

@Component
public class AvailabilityAnalysisRunner {

    private final ProjectsApi projectsApi;
    private final AnalysisResultTemplate resultTemplate;
    private final AnalysisStatusTemplate statusTemplate;

    public AvailabilityAnalysisRunner(
            AnalysisResultTemplate resultTemplate,
            AnalysisStatusTemplate statusTemplate
    ) {
        ApiClient client = new ApiClient();
        client.setBasePath("http://localhost:8761/tramways/rest");
        this.projectsApi = new ProjectsApi(client);
        this.resultTemplate = resultTemplate;
        this.statusTemplate = statusTemplate;
    }

    public void run(String projectId, String mapId, String analysisUuid, List<Property> properties) {
        try {
            statusTemplate.sendDefault(createAnalysisStatusMessage(analysisUuid, IN_PROGRESS));
            RoadMap map = projectsApi.getMap(projectId, mapId);
            DefaultPropertySource propertySource = new DefaultPropertySource();
            propertySource.addProperties(properties);
            AvailabilityAnalysis analysis = new AvailabilityAnalysis(map, propertySource);
            XYAnalysisResult result = analysis.run();
            AnalysisResultMessage message = new AnalysisResultMessage();
            message.setAnalysisUuid(analysisUuid);
            message.setBody(result);
            resultTemplate.sendDefault(message);
            statusTemplate.sendDefault(createAnalysisStatusMessage(analysisUuid, DONE));
        } catch (ApiException e) {
            LoggerFactory.getLogger(getClass()).error("Error", e);
        }
    }

    private AnalysisStatusMessage createAnalysisStatusMessage(String analysisUuid, AnalysisStatus status) {
        AnalysisStatusMessage msg = new AnalysisStatusMessage();
        msg.setAnalysisUuid(analysisUuid);
        msg.setBody(status);
        return msg;
    }

}
