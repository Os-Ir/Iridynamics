package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.heat.HeatUtil;
import com.atodium.iridynamics.api.heat.IPhasePortrait;
import com.atodium.iridynamics.api.module.ItemHeatModule;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Arrays;

public class HeatProcessCapability implements IHeatProcess, ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation KEY = Iridynamics.rl("heat_process");
    public static final Capability<IHeatProcess> HEAT_PROCESS = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final IPhasePortrait portrait;
    private double recipeTemperature, recipeEnergy;
    private final double[] resistances;
    private double energy, processEnergy;

    public HeatProcessCapability(IPhasePortrait portrait) {
        this(portrait, 0.0, 0.0, new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
    }

    public HeatProcessCapability(IPhasePortrait portrait, double recipeTemperature, double recipeEnergy, double[] resistances) {
        this.portrait = portrait;
        this.recipeTemperature = recipeTemperature;
        this.recipeEnergy = recipeEnergy;
        if (resistances.length >= 6) this.resistances = Arrays.copyOf(resistances, 6);
        else this.resistances = new double[6];
        this.energy = this.processEnergy = 0.0;
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
        energy = Math.max(energy, 0.0);
        this.increaseEnergy(energy - this.energy, this.recipeTemperature);
    }

    @Override
    public void increaseEnergy(double energy) {
        energy = Math.max(energy, 0.0);
        this.increaseEnergy(energy, this.recipeTemperature);
    }

    @Override
    public double increaseEnergy(double energy, double maxTemperature) {
        return this.increaseEnergy(energy, ItemHeatModule.ATMOSPHERIC_PRESSURE, maxTemperature);
    }

    @Override
    public double increaseEnergy(double energy, double pressure, double maxTemperature) {
        energy = Math.max(energy, 0.0);
        double maxEnergy = this.getPhasePortrait().getEnergy(pressure, maxTemperature) - this.energy;
        if (energy <= maxEnergy) this.energy += energy;
        else {
            this.energy += maxEnergy;
            this.processEnergy += energy - maxEnergy;
        }
        return 0.0;
    }

    @Override
    public double getResistance(Direction direction) {
        return this.resistances[direction.get3DDataValue()];
    }

    @Override
    public double[] getAllResistance() {
        return this.resistances;
    }

    @Override
    public void updateResistance(Direction direction, double resistance) {
        throw new UnsupportedOperationException("Can not update resistance for HeatProcessCapability");
    }

    @Override
    public void updateResistance(double[] resistances) {
        throw new UnsupportedOperationException("Can not update resistance for HeatProcessCapability");
    }

    @Override
    public double getRecipeEnergy() {
        return this.recipeEnergy;
    }

    @Override
    public double getRecipeTemperature() {
        return this.recipeTemperature;
    }

    @Override
    public double process() {
        return this.processEnergy / this.recipeEnergy;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == HeatCapability.HEAT || cap == HEAT_PROCESS) return LazyOptional.of(() -> this).cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("recipeTemperature", this.recipeTemperature);
        tag.putDouble("recipeEnergy", this.recipeEnergy);
        tag.putDouble("energy", this.energy);
        tag.putDouble("processEnergy", this.processEnergy);
        ListTag resistanceTag = new ListTag();
        for (int i = 0; i < 6; i++) resistanceTag.add(i, DoubleTag.valueOf(this.resistances[i]));
        tag.put("resistance", resistanceTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.recipeTemperature = tag.getDouble("recipeTemperature");
        this.recipeEnergy = tag.getDouble("recipeEnergy");
        this.energy = tag.getDouble("energy");
        this.processEnergy = tag.getDouble("processEnergy");
        ListTag resistanceTag = tag.getList("resistance", Tag.TAG_DOUBLE);
        for (int i = 0; i < 6; i++) this.resistances[i] = resistanceTag.getDouble(i);
    }
}