package com.atodium.iridynamics.api.heat;

import com.atodium.iridynamics.api.material.Phase;
import com.atodium.iridynamics.api.util.data.MonotonicMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Function;

public abstract class MaterialHeatInfo {
    public abstract SubMaterialHeatInfo getSubHeatInfo(double pressure);

    public double getMoleCapacity(double pressure, Phase phase) {
        return this.getSubHeatInfo(pressure).getMoleCapacity(phase);
    }

    public double getMeltingPoint() {
        return this.getMeltingPoint(HeatModule.ATMOSPHERIC_PRESSURE);
    }

    public double getMeltingPoint(double pressure) {
        return this.getSubHeatInfo(pressure).getCriticalPoints().getCriticalPoint(Phase.LIQUID);
    }

    public double getBoilingPoint() {
        return this.getBoilingPoint(HeatModule.ATMOSPHERIC_PRESSURE);
    }

    public double getBoilingPoint(double pressure) {
        return this.getSubHeatInfo(pressure).getCriticalPoints().getCriticalPoint(Phase.GAS);
    }

    public MonotonicMap<Phase> getCriticalPoints() {
        return this.getCriticalPoints(HeatModule.ATMOSPHERIC_PRESSURE);
    }

    public MonotonicMap<Phase> getCriticalPoints(double pressure) {
        return this.getSubHeatInfo(pressure).getCriticalPoints();
    }

    public double getTemperature(double pressure, double moleEnergy) {
        SubMaterialHeatInfo sub = this.getSubHeatInfo(pressure);
        List<Pair<Double, Phase>> points = sub.getCriticalPoints().getPointsMap();
        double temp = 0;
        for (Pair<Double, Phase> pair : points) {
            double pointTemp = pair.getLeft();
            double specificHeat = sub.getMoleCapacity(pair.getRight());
            if (specificHeat * (pointTemp - temp) > moleEnergy) {
                temp += moleEnergy / specificHeat;
                moleEnergy = 0;
                break;
            } else {
                moleEnergy -= specificHeat * (pointTemp - temp);
                temp = pointTemp;
            }
        }
        temp += moleEnergy / sub.getMoleCapacity(points.get(points.size() - 1).getRight());
        return temp;
    }

    public double getMoleEnergy(double pressure, double temperature) {
        SubMaterialHeatInfo sub = this.getSubHeatInfo(pressure);
        List<Pair<Double, Phase>> points = sub.getCriticalPoints().getPointsMap();
        double moleEnergy = 0, temp = 0;
        for (Pair<Double, Phase> pair : points) {
            double pointTemp = pair.getLeft();
            if (pointTemp <= temperature) {
                moleEnergy += (pointTemp - temp) * sub.getMoleCapacity(pair.getRight());
                temp = pointTemp;
            } else {
                moleEnergy += (temperature - temp) * sub.getMoleCapacity(pair.getRight());
                temp = pointTemp;
                break;
            }
        }
        if (temperature > temp) {
            moleEnergy += (temperature - temp) * sub.getMoleCapacity(points.get(points.size() - 1).getRight());
        }
        return moleEnergy;
    }

    public Phase getPhase(double pressure, double temperature) {
        return this.getSubHeatInfo(pressure).getCriticalPoints().getData(temperature);
    }

    public static FunctionHeatInfo getNormal(Function<Double, SubMaterialHeatInfo> function) {
        return new FunctionHeatInfo(function);
    }

    public static SimplifiedHeatInfo getSimplified(SubMaterialHeatInfo sub) {
        return new SimplifiedHeatInfo(sub);
    }

    private static class FunctionHeatInfo extends MaterialHeatInfo {
        private final Function<Double, SubMaterialHeatInfo> function;

        private FunctionHeatInfo(Function<Double, SubMaterialHeatInfo> function) {
            this.function = function;
        }

        @Override
        public SubMaterialHeatInfo getSubHeatInfo(double pressure) {
            return this.function.apply(pressure);
        }
    }

    private static class SimplifiedHeatInfo extends MaterialHeatInfo {
        private final SubMaterialHeatInfo sub;
        private final MonotonicMap<Phase> phaseEnergy;

        private SimplifiedHeatInfo(SubMaterialHeatInfo sub) {
            this.sub = sub;
            double sum = 0.0;
            double lastCapacity = 0.0;
            double lastTemp = 0.0;
            MonotonicMap.Builder<Phase> builder = MonotonicMap.builder();
            for (Pair<Double, Phase> pair : sub.getCriticalPoints().getPointsMap()) {
                Phase phase = pair.getRight();
                sum += lastCapacity * (pair.getLeft() - lastTemp);
                lastCapacity = sub.getMoleCapacity(phase);
                lastTemp = pair.getLeft();
                builder.addCriticalPoint(sum, phase);
            }
            this.phaseEnergy = builder.build();
        }

        @Override
        public SubMaterialHeatInfo getSubHeatInfo(double pressure) {
            return this.sub;
        }

        @Override
        public double getTemperature(double pressure, double moleEnergy) {
            Phase phase = this.phaseEnergy.getData(moleEnergy);
            return this.sub.getCriticalPoints().getCriticalPoint(phase) + (moleEnergy - this.phaseEnergy.getCriticalPoint(phase)) / this.sub.getMoleCapacity(phase);
        }

        @Override
        public double getMoleEnergy(double pressure, double temperature) {
            Phase phase = this.sub.getCriticalPoints().getData(temperature);
            return this.phaseEnergy.getCriticalPoint(phase) + (temperature - this.sub.getCriticalPoints().getCriticalPoint(phase)) * this.sub.getMoleCapacity(phase);
        }
    }
}