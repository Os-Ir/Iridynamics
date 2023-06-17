package com.atodium.iridynamics.api.heat;

import com.atodium.iridynamics.api.material.Phase;
import com.atodium.iridynamics.api.util.data.MonotonicMap;

import java.util.EnumMap;

public abstract class SubMaterialHeatInfo {
    public abstract double getMoleCapacity(Phase phase);

    public abstract MonotonicMap<Phase> getCriticalPoints();

    public static Builder builder() {
        return new Builder();
    }

    public static SubMaterialHeatInfo getSimplified(Phase phase, double moleCapacity) {
        return new SimplifiedSubPhasePortrait(phase, moleCapacity);
    }

    public static class Builder {
        private MonotonicMap<Phase> criticalPoints;
        private final EnumMap<Phase, Double> moleCapacity;

        public Builder() {
            this.moleCapacity = new EnumMap<>(Phase.class);
        }

        public Builder setCriticalPoints(MonotonicMap<Phase> criticalPoints) {
            this.criticalPoints = criticalPoints;
            return this;
        }

        public Builder putCapacity(Phase phase, double moleCapacity) {
            this.moleCapacity.put(phase, moleCapacity);
            return this;
        }

        public SubMaterialHeatInfo build() {
            return new NormalSubPhasePortrait(this.criticalPoints, this.moleCapacity);
        }
    }

    private static class NormalSubPhasePortrait extends SubMaterialHeatInfo {
        private final MonotonicMap<Phase> criticalPoints;
        private final EnumMap<Phase, Double> moleCapacity;

        private NormalSubPhasePortrait(MonotonicMap<Phase> criticalPoints, EnumMap<Phase, Double> moleCapacity) {
            this.criticalPoints = criticalPoints;
            this.moleCapacity = moleCapacity;
        }

        @Override
        public double getMoleCapacity(Phase phase) {
            if (this.moleCapacity.containsKey(phase)) {
                return this.moleCapacity.get(phase);
            }
            return 1;
        }

        @Override
        public MonotonicMap<Phase> getCriticalPoints() {
            return this.criticalPoints;
        }
    }

    private static class SimplifiedSubPhasePortrait extends SubMaterialHeatInfo {
        private final MonotonicMap<Phase> criticalPoints;
        private final double moleCapacity;

        private SimplifiedSubPhasePortrait(Phase phase, double moleCapacity) {
            this.criticalPoints = MonotonicMap.<Phase>builder().addCriticalPoint(0.0, phase).build();
            this.moleCapacity = moleCapacity;
        }

        @Override
        public double getMoleCapacity(Phase phase) {
            return this.moleCapacity;
        }

        @Override
        public MonotonicMap<Phase> getCriticalPoints() {
            return this.criticalPoints;
        }
    }
}