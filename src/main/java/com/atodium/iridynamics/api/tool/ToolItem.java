package com.atodium.iridynamics.api.tool;

import com.atodium.iridynamics.api.module.ToolModule;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class ToolItem extends Item {
    protected final IToolInfo toolInfo;
    protected final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public ToolItem(Item.Properties properties, IToolInfo toolInfo) {
        super(properties);
        this.toolInfo = toolInfo;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", toolInfo.getAttackDamage(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", toolInfo.getAttackSpeed(), AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    public IToolInfo getToolInfo() {
        return this.toolInfo;
    }

    public static CompoundTag getToolBaseTag(ItemStack stack) {
        return stack.getOrCreateTagElement("tool_info");
    }

    public ItemStack createItemStack() {
        return new ItemStack(this);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    @Override
    @SuppressWarnings("deprecation")
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (!level.isClientSide) this.damageItem(stack, this.toolInfo.getBlockBreakDamage());
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (this.toolInfo.isCorrectToolForDrops(stack, state)) return this.toolInfo.getDestroySpeed(stack, state);
        return 1;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return this.toolInfo.isCorrectToolForDrops(stack, state);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        this.toolInfo.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return this.getToolDamage(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0f - 13.0f * this.getToolDamage(stack) / this.getToolMaxDamage(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float value = ((float) this.getToolDamage(stack)) / this.getToolMaxDamage(stack);
        if (value >= 0.7 && value < 0.9) {
            return 0xffff00;
        }
        if (value >= 0.9) {
            return 0xff0000;
        }
        return 0x00ff00;
    }

    public boolean disableItemDamage(ItemStack stack) {
        return this.toolInfo.getMaxDamage(stack) == 0;
    }

    public boolean damageItem(ItemStack stack, int value) {
        if (this.disableItemDamage(stack)) {
            return false;
        }
        CompoundTag tag = getToolBaseTag(stack);
        int maxDamage = this.getToolMaxDamage(stack);
        int damage = this.getToolDamage(stack);
        if (damage + value < maxDamage) {
            tag.putInt("damage", damage + value);
            return false;
        }
        ToolModule.clearItem(stack);
        return true;
    }

    public int getToolMaxDamage(ItemStack stack) {
        return this.toolInfo.getMaxDamage(stack);
    }

    public int getToolDamage(ItemStack stack) {
        if (this.disableItemDamage(stack)) {
            return 0;
        }
        CompoundTag nbt = getToolBaseTag(stack);
        if (nbt.contains("damage")) {
            return nbt.getInt("damage");
        }
        return 0;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (this.toolInfo instanceof IToolUsable) {
            return ((IToolUsable) this.toolInfo).use(world, player, hand);
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        if (this.toolInfo instanceof IToolUsable) {
            return ((IToolUsable) this.toolInfo).getUseDuration(stack);
        }
        return 0;
    }


    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        if (this.toolInfo instanceof IToolUsable) {
            return ((IToolUsable) this.toolInfo).getUseAnimation(stack);
        }
        return UseAnim.NONE;
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (this.toolInfo instanceof IToolUsable) {
            return ((IToolUsable) this.toolInfo).useOn(context);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (this.toolInfo instanceof IToolUsable) {
            ((IToolUsable) this.toolInfo).releaseUsing(stack, level, entity, timeLeft);
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab tag, NonNullList<ItemStack> items) {
        if (this.allowdedIn(tag)) {
            items.add(this.createItemStack());
        }
    }
}