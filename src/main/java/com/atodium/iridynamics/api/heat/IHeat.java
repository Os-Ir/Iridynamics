package com.atodium.iridynamics.api.heat;

import com.atodium.iridynamics.api.heat.IPhasePortrait;
import com.atodium.iridynamics.api.heat.HeatModule;
import net.minecraft.core.Direction;

public interface IHeat {
    <T extends IPhasePortrait> T getPhasePortrait();

    double getEnergy();

    void setEnergy(double energy);

    void increaseEnergy(double energy);

    double getResistance(Direction direction);

    double[] getAllResistance();

    default double getResistance() {
        return this.getResistance(Direction.UP);
    }

    void updateResistance(Direction direction, double resistance);

    void updateResistance(double[] resistances);

    default double getTemperature() {
        return this.getTemperature(HeatModule.ATMOSPHERIC_PRESSURE);
    }

    default double getTemperature(double pressure) {
        return this.getPhasePortrait().getTemperature(pressure, this.getEnergy());
    }

    default void setTemperature(double temperature) {
        this.setTemperature(HeatModule.ATMOSPHERIC_PRESSURE, temperature);
    }

    default void setTemperature(double pressure, double temperature) {
        this.setEnergy(this.getPhasePortrait().getEnergy(pressure, temperature));
    }

    default double increaseEnergy(double energy, double maxTemperature) {
        return this.increaseEnergy(energy, HeatModule.ATMOSPHERIC_PRESSURE, maxTemperature);
    }

    default double increaseEnergy(double energy, double pressure, double maxTemperature) {
        double maxEnergy = this.getPhasePortrait().getEnergy(pressure, maxTemperature) - this.getEnergy();
        if (maxEnergy > 0) {
            if (energy <= maxEnergy) {
                this.increaseEnergy(energy);
                return 0.0;
            }
            this.increaseEnergy(maxEnergy);
            return energy - maxEnergy;
        }
        if (energy >= 0) return energy;
        this.increaseEnergy(energy);
        return 0.0;
    }
}