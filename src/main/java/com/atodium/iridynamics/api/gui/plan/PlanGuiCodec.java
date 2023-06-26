package com.atodium.iridynamics.api.gui.plan;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.gui.IGuiHolderCodec;
import com.atodium.iridynamics.api.gui.IModularGuiHolder;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class PlanGuiCodec implements IGuiHolderCodec<PlanGuiHolder> {
    public static final PlanGuiCodec INSTANCE = new PlanGuiCodec();
    public static final ResourceLocation REGISTRY_NAME = Iridynamics.rl("plan");

    public PlanGuiCodec() {

    }

    public static void init() {
        ModularGuiInfo.CODEC.register(REGISTRY_NAME, INSTANCE);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return REGISTRY_NAME;
    }

    @Override
    public void writeHolder(FriendlyByteBuf buf, IModularGuiHolder<?> holder) {
        buf.writeBlockPos(((PlanGuiHolder) holder).planInfoProvider().getBlockPos());
    }

    @Override
    public PlanGuiHolder readHolder(FriendlyByteBuf buf) {
        return new PlanGuiHolder((IPlanBlockEntity) Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()));
    }
}