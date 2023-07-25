package com.atodium.iridynamics.api.heat.liquid;

import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.Map;

public class ImmutableLiquidContainer implements ILiquidContainer {
    private final ImmutableMap<MaterialBase, Integer> materials;

    public ImmutableLiquidContainer(Map<MaterialBase, Integer> materials) {
        this.materials = ImmutableMap.copyOf(materials);
    }

    @Override
    public boolean isEmpty() {
        return this.materials.isEmpty();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaterialTypes() {
        return this.materials.size();
    }

    @Override
    public ImmutableMap<MaterialBase, Integer> getAllMaterials() {
        return this.materials;
    }

    @Override
    public ImmutableMap<MaterialBase, Integer> getAllLiquidMaterials(double temperature) {
        Object2IntMap<MaterialBase> filter = new Object2IntOpenHashMap<>();
        for (Map.Entry<MaterialBase, Integer> entry : this.materials.entrySet()) {
            MaterialBase material = entry.getKey();
            if (temperature >= material.getHeatInfo().getMeltingPoint())
                filter.put(material, entry.getValue().intValue());
        }
        return ImmutableMap.copyOf(filter);
    }

    @Override
    public int usedCapacity() {
        int used = 0;
        for (Map.Entry<MaterialBase, Integer> entry : this.materials.entrySet()) used += entry.getValue();
        return used;
    }

    @Override
    public int liquidCapacity() {
        return this.usedCapacity();
    }

    @Override
    public boolean hasMaterial(MaterialBase material) {
        return this.materials.containsKey(material);
    }

    @Override
    public boolean hasLiquidMaterial(MaterialBase material, double temperature) {
        return temperature >= material.getHeatInfo().getMeltingPoint() && this.materials.containsKey(material);
    }

    @Override
    public int addMaterial(MaterialBase material, int add) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaterialUnit(MaterialBase material) {
        return this.materials.get(material);
    }

    @Override
    public int getLiquidMaterialUnit(MaterialBase material, double temperature) {
        if (temperature >= material.getHeatInfo().getMeltingPoint()) return this.materials.get(material);
        return 0;
    }
}