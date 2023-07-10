package com.atodium.iridynamics.network;

import com.atodium.iridynamics.api.gui.ModularContainer;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class ModularToServerPacket {
    private final FriendlyByteBuf data;
    private final int window;

    public ModularToServerPacket(FriendlyByteBuf buf, int window) {
        this.data = buf;
        this.window = window;
    }

    public ModularToServerPacket(FriendlyByteBuf buf) {
        this.data = new FriendlyByteBuf(Unpooled.copiedBuffer((buf.readBytes(buf.readInt()))));
        this.window = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.data.readableBytes());
        buf.writeBytes(this.data);
        buf.writeInt(this.window);
    }

    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && (this.window == 0 || this.window == player.containerMenu.containerId) && player.containerMenu instanceof ModularContainer modularContainer)
                modularContainer.getGuiInfo().getWidget(this.data.readInt()).receiveMessageFromClient(this.data);
        });
    }
}