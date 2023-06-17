package com.atodium.iridynamics.common.network;

import com.atodium.iridynamics.api.gui.CapabilityUpdateListener;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;

public class CapabilityUpdatePacket {
    private final Map<Integer, CompoundTag> data;
    private final int window;

    public CapabilityUpdatePacket(int window, int slot, ItemStack stack) {
        this.window = window;
        this.data = Maps.newHashMap();
        CompoundTag nbt = CapabilityUpdateListener.readCapabilityData(stack);
        if (!nbt.isEmpty()) this.data.put(slot, nbt);
    }

    public CapabilityUpdatePacket(FriendlyByteBuf buf) {
        this.window = buf.readInt();
        this.data = Maps.newHashMap();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            int slot = buf.readInt();
            CompoundTag nbt = buf.readNbt();
            this.data.put(slot, nbt);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.window);
        buf.writeInt(this.data.size());
        this.data.forEach((slot, nbt) -> {
            buf.writeInt(slot);
            buf.writeNbt(nbt);
        });
    }

    public boolean hasData() {
        return !this.data.isEmpty();
    }

    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && (this.window == 0 || this.window == player.containerMenu.containerId))
                this.data.forEach((slot, tag) -> CapabilityUpdateListener.applyCapabilityData(player.containerMenu.getSlot(slot).getItem(), tag));
        });
    }
}