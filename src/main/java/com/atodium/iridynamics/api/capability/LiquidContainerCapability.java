package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.heat.HeatUtil;
import com.atodium.iridynamics.api.heat.IPhasePortrait;
import com.atodium.iridynamics.api.heat.impl.LiquidContainerPortrait;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Map;

public class LiquidContainerCapability implements IHeat, ILiquidContainer, ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation KEY = new ResourceLocation(Iridynamics.MODID, "liquid_container");
    public static final Capability<ILiquidContainer> LIQUID_CONTAINER = CapabilityManager.get(new CapabilityToken<>() {
    });

    private int liquidCapacity;
    private double energy;
    private final LiquidContainerPortrait portrait;
    private final Map<MaterialBase, Integer> materials;

    public LiquidContainerCapability(int liquidCapacity) {
        this.liquidCapacity = liquidCapacity;
        this.portrait = new LiquidContainerPortrait(this);
        this.materials = Maps.newHashMap();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IPhasePortrait> T getPhasePortrait() {
        return (T) this.portrait;
    }

    @Override
    public double getEnergy() {
        return this.energy;
    }

    @Override
    public void setEnergy(double energy) {
        this.energy = Math.max(energy, 0.0);
    }

    @Override
    public void increaseEnergy(double energy) {
        this.energy = Math.max(this.energy + energy, 0.0);
    }

    @Override
    public double getResistance(Direction direction) {
        return 0.0;
    }

    @Override
    public double[] getAllResistance() {
        return new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    }

    @Override
    public void updateResistance(Direction direction, double resistance) {

    }

    @Override
    public void updateResistance(double[] resistances) {

    }

    @Override
    public double getTemperature(double pressure) {
        if (this.isEmpty()) return HeatUtil.AMBIENT_TEMPERATURE;
        return this.getPhasePortrait().getTemperature(pressure, this.getEnergy());
    }

    @Override
    public boolean isEmpty() {
        return this.materials.isEmpty();
    }

    @Override
    public void clear() {
        this.materials.clear();
        this.energy = 0.0;
        this.portrait.updatePointEnergy();
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
    public int usedCapacity() {
        int used = 0;
        for (Map.Entry<MaterialBase, Integer> entry : this.materials.entrySet()) used += entry.getValue();
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
    public int addMaterial(MaterialBase material, int add) {
        if (add < 0) {
            int unit = this.getMaterialUnit(material) + add;
            if (unit <= 0) this.materials.remove(material);
            else this.materials.put(material, unit);
            this.portrait.updatePointEnergy();
            return 0;
        }
        int remainSpace = this.liquidCapacity - this.usedCapacity();
        if (remainSpace >= add) {
            this.materials.put(material, this.getMaterialUnit(material) + add);
            this.portrait.updatePointEnergy();
            return 0;
        }
        this.materials.put(material, this.getMaterialUnit(material) + remainSpace);
        this.portrait.updatePointEnergy();
        return add - remainSpace;
    }

    @Override
    public int getMaterialUnit(MaterialBase material) {
        if (this.materials.containsKey(material)) return this.materials.get(material);
        return 0;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == LIQUID_CONTAINER) return LazyOptional.of(() -> this).cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("liquid_capacity", this.liquidCapacity);
        tag.putDouble("energy", this.energy);
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
        this.liquidCapacity = tag.getInt("liquid_capacity");
        this.energy = tag.getDouble("energy");
        ListTag materials = tag.getList("materials", Tag.TAG_COMPOUND);
        for (int i = 0; i < materials.size(); i++) {
            CompoundTag t = materials.getCompound(i);
            this.materials.put(MaterialBase.getMaterialByName(t.getString("material")), t.getInt("unit"));
        }
        this.portrait.updatePointEnergy();
    }
}