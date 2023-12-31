package com.atodium.iridynamics.api.tool;

import com.atodium.iridynamics.api.material.type.MaterialBase;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface IToolInfo {
    ResourceLocation getRegistryName();

    boolean validateMaterial(int index, MaterialBase material);

    int getMaxDamage(ItemStack stack);

    int getBlockBreakDamage();

    int getInteractionDamage();

    int getContainerCraftDamage();

    float getAttackDamage();

    float getAttackSpeed();

    boolean isCorrectToolForDrops(ItemStack stack, BlockState state);

    float getDestroySpeed(ItemStack stack, BlockState state);

    default Map<Integer, Pair<String, MaterialBase>> getDefaultMaterial() {
        return Collections.emptyMap();
    }

    @OnlyIn(Dist.CLIENT)
    default void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {

    }

    default ItemStack getBrokenItemStack(ItemStack stack) {
        return ItemStack.EMPTY;
    }
}