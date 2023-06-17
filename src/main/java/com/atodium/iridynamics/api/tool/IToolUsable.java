package com.atodium.iridynamics.api.tool;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;


public interface IToolUsable {
    int MAX_USE_TICK = 72000;

    InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand);

    default int getUseDuration(ItemStack stack) {
        return MAX_USE_TICK;
    }

    default UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    default InteractionResult useOn(UseOnContext context) {
        return InteractionResult.CONSUME;
    }

    default void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {

    }
}