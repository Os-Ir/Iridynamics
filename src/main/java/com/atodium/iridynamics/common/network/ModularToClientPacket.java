package com.atodium.iridynamics.common.network;

import com.atodium.iridynamics.api.gui.ModularContainer;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ModularToClientPacket {
    private final FriendlyByteBuf data;
    private final int window;

    public ModularToClientPacket(FriendlyByteBuf buf, int window) {
        this.data = buf;
        this.window = window;
    }

    public ModularToClientPacket(FriendlyByteBuf buf) {
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
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && (this.window == 0 || this.window == player.containerMenu.containerId) && player.containerMenu instanceof ModularContainer modularContainer)
                modularContainer.getGuiInfo().getWidget(this.data.readInt()).receiveMessageFromServer(this.data);
        });
    }
}