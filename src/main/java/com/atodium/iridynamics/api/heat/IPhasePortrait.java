package com.atodium.iridynamics.api.heat;

import com.atodium.iridynamics.api.material.Phase;

public interface IPhasePortrait {
    double getTemperature(double pressure, double energy);

    double getEnergy(double pressure, double temperature);

    Phase getPhase(double pressure, double temperature);
}