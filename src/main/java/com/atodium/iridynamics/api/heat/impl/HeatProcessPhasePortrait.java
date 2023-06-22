package com.atodium.iridynamics.api.heat.impl;

import com.atodium.iridynamics.api.heat.IPhasePortrait;
import com.atodium.iridynamics.api.material.Phase;

public record HeatProcessPhasePortrait(double capacity, double transformPoint,
                                       double transformEnergy) implements IPhasePortrait {
    public double process(double energy) {
        double base = this.capacity * this.transformPoint;
        return energy > base ? (energy - base) / this.transformEnergy : 0.0;
    }

    @Override
    public double getTemperature(double pressure, double energy) {
        return Math.min(energy / this.capacity, this.transformPoint);
    }

    @Override
    public double getEnergy(double pressure, double temperature) {
        return Math.min(temperature, this.transformPoint) * this.capacity;
    }

    @Override
    public Phase getPhase(double pressure, double temperature) {
        return Phase.SOLID;
    }
}