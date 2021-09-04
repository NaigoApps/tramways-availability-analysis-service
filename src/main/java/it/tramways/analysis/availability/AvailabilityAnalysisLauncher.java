package it.tramways.analysis.availability;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityAnalysisLauncher {

    public void launch(){
        AvailabilityAnalysis analysis = new AvailabilityAnalysis(new DefaultPropertySource());
        analysis.run();
    }

}
