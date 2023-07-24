package com.atodium.iridynamics.api.liquid;

import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public class SimpleLiquidContainer implements ILiquidContainer, INBTSerializable<CompoundTag> {
    private final int liquidCapacity;
    private final Object2IntMap<MaterialBase> materials;

    public SimpleLiquidContainer() {
        this(Integer.MAX_VALUE);
    }

    public SimpleLiquidContainer(int liquidCapacity) {
        this.liquidCapacity = liquidCapacity;
        this.materials = new Object2IntOpenHashMap<>();
    }

    @Override
    public boolean isEmpty() {
        return this.materials.isEmpty();
    }

    @Override
    public void clear() {
        this.materials.clear();
    }

    @Override
    public int getMaterialTypes() {
        return this.materials.size();
    }

    @Override
    public ImmutableMap<MaterialBase, Integer> getAllMaterials() {
        return ImmutableMap.copyOf(this.materials);
    }

    @Override
    public ImmutableMap<MaterialBase, Integer> getAllLiquidMaterials(double temperature) {
        Object2IntMap<MaterialBase> filter = new Object2IntOpenHashMap<>();
        for (Object2IntMap.Entry<MaterialBase> entry : this.materials.object2IntEntrySet()) {
            MaterialBase material = entry.getKey();
            if (temperature >= material.getHeatInfo().getMeltingPoint()) filter.put(material, entry.getIntValue());
        }
        return ImmutableMap.copyOf(filter);
    }

    @Override
    public int usedCapacity() {
        int used = 0;
        for (Object2IntMap.Entry<MaterialBase> entry : this.materials.object2IntEntrySet()) used += entry.getIntValue();
        return used;
    }

    @Override
    public int liquidCapacity() {
        return this.liquidCapacity;
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
        if (add <= 0) {
            int unit = this.getMaterialUnit(material) + add;
            if (unit <= 0) this.materials.removeInt(material);
            else this.materials.put(material, unit);
            return 0;
        }
        int remainSpace = this.liquidCapacity - this.usedCapacity();
        if (remainSpace >= add) {
            int unit = this.getMaterialUnit(material) + add;
            if (unit <= 0) this.materials.removeInt(material);
            else this.materials.put(material, this.getMaterialUnit(material) + add);
            return 0;
        }
        int unit = this.getMaterialUnit(material) + add;
        if (unit <= 0) this.materials.removeInt(material);
        else this.materials.put(material, this.getMaterialUnit(material) + remainSpace);
        return add - remainSpace;
    }

    @Override
    public int getMaterialUnit(MaterialBase material) {
        return this.materials.getInt(material);
    }

    @Override
    public int getLiquidMaterialUnit(MaterialBase material, double temperature) {
        if (temperature >= material.getHeatInfo().getMeltingPoint()) return this.materials.getInt(material);
        return 0;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag materials = new ListTag();
        this.materials.forEach((material, unit) -> {
            CompoundTag t = new CompoundTag();
            t.putString("material", material.getName());
            t.putInt("unit", unit);
            materials.add(t);
        });
        tag.put("materials", materials);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ListTag materials = tag.getList("materials", Tag.TAG_COMPOUND);
        this.materials.clear();
        for (int i = 0; i < materials.size(); i++) {
            CompoundTag t = materials.getCompound(i);
            this.materials.put(MaterialBase.getMaterialByName(t.getString("material")), t.getInt("unit"));
        }
    }
}