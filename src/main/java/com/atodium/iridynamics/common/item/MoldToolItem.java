package com.atodium.iridynamics.common.item;

import com.atodium.iridynamics.api.material.ModSolidShapes;
import com.atodium.iridynamics.api.material.SolidShape;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class MoldToolItem extends Item {
    public static final List<SolidShape> GENERATED_MOLDS = Lists.newArrayList();

    public MoldToolItem(Properties properties) {
        super(properties);
    }

    public static SolidShape getMoldShape(ItemStack stack) {
        if (stack.getItem() instanceof MoldToolItem) {
            CompoundTag tag = stack.getOrCreateTagElement("material");
            if (!tag.contains("shape")) tag.putString("shape", ModSolidShapes.HAMMER_HEAD.getName());
            return SolidShape.getShapeByName(tag.getString("shape"));
        }
        return null;
    }


    public ItemStack createItemStack(SolidShape shape) {
        return this.createItemStack(shape, 1);
    }

    public ItemStack createItemStack(SolidShape shape, int count) {
        ItemStack stack = new ItemStack(this, count);
        stack.getOrCreateTagElement("material").putString("shape", shape.getName());
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(new TextComponent(ChatFormatting.AQUA + getMoldShape(stack).getLocalizedName()));
        tooltip.add(new TextComponent(ChatFormatting.DARK_RED + I18n.get("iridynamics.info.mold_tool.disposable")));
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
        if (this.allowdedIn(tab)) GENERATED_MOLDS.forEach((shape) -> list.add(this.createItemStack(shape)));
    }
}