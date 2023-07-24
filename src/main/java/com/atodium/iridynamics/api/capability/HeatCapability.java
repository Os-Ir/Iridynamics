package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.heat.IHeat;
import com.atodium.iridynamics.api.heat.IPhasePortrait;
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import com.atodium.iridynamics.api.heat.HeatModule;
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

public class HeatCapability implements IHeat, ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation KEY = Iridynamics.rl("heat");
    public static final Capability<IHeat> HEAT = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final IPhasePortrait portrait;
    private double energy;
    private double[] resistances;

    public HeatCapability() {
        this(new SolidPhasePortrait(4200.0));
    }

    public HeatCapability(IPhasePortrait portrait) {
        this(portrait, portrait.getEnergy(HeatModule.ATMOSPHERIC_PRESSURE, HeatModule.AMBIENT_TEMPERATURE), 0.0);
    }

    public HeatCapability(IPhasePortrait portrait, double resistance) {
        this(portrait, portrait.getEnergy(HeatModule.ATMOSPHERIC_PRESSURE, HeatModule.AMBIENT_TEMPERATURE), resistance);
    }

    public HeatCapability(IPhasePortrait portrait, double[] resistances) {
        this(portrait, portrait.getEnergy(HeatModule.ATMOSPHERIC_PRESSURE, HeatModule.AMBIENT_TEMPERATURE), resistances);
    }

    public HeatCapability(IPhasePortrait portrait, double energy, double resistance) {
        this.portrait = portrait;
        this.energy = energy;
        this.resistances = new double[6];
        Arrays.fill(this.resistances, resistance);
    }

    public HeatCapability(IPhasePortrait portrait, double energy, double[] resistances) {
        this.portrait = portrait;
        this.energy = energy;
        if (resistances.length >= 6) this.resistances = Arrays.copyOf(resistances, 6);
        else this.resistances = new double[6];
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
        return this.resistances[direction.get3DDataValue()];
    }

    @Override
    public double[] getAllResistance() {
        return this.resistances;
    }

    @Override
    public void updateResistance(Direction direction, double resistance) {
        this.resistances[direction.get3DDataValue()] = resistance;
    }

    @Override
    public void updateResistance(double[] resistances) {
        if (resistances.length >= 6) this.resistances = Arrays.copyOf(resistances, 6);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == HEAT) return LazyOptional.of(() -> this).cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("energy", this.energy);
        ListTag resistanceTag = new ListTag();
        for (int i = 0; i < 6; i++) resistanceTag.add(i, DoubleTag.valueOf(this.resistances[i]));
        tag.put("resistance", resistanceTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.energy = tag.getDouble("energy");
        ListTag resistanceTag = tag.getList("resistance", Tag.TAG_DOUBLE);
        for (int i = 0; i < 6; i++) this.resistances[i] = resistanceTag.getDouble(i);
    }
}