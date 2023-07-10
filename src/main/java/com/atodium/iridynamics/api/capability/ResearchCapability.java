package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.module.research.ResearchNetwork;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ResearchCapability implements IResearch, ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation KEY = Iridynamics.rl("research");
    public static final Capability<IResearch> RESEARCH = CapabilityManager.get(new CapabilityToken<>() {
    });

    private ResearchNetwork network;

    @Override
    public ResearchNetwork network() {
        return this.network;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == RESEARCH) return LazyOptional.of(() -> this).cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("network", this.network.serialize());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.network.deserialize(tag.getCompound("network"));
    }
}