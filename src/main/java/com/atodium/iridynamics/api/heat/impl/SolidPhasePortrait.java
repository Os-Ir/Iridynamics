package com.atodium.iridynamics.api.heat.impl;

import com.atodium.iridynamics.api.heat.IPhasePortrait;
import com.atodium.iridynamics.api.material.Phase;

public class SolidPhasePortrait implements IPhasePortrait {
    private double capacity;

    public SolidPhasePortrait(double capacity) {
        this.capacity = capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getCapacity() {
        return this.capacity;
    }

    @Override
    public double getTemperature(double pressure, double energy) {
        return energy / this.capacity;
    }

    @Override
    public double getEnergy(double pressure, double temperature) {
        return temperature * this.capacity;
    }

    @Override
    public Phase getPhase(double pressure, double temperature) {
        return Phase.SOLID;
    }
}