package it.tramways.analysis.availability;

import static it.tramways.analysis.availability.AvailabilityAnalysisProperties.PERIOD;

import it.tramways.commons.analysis.model.XYAnalysisResult;
import it.tramways.commons.analysis.model.XYPoint;
import it.tramways.projects.api.v1.model.IntegerProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.oristool.analyzer.log.AnalysisMonitor;
import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.petrinet.Marking;
import org.slf4j.LoggerFactory;

public class AvailabilityAnalysis {

    private static final int FPS = 1;
    private final PropertySource source;

    public AvailabilityAnalysis(PropertySource params) {
        this.source = params;
    }

    private static RegTransient createAnalysis(int period) {
        return RegTransient.builder()
            .greedyPolicy(BigDecimal.valueOf(period), BigDecimal.ZERO)
            .timeBound(BigDecimal.valueOf(period))
            .monitor(new MyAnalysisMonitor())
            .timeStep(BigDecimal.valueOf(1.0 / FPS))
            .build();
    }

    public XYAnalysisResult run() {
        CrossingPointPetriNetMapper mapper = new CrossingPointPetriNetMapper();

        int analysisTime = getIntegerParam(PERIOD.name(), 120);

        mapper.map(source);

        int steps = analysisTime * FPS + 1;

        double[] time = IntStream.range(0, steps)
            .asDoubleStream()
            .map(v -> v / FPS)
            .toArray();
        double[] av = IntStream.range(0, steps)
            .asDoubleStream()
            .map(v -> 1.0)
            .toArray();

        RegTransient analysis = createAnalysis(analysisTime);
        TransientSolution<DeterministicEnablingState, Marking> ssSolution = analysis
            .compute(mapper.getNet(), mapper.getMarking());

        TransientSolution<DeterministicEnablingState, RewardRate> reward = TransientSolution
            .computeRewards(false,
                ssSolution, "1-red");

        for (int tick = 0; tick < steps; tick++) {
            av[tick] *= reward.getSolution()[tick % (analysisTime * FPS)][0][0];
        }

        XYAnalysisResult result = new XYAnalysisResult();
        result.setResultType(XYAnalysisResult.class.getSimpleName());
        result.setxLabel("Time");
        result.setyLabel("Availability");

        List<XYPoint> points = new ArrayList<>();
        for (int i = 0; i < time.length; i++) {
            XYPoint point = new XYPoint();
            point.setX(BigDecimal.valueOf(time[i]));
            point.setY(BigDecimal.valueOf(av[i]));
            points.add(point);
        }
        result.setPoints(points);

        return result;
    }

    private int getIntegerParam(String name, int def) {
        IntegerProperty property = source.findProperty(name, IntegerProperty.class);
        if (property != null && property.getValue() != null) {
            return property.getValue();
        }
        return def;
    }

    private static class MyAnalysisMonitor implements AnalysisMonitor {

        @Override
        public void notifyMessage(String message) {
            LoggerFactory.getLogger(getClass()).info(message);
        }

        @Override
        public boolean interruptRequested() {
            return false;
        }
    }
}
