package com.atodium.iridynamics.api.gui.impl;

import com.atodium.iridynamics.api.gui.IGuiHolderCodec;
import com.atodium.iridynamics.api.gui.IModularGuiHolder;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockEntityCodec<T extends BlockEntity> implements IGuiHolderCodec<IBlockEntityGuiHolder<T>> {
    private final ResourceLocation registryName;

    private BlockEntityCodec(ResourceLocation registryName) {
        this.registryName = registryName;
        ModularGuiInfo.CODEC.register(registryName, this);
    }

    public static <T extends BlockEntity> BlockEntityCodec<T> createCodec(ResourceLocation registryName) {
        return new BlockEntityCodec<>(registryName);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    @Override
    public void writeHolder(FriendlyByteBuf buf, IModularGuiHolder<?> holder) {
        if (holder instanceof BlockEntity entity) buf.writeBlockPos(entity.getBlockPos());
        else throw new IllegalArgumentException("The ModularGuiHolder is not BlockEntity");
    }

    @SuppressWarnings("unchecked")
    @OnlyIn(Dist.CLIENT)
    @Override
    public IBlockEntityGuiHolder<T> readHolder(FriendlyByteBuf buf) {
        return (IBlockEntityGuiHolder<T>) Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos());
    }
}