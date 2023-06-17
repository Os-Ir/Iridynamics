package com.atodium.iridynamics.api.tool;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ToolHarvestDisable implements IToolInfo {
    @Override
    public int getBlockBreakDamage() {
        return 0;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 0;
    }

    public ToolItem getToolItem(ItemStack stack) {
        return (ToolItem) stack.getItem();
    }
}