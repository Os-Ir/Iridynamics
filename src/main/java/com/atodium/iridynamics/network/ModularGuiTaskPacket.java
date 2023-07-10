package com.atodium.iridynamics.network;

import com.atodium.iridynamics.api.gui.ModularContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ModularGuiTaskPacket {
    private final int window;
    private final int[] tasks;

    public ModularGuiTaskPacket(int window, int[] tasks) {
        this.window = window;
        this.tasks = tasks;
    }

    public ModularGuiTaskPacket(FriendlyByteBuf buf) {
        this.window = buf.readInt();
        int length = buf.readInt();
        this.tasks = new int[length];
        for (int i = 0; i < length; i++) this.tasks[i] = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.window);
        buf.writeInt(this.tasks.length);
        for (int id : this.tasks) buf.writeInt(id);
    }

    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && (this.window == 0 || this.window == player.containerMenu.containerId) && player.containerMenu instanceof ModularContainer modularContainer)
                for (int id : this.tasks) modularContainer.getGuiHolder().executeTask(modularContainer, id);
        });
    }
}