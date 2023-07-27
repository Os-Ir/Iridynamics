package com.atodium.iridynamics.api.module;

import com.atodium.iridynamics.api.tool.IToolInfo;
import com.atodium.iridynamics.api.tool.ToolItem;
import com.atodium.iridynamics.api.item.ItemDelegate;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ToolModule {
    public static final UnorderedRegistry<ResourceLocation, IToolInfo> TOOL_INFO = new UnorderedRegistry<>();
    public static final UnorderedRegistry<ItemDelegate, IToolInfo> ITEM_TOOL_INFO = new UnorderedRegistry<>();

    public static void clearItem(ItemStack stack) {
        stack.setCount(0);
    }

    public static IToolInfo getItemToolInfo(ItemStack stack) {
        if (stack.getItem() instanceof ToolItem toolItem) return toolItem.getToolInfo();
        ItemDelegate item = ItemDelegate.of(stack);
        if (ITEM_TOOL_INFO.containsKey(item)) return ITEM_TOOL_INFO.get(item);
        return null;
    }

    public static void toolCraftingDamage(ItemStack stack) {
        if (stack.getItem() instanceof ToolItem toolItem)
            toolItem.damageItem(stack, toolItem.getToolInfo().getContainerCraftDamage());
        else if (ITEM_TOOL_INFO.containsKey(ItemDelegate.of(stack))) clearItem(stack);
    }

    public static boolean isTool(ItemStack stack) {
        return stack.getItem() instanceof ToolItem || ITEM_TOOL_INFO.containsKey(ItemDelegate.of(stack));
    }

    public static void registerItem(ItemDelegate item, IToolInfo info) {
        ITEM_TOOL_INFO.register(item, info);
    }

    public static IToolInfo getItemToolInfo(ItemDelegate item) {
        return ITEM_TOOL_INFO.get(item);
    }

    public static void register(IToolInfo info) {
        TOOL_INFO.register(info.getRegistryName(), info);
    }

    public static boolean isToolNonnullEquals(IToolInfo a, IToolInfo b) {
        if (a == null || b == null) return false;
        return a.getRegistryName().equals(b.getRegistryName());
    }

    public static boolean isToolEquals(IToolInfo a, IToolInfo b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.getRegistryName().equals(b.getRegistryName());
    }
}