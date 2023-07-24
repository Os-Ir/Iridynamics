package com.atodium.iridynamics.common.tool;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.IIgnitable;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.heat.IHeat;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.heat.HeatModule;
import com.atodium.iridynamics.api.tool.IToolUsable;
import com.atodium.iridynamics.api.tool.ToolHarvestDisable;
import com.atodium.iridynamics.api.tool.ToolItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class ToolIgniter extends ToolHarvestDisable implements IToolUsable {
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(Iridynamics.MODID, "igniter");
    public static final ToolIgniter INSTANCE = new ToolIgniter();

    public static void igniteBlock(ItemStack igniter, IIgnitable ignitable, Direction direction) {
        IHeat heat = igniter.getCapability(HeatCapability.HEAT).orElseThrow(NullPointerException::new);
        if (ignitable.ignite(direction, heat.getTemperature())) {
            heat.increaseEnergy(-160000.0);
            if (heat.getTemperature() < HeatModule.AMBIENT_TEMPERATURE) heat.setTemperature(HeatModule.AMBIENT_TEMPERATURE);
        }
    }

    @Override
    public ResourceLocation getRegistryName() {
        return REGISTRY_NAME;
    }

    @Override
    public boolean validateMaterial(int index, MaterialBase material) {
        return false;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 25600;
    }

    @Override
    public int getInteractionDamage() {
        return 0;
    }

    @Override
    public int getContainerCraftDamage() {
        return 0;
    }

    @Override
    public float getAttackDamage() {
        return 0.0f;
    }

    @Override
    public float getAttackSpeed() {
        return -3.0f;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        IHeat cap = stack.getCapability(HeatCapability.HEAT).orElseThrow(NullPointerException::new);
        if (player.isCrouching() && cap.getTemperature() <= 575) {
            cap.increaseEnergy(20000);
            if (cap.getTemperature() > 600) {
                cap.setTemperature(600);
            }
            this.getToolItem(stack).damageItem(stack, 100);
        }
        return InteractionResultHolder.success(stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        ToolItem item = this.getToolItem(stack);
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
}