package com.atodium.iridynamics.api.gui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IGuiHolderCodec<T extends IModularGuiHolder<T>> {
    ResourceLocation getRegistryName();

    void writeHolder(FriendlyByteBuf tag, IModularGuiHolder<?> holder);

    @OnlyIn(Dist.CLIENT)
    T readHolder(FriendlyByteBuf tag);
}