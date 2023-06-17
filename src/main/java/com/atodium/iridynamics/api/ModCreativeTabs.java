package com.atodium.iridynamics.api;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.item.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModCreativeTabs {
    public static final CreativeModeTab BLOCK = new CreativeModeTab(Iridynamics.MODID + "_block") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.FORGE.get());
        }
    };

    public static final CreativeModeTab ITEM = new CreativeModeTab(Iridynamics.MODID + "_item") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.IGNITER.get());
        }
    };

    public static final CreativeModeTab MATERIAL = new CreativeModeTab(Iridynamics.MODID + "_material") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.COPPER_INGOT);
        }
    };
}