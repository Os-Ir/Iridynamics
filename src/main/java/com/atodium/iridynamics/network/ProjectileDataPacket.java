package com.atodium.iridynamics.network;

import com.atodium.iridynamics.common.entity.ProjectileBaseEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class ProjectileDataPacket {
    private final UUID player;
    private final boolean killed;

    public ProjectileDataPacket(UUID player, boolean killed) {
        this.player = player;
        this.killed = killed;
    }

    public ProjectileDataPacket(FriendlyByteBuf buf) {
        this.player = buf.readUUID();
        this.killed = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.player);
        buf.writeBoolean(this.killed);
    }

    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player.getUUID().equals(this.player)) ProjectileBaseEntity.onPlayerHit(player, killed);
        });
    }
}