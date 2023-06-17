package com.atodium.iridynamics.api.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public interface IModularGuiHolder<T extends IModularGuiHolder<T>> {
    IGuiHolderCodec<T> getCodec();

    Component getTitle(Player player);

    ModularGuiInfo createGuiInfo(Player player);

    default int[] getTasksToExecute(ModularContainer container) {
        return new int[0];
    }

    default void executeTask(ModularContainer container, int id) {

    }

    @OnlyIn(Dist.CLIENT)
    default void executeRenderTask(ModularScreen screen) {

    }

    default void openGui(Player player, int... args) {
        if (player instanceof ServerPlayer serverPlayer) ModularGuiInfo.openModularGui(this, serverPlayer, args);
        else throw new IllegalStateException("Can not open gui on client side");
    }
}