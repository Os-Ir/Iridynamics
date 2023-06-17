package com.atodium.iridynamics.api.capability;

public interface IHeatProcess extends IHeat {
    double getRecipeEnergy();

    double getRecipeTemperature();

    double process();

    default boolean isFinish() {
        return this.process() >= 1.0;
    }
}