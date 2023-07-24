package com.atodium.iridynamics.api.heat.impl;

import com.atodium.iridynamics.api.capability.LiquidContainerCapability;
import com.atodium.iridynamics.api.heat.HeatModule;
import com.atodium.iridynamics.api.heat.IPhasePortrait;
import com.atodium.iridynamics.api.material.Phase;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.util.data.MonotonicEntryMap;
import com.atodium.iridynamics.api.util.data.MonotonicMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class LiquidContainerPortrait implements IPhasePortrait {
    private final LiquidContainerCapability container;
    private MonotonicEntryMap pointEnergy;
    private MonotonicMap<Double> pointCapacity, energyCapacity;

    public LiquidContainerPortrait(LiquidContainerCapability container) {
        this.container = container;
        this.pointEnergy = MonotonicEntryMap.builder().build();
        this.pointCapacity = MonotonicMap.<Double>builder().build();
        this.energyCapacity = MonotonicMap.<Double>builder().build();
    }

    public void updatePointEnergy() {
        MonotonicEntryMap.Builder pointEnergyBuilder = MonotonicEntryMap.builder();
        MonotonicMap.Builder<Double> pointCapacityBuilder = MonotonicMap.builder();
        MonotonicMap.Builder<Double> energyCapacityBuilder = MonotonicMap.builder();
        List<Pair<MaterialBase, Integer>> materials = new ArrayList<>(this.container.getAllMaterials().entrySet().stream().map((entry) -> Pair.of(entry.getKey(), entry.getValue())).toList());
        materials.sort((pair1, pair2) -> Double.compare(this.getMeltPoint(pair1.getLeft()), this.getMeltPoint(pair2.getLeft())));
        double energy = 0.0;
        double lastPoint = 0.0;
        for (int i = 0; i < materials.size(); i++) {
            double point = this.getMeltPoint(materials.get(i).getLeft());
            double capacity = 0.0;
            for (int j = 0; j < i; j++)
                capacity += this.getLiquidCapacity(materials.get(j).getLeft()) * materials.get(j).getRight() / 144.0;
            for (int j = i; j < materials.size(); j++)
                capacity += this.getSolidCapacity(materials.get(j).getLeft()) * materials.get(j).getRight() / 144.0;
            energyCapacityBuilder.addData(energy, capacity);
            energy += (point - lastPoint) * capacity;
            pointEnergyBuilder.addData(point, energy);
            pointCapacityBuilder.addData(lastPoint, capacity);
            lastPoint = point;
        }
        double capacity = 0.0;
        for (Pair<MaterialBase, Integer> material : materials)
            capacity += this.getLiquidCapacity(material.getLeft()) * material.getRight() / 144.0;
        pointCapacityBuilder.addData(lastPoint, capacity);
        energyCapacityBuilder.addData(energy, capacity);
        this.pointEnergy = pointEnergyBuilder.build();
        this.pointCapacity = pointCapacityBuilder.build();
        this.energyCapacity = energyCapacityBuilder.build();
    }

    private double getMeltPoint(MaterialBase material) {
        return material.getHeatInfo().getCriticalPoints(HeatModule.ATMOSPHERIC_PRESSURE).getKey(Phase.LIQUID);
    }

    private double getSolidCapacity(MaterialBase material) {
        return material.getHeatInfo().getMoleCapacity(HeatModule.ATMOSPHERIC_PRESSURE, Phase.SOLID);
    }

    private double getLiquidCapacity(MaterialBase material) {
        return material.getHeatInfo().getMoleCapacity(HeatModule.ATMOSPHERIC_PRESSURE, Phase.LIQUID);
    }

    @Override
    public double getTemperature(double pressure, double energy) {
        return this.pointEnergy.getKey(energy) + (energy - this.pointEnergy.valueFloor(energy)) / this.energyCapacity.getData(energy);
    }

    @Override
    public double getEnergy(double pressure, double temperature) {
        return this.pointEnergy.getValue(temperature) + (temperature - this.pointEnergy.keyFloor(temperature)) * this.pointCapacity.getData(temperature);
    }

    @Override
    public Phase getPhase(double pressure, double temperature) {
        return Phase.SOLID;
    }
}