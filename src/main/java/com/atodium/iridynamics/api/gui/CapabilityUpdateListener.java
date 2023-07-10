package com.atodium.iridynamics.api.gui;

import com.atodium.iridynamics.network.CapabilityUpdatePacket;
import com.atodium.iridynamics.network.ModNetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.network.NetworkDirection;

import java.util.HashMap;
import java.util.Map;

public class CapabilityUpdateListener implements ContainerListener {
    private static final Map<ResourceLocation, Capability<?>> REGISTRY = new HashMap<>();

    private final ServerPlayer player;

    public CapabilityUpdateListener(ServerPlayer player) {
        this.player = player;
    }

    public static void register(ResourceLocation key, Capability<?> cap) {
        REGISTRY.put(key, cap);
    }

    @SuppressWarnings("unchecked")
    public static CompoundTag readCapabilityData(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        REGISTRY.forEach((key, cap) -> stack.getCapability(cap).ifPresent((data) -> tag.put(key.toString(), ((ICapabilitySerializable<CompoundTag>) data).serializeNBT())));
        return tag;
    }

    @SuppressWarnings("unchecked")
    public static void applyCapabilityData(ItemStack stack, CompoundTag nbt) {
        REGISTRY.forEach((key, cap) -> stack.getCapability(cap).ifPresent((data) -> ((ICapabilitySerializable<CompoundTag>) data).deserializeNBT((CompoundTag) nbt.get(key.toString()))));
    }

    public static boolean shouldSync(ItemStack stack) {
        for (Capability<?> cap : REGISTRY.values()) if (stack.getCapability(cap, null).isPresent()) return true;
        return false;
    }

    @Override
    public void slotChanged(AbstractContainerMenu container, int slot, ItemStack stack) {
        if (shouldSync(stack)) {
            CapabilityUpdatePacket packet = new CapabilityUpdatePacket(container.containerId, slot, stack);
            if (packet.hasData())
                ModNetworkHandler.CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @Override
    public void dataChanged(AbstractContainerMenu container, int varToUpdate, int newValue) {

    }
}