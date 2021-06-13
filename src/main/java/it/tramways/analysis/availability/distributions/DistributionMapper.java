package it.tramways.analysis.availability.distributions;

import it.tramways.projects.api.v1.model.Distribution;
import it.tramways.projects.api.v1.model.ExponentialDistribution;
import it.tramways.projects.api.v1.model.UniformDistribution;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.TransitionFeature;

public class DistributionMapper {

    private DistributionMapper() {

    }

    public static StochasticTransitionFeature map(UniformDistribution u) {
        return StochasticTransitionFeature.newUniformInstance(u.getLeft(), u.getRight());
    }

    public static StochasticTransitionFeature map(ExponentialDistribution d) {
        return StochasticTransitionFeature.newExponentialInstance(d.getLambda());
    }

    public static TransitionFeature map(Distribution distribution) {
        if (distribution instanceof ExponentialDistribution) {
            return map((ExponentialDistribution) distribution);
        } else if (distribution instanceof UniformDistribution) {
            return map((UniformDistribution) distribution);
        }
        return null;
    }
}
