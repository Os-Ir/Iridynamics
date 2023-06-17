package com.atodium.iridynamics.api.material;

import com.atodium.iridynamics.api.material.type.MaterialBase;

public class MaterialToolPropertyInfo {
    private MaterialBase material;
    private final int durability, harvestLevel;
    private final float efficiency;

    public MaterialToolPropertyInfo() {
        this(0, 0, 0.0f);
    }

    public MaterialToolPropertyInfo(int durability, int harvestLevel, float efficiency) {
        this.durability = Math.max(durability, 0);
        this.harvestLevel = Math.max(harvestLevel, 0);
        this.efficiency = Math.max(efficiency, 0.0f);
    }

    public static MaterialToolPropertyInfo empty(MaterialBase material) {
        return new MaterialToolPropertyInfo().setMaterial(material);
    }

    public MaterialToolPropertyInfo setMaterial(MaterialBase material) {
        this.material = material;
        return this;
    }

    public MaterialBase material() {
        return this.material;
    }

    public int durability() {
        return this.durability;
    }

    public int harvestLevel() {
        return this.harvestLevel;
    }

    public float efficiency() {
        return this.efficiency;
    }
}