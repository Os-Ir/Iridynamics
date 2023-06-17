package com.atodium.iridynamics.api.tool;

import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.util.data.GreekAlphabet;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public abstract class MaterialToolBase implements IToolInfo {
    public static final UnorderedRegistry<ResourceLocation, MaterialToolBase> MATERIAL_TOOL = new UnorderedRegistry<>();

    public static void register(MaterialToolBase tool) {
        MATERIAL_TOOL.register(tool.getRegistryName(), tool);
    }

    public abstract ToolRenderInfo getRenderInfo();

    public abstract boolean validateMaterial(int index, MaterialBase material);

    @Override
    public int getMaxDamage(ItemStack stack) {
        MaterialBase material = MaterialToolItem.getToolMaterial(stack, 0);
        return material != null ? material.getDurability() : 0;
    }

    @Override
    public int getBlockBreakDamage() {
        return 200;
    }

    @Override
    public int getInteractionDamage() {
        return 50;
    }

    @Override
    public int getContainerCraftDamage() {
        return 800;
    }

    @Override
    public int getEntityHitDamage() {
        return 100;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        MaterialBase material = MaterialToolItem.getToolMaterial(stack, 0);
        return material == null || !this.isCorrectToolForDrops(stack, state) ? 0.0f : material.getEfficiency();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        MaterialToolItem.getToolAllMaterial(stack)
                .forEach((index, pair) -> tooltip.add(new TextComponent(ChatFormatting.BLUE
                        + I18n.get("iridynamics.tool." + pair.getLeft()) + " [" + ChatFormatting.AQUA
                        + GreekAlphabet.getLowercase(index + 1) + ChatFormatting.BLUE + "] : "
                        + ChatFormatting.GREEN + pair.getRight().getLocalizedName())));
        MaterialToolItem item = this.getToolItem(stack);
        int damage = item.getToolDamage(stack);
        int maxDamage = item.getToolMaxDamage(stack);
        double durability = ((double) damage) / maxDamage;
        ChatFormatting color = ChatFormatting.GREEN;
        if (durability >= 0.7 && durability < 0.9) {
            color = ChatFormatting.YELLOW;
        } else if (durability >= 0.9 && durability < 0.97) {
            color = ChatFormatting.RED;
        } else if (durability >= 0.97) {
            color = System.currentTimeMillis() % 500 < 250 ? ChatFormatting.RED : ChatFormatting.WHITE;
        }
        int num = (int) ((1.0 - durability) * 20.0);
        String text = color + "=".repeat(Math.max(0, num)) + ">" + ChatFormatting.WHITE + "-".repeat(Math.max(0, 19 - num));
        tooltip.add(new TextComponent(color + String.valueOf(maxDamage - damage) + ChatFormatting.WHITE + " / " + maxDamage + " " + text));
    }

    protected MaterialToolItem getToolItem(ItemStack stack) {
        return (MaterialToolItem) stack.getItem();
    }
}