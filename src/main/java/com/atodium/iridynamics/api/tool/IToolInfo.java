package com.atodium.iridynamics.api.tool;

import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
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
    UnorderedRegistry<ResourceLocation, IToolInfo> TOOL_INFO = new UnorderedRegistry<>();

    static void register(IToolInfo info) {
        TOOL_INFO.register(info.getRegistryName(), info);
    }

    static boolean isToolNonnullEquals(IToolInfo a, IToolInfo b) {
        if (a == null || b == null) return false;
        return a.getRegistryName().equals(b.getRegistryName());
    }

    static boolean isToolEquals(IToolInfo a, IToolInfo b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.getRegistryName().equals(b.getRegistryName());
    }

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