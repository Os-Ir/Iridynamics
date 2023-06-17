package com.atodium.iridynamics.api.material;

import com.atodium.iridynamics.api.material.type.MaterialBase;

public class MaterialPhysicalInfo {
    private MaterialBase material;
    private final double density, thermalConductivity, heatCapacity, calorificValue;

    public MaterialPhysicalInfo() {
        this(1000.0, 100.0, 1000.0, 0.0);
    }

    public MaterialPhysicalInfo(double density, double thermalConductivity, double heatCapacity, double calorificValue) {
        this.density = density;
        this.thermalConductivity = thermalConductivity;
        this.heatCapacity = heatCapacity;
        this.calorificValue = calorificValue;
    }

    public static MaterialPhysicalInfo empty(MaterialBase material) {
        return new MaterialPhysicalInfo().setMaterial(material);
    }

    public MaterialPhysicalInfo setMaterial(MaterialBase material) {
        this.material = material;
        return this;
    }

    public MaterialBase material() {
        return this.material;
    }

    public double density() {
        return this.density;
    }

    public double thermalConductivity() {
        return this.thermalConductivity;
    }

    public double heatCapacity() {
        return this.heatCapacity;
    }

    public double calorificValue() {
        return this.calorificValue;
    }

    public double moleMass() {
        return this.density / 9.0;
    }

    public double moleHeatCapacity() {
        return this.heatCapacity * this.density / 9.0;
    }

    public double moleCalorificValue() {
        return this.calorificValue * this.density / 9.0;
    }
}