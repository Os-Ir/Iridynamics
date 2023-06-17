package com.atodium.iridynamics.common.network;

import com.atodium.iridynamics.api.gui.IModularGuiHolder;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;

public class ModularGuiOpenPacket {
    private final FriendlyByteBuf holderPacket;
    private final FriendlyByteBuf[] parentHolderPackets;
    private final List<FriendlyByteBuf> updateData;
    private final int window;
    private final int[] args;

    public ModularGuiOpenPacket(FriendlyByteBuf holderPacket, FriendlyByteBuf[] parentHolderPacket, List<FriendlyByteBuf> updateData, int window, int[] args) {
        this.holderPacket = holderPacket;
        this.parentHolderPackets = parentHolderPacket;
        this.updateData = updateData;
        this.window = window;
        this.args = args;
    }

    public ModularGuiOpenPacket(FriendlyByteBuf buf) {
        this.holderPacket = new FriendlyByteBuf(Unpooled.copiedBuffer(buf.readBytes(buf.readInt())));
        int size = buf.readInt();
        this.parentHolderPackets = new FriendlyByteBuf[size];
        for (int i = 0; i < size; i++)
            this.parentHolderPackets[i] = new FriendlyByteBuf(Unpooled.copiedBuffer(buf.readBytes(buf.readInt())));
        size = buf.readInt();
        this.updateData = new ArrayList<>();
        for (int i = 0; i < size; i++)
            this.updateData.add(new FriendlyByteBuf(Unpooled.copiedBuffer(buf.readBytes(buf.readInt()))));
        this.window = buf.readInt();
        size = buf.readInt();
        this.args = new int[size];
        for (int i = 0; i < size; i++) this.args[i] = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.holderPacket.readableBytes());
        buf.writeBytes(this.holderPacket);
        buf.writeInt(this.parentHolderPackets.length);
        for (FriendlyByteBuf parentHolderPacket : this.parentHolderPackets) {
            buf.writeInt(parentHolderPacket.readableBytes());
            buf.writeBytes(parentHolderPacket);
        }
        buf.writeInt(this.updateData.size());
        this.updateData.forEach((data) -> {
            buf.writeInt(data.readableBytes());
            buf.writeBytes(data);
        });
        buf.writeInt(this.window);
        buf.writeInt(this.args.length);
        for (int arg : this.args) {
            buf.writeInt(arg);
        }
    }

    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ResourceLocation codecName = this.holderPacket.readResourceLocation();
            if (!ModularGuiInfo.CODEC.containsKey(codecName))
                throw new RuntimeException("The gui holder registry name is invalid");
            IModularGuiHolder<?>[] parentHolders = new IModularGuiHolder<?>[this.parentHolderPackets.length];
            for (int i = 0; i < parentHolders.length; i++) {
                ResourceLocation parentCodecName = this.parentHolderPackets[i].readResourceLocation();
                if (!ModularGuiInfo.CODEC.containsKey(parentCodecName))
                    throw new RuntimeException("The gui holder registry name is invalid");
                parentHolders[i] = ModularGuiInfo.CODEC.get(parentCodecName).readHolder(this.parentHolderPackets[i]);
            }
            ModularGuiInfo.openClientModularGui(this.window, ModularGuiInfo.CODEC.get(codecName).readHolder(this.holderPacket), parentHolders, this.updateData, this.args);
        });
    }
}