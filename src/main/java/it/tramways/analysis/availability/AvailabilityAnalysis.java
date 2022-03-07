package it.tramways.analysis.availability;

import it.tramways.analysis.api.v1.model.XYAnalysisResult;
import it.tramways.analysis.api.v1.model.XYPoint;
import it.tramways.projects.api.v1.model.RoadMap;
import org.oristool.analyzer.log.AnalysisMonitor;
import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.petrinet.Marking;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AvailabilityAnalysis {

    private static final int FPS = 1;
    private final RoadMap map;
    private final PropertySource propertySource;

    public AvailabilityAnalysis(RoadMap map, PropertySource propertySource) {
        this.map = map;
        this.propertySource = propertySource;
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

        Integer analysisTime = mapper.getPeriod();
        if (analysisTime == null) {
            analysisTime = 120;
        }

        mapper.map(map, propertySource);

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
