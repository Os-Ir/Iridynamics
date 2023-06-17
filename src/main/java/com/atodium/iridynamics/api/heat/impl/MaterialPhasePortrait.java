package com.atodium.iridynamics.api.heat.impl;

import com.atodium.iridynamics.api.heat.IPhasePortrait;
import com.atodium.iridynamics.api.heat.MaterialHeatInfo;
import com.atodium.iridynamics.api.material.Phase;

public class MaterialPhasePortrait implements IPhasePortrait {
    private final MaterialHeatInfo material;
    private double mole;

    public MaterialPhasePortrait(MaterialHeatInfo material, double mole) {
        this.material = material;
        this.mole = mole;
    }

    public MaterialHeatInfo getMaterialHeatInfo() {
        return this.material;
    }

    public void setMoleNumber(double mole) {
        this.mole = mole;
    }

    public double getMoleNumber() {
        return this.mole;
    }

    @Override
    public double getTemperature(double pressure, double energy) {
        return this.material.getTemperature(pressure, energy / this.mole);
    }

    @Override
    public double getEnergy(double pressure, double temperature) {
        return this.material.getMoleEnergy(pressure, temperature) * this.mole;
    }

    @Override
    public Phase getPhase(double pressure, double temperature) {
        return this.material.getPhase(pressure, temperature);
    }
}