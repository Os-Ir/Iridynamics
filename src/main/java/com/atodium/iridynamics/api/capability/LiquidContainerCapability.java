package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.heat.HeatModule;
import com.atodium.iridynamics.api.heat.IHeat;
import com.atodium.iridynamics.api.heat.IPhasePortrait;
import com.atodium.iridynamics.api.heat.impl.LiquidContainerPortrait;
import com.atodium.iridynamics.api.liquid.ILiquidContainer;
import com.atodium.iridynamics.api.liquid.SimpleLiquidContainer;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class LiquidContainerCapability implements IHeat, ILiquidContainer, ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation KEY = Iridynamics.rl("liquid_container");
    public static final Capability<ILiquidContainer> LIQUID_CONTAINER = CapabilityManager.get(new CapabilityToken<>() {
    });

    private double energy;
    private final LiquidContainerPortrait portrait;
    private final SimpleLiquidContainer container;

    public LiquidContainerCapability(int liquidCapacity) {
        this.portrait = new LiquidContainerPortrait(this);
        this.container = new SimpleLiquidContainer(liquidCapacity);
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
        if (this.isEmpty()) return HeatModule.AMBIENT_TEMPERATURE;
        return this.getPhasePortrait().getTemperature(pressure, this.getEnergy());
    }

    @Override
    public boolean isEmpty() {
        return this.container.isEmpty();
    }

    @Override
    public void clear() {
        this.container.clear();
        this.energy = 0.0;
        this.portrait.updatePointEnergy();
    }

    @Override
    public int getMaterialTypes() {
        return this.container.getMaterialTypes();
    }

    @Override
    public ImmutableMap<MaterialBase, Integer> getAllMaterials() {
        return this.container.getAllMaterials();
    }

    @Override
    public int usedCapacity() {
        return this.container.usedCapacity();
    }

    @Override
    public int liquidCapacity() {
        return this.container.liquidCapacity();
    }

    @Override
    public boolean hasMaterial(MaterialBase material) {
        return this.container.hasMaterial(material);
    }

    @Override
    public int addMaterial(MaterialBase material, int add) {
        int remain = this.container.addMaterial(material, add);
        this.portrait.updatePointEnergy();
        return remain;
    }

    @Override
    public int getMaterialUnit(MaterialBase material) {
        return this.container.getMaterialUnit(material);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == LIQUID_CONTAINER) return LazyOptional.of(() -> this).cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("energy", this.energy);
        tag.put("container", this.container.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.energy = tag.getDouble("energy");
        this.container.deserializeNBT(tag.getCompound("container"));
        this.portrait.updatePointEnergy();
    }
}