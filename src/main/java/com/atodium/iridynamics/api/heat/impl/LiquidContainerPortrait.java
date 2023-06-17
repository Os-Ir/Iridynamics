package com.atodium.iridynamics.api.heat.impl;

import com.atodium.iridynamics.api.capability.LiquidContainerCapability;
import com.atodium.iridynamics.api.heat.HeatUtil;
import com.atodium.iridynamics.api.heat.IPhasePortrait;
import com.atodium.iridynamics.api.material.Phase;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.util.data.MonotonicEntryMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class LiquidContainerPortrait implements IPhasePortrait {
    private final LiquidContainerCapability container;
    private MonotonicEntryMap pointEnergy, pointCapacity, energyCapacity;

    public LiquidContainerPortrait(LiquidContainerCapability container) {
        this.container = container;
        this.pointEnergy = MonotonicEntryMap.builder().build();
        this.pointCapacity = MonotonicEntryMap.builder().build();
        this.energyCapacity = MonotonicEntryMap.builder().build();
    }

    public void updatePointEnergy() {
        MonotonicEntryMap.Builder pointEnergyBuilder = MonotonicEntryMap.builder();
        MonotonicEntryMap.Builder pointCapacityBuilder = MonotonicEntryMap.builder();
        MonotonicEntryMap.Builder energyCapacityBuilder = MonotonicEntryMap.builder();
        List<Pair<MaterialBase, Integer>> materials = new ArrayList<>(this.container.getAllMaterials().entrySet().stream().map((entry) -> Pair.of(entry.getKey(), entry.getValue())).toList());
        materials.sort((pair1, pair2) -> Double.compare(this.getMeltPoint(pair1.getLeft()), this.getMeltPoint(pair2.getLeft())));
        int size = materials.size();
        double energy = 0.0;
        double lastPoint = 0.0;
        for (int i = 0; i < size; i++) {
            double point = this.getMeltPoint(materials.get(i).getLeft());
            double capacity = 0.0;
            for (int j = 0; j < i; j++)
                capacity += this.getLiquidCapacity(materials.get(j).getLeft()) * materials.get(i).getRight() / 144.0;
            for (int j = i; j < size; j++)
                capacity += this.getSolidCapacity(materials.get(j).getLeft()) * materials.get(i).getRight() / 144.0;
            energyCapacityBuilder.addData(energy, capacity);
            energy += (point - lastPoint) * capacity;
            pointEnergyBuilder.addData(point, energy);
            pointCapacityBuilder.addData(lastPoint, capacity);
            lastPoint = point;
        }
        double capacity = 0.0;
        for (int i = 0; i < size; i++)
            capacity += this.getLiquidCapacity(materials.get(i).getLeft()) * materials.get(i).getRight() / 144.0;
        pointCapacityBuilder.addData(lastPoint, capacity);
        energyCapacityBuilder.addData(energy, capacity);
        this.pointEnergy = pointEnergyBuilder.build();
        this.pointCapacity = pointCapacityBuilder.build();
        this.energyCapacity = energyCapacityBuilder.build();
    }

    private double getMeltPoint(MaterialBase material) {
        return material.getHeatInfo().getCriticalPoints(HeatUtil.ATMOSPHERIC_PRESSURE).getCriticalPoint(Phase.LIQUID);
    }

    private double getSolidCapacity(MaterialBase material) {
        return material.getHeatInfo().getMoleCapacity(HeatUtil.ATMOSPHERIC_PRESSURE, Phase.SOLID);
    }

    private double getLiquidCapacity(MaterialBase material) {
        return material.getHeatInfo().getMoleCapacity(HeatUtil.ATMOSPHERIC_PRESSURE, Phase.LIQUID);
    }

    @Override
    public double getTemperature(double pressure, double energy) {
        return this.pointEnergy.getKey(energy) + (energy - this.pointEnergy.valueFloor(energy)) / this.energyCapacity.getValue(energy);
    }

    @Override
    public double getEnergy(double pressure, double temperature) {
        return this.pointEnergy.getValue(temperature) + (temperature - this.pointEnergy.keyFloor(temperature)) * this.pointCapacity.getValue(temperature);
    }

    @Override
    public Phase getPhase(double pressure, double temperature) {
        return Phase.SOLID;
    }
}