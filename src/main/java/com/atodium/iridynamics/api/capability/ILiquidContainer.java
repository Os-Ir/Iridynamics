package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.google.common.collect.ImmutableMap;

public interface ILiquidContainer {
    boolean isEmpty();

    void clear();

    int getMaterialTypes();

    ImmutableMap<MaterialBase, Integer> getAllMaterials();

    int usedCapacity();

    int liquidCapacity();

    boolean hasMaterial(MaterialBase material);

    int addMaterial(MaterialBase material, int add);

    int getMaterialUnit(MaterialBase material);
}