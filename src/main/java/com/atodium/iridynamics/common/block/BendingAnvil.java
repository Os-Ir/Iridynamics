package com.atodium.iridynamics.common.block;

import com.atodium.iridynamics.api.material.MaterialEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

public class BendingAnvil extends Block {
    public static final VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);

    public BendingAnvil(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);
        if (MaterialEntry.containsMaterialEntry(stack)) {
            MaterialEntry entry = MaterialEntry.getItemMaterialEntry(stack);
            if (entry.shape().hasBendingResult()) {
                stack.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, MaterialEntry.getMaterialItemStack(entry.shape().getBendingResult(), entry.material()));
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level.isClientSide) return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        boolean harvest = state.canHarvestBlock(level, pos, player);
        if (!player.isCreative() && harvest)
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModBlocks.BENDING_ANVIL.get()));
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}