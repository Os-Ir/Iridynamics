package com.atodium.iridynamics.api.heat.liquid;

import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.google.common.collect.ImmutableMap;

public interface ILiquidContainer {
    boolean isEmpty();

    void clear();

    int getMaterialTypes();

    ImmutableMap<MaterialBase, Integer> getAllMaterials();

    ImmutableMap<MaterialBase, Integer> getAllLiquidMaterials(double temperature);

    int usedCapacity();

    int liquidCapacity();

    boolean hasMaterial(MaterialBase material);

    boolean hasLiquidMaterial(MaterialBase material, double temperature);

    int addMaterial(MaterialBase material, int add);

    int getMaterialUnit(MaterialBase material);

    int getLiquidMaterialUnit(MaterialBase material, double temperature);
}