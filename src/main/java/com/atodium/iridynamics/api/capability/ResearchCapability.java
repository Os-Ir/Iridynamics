package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.research.ResearchModule;
import com.atodium.iridynamics.api.research.ResearchNetwork;
import com.google.common.collect.Maps;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Map;

public class ResearchCapability implements IResearch, ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation KEY = Iridynamics.rl("research");
    public static final Capability<IResearch> RESEARCH = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final Map<String, ResearchNetwork> networks;

    public ResearchCapability() {
        this.networks = Maps.newHashMap();
        for (Map.Entry<String, String> entry : ResearchModule.NETWORK_TYPES.entrySet())
            this.networks.put(entry.getKey(), new ResearchNetwork(entry.getValue()));
    }

    @Override
    public Map<String, ResearchNetwork> allNetworks() {
        return this.networks;
    }

    @Override
    public ResearchNetwork network(String category) {
        return this.networks.get(category);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == RESEARCH) return LazyOptional.of(() -> this).cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        this.networks.forEach((category, network) -> tag.put(category, network.serialize()));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.networks.forEach((category, network) -> network.deserialize(tag.getCompound(category)));
    }
}