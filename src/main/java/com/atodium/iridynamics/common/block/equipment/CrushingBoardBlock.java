package com.atodium.iridynamics.common.block.equipment;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.tool.MaterialToolItem;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.atodium.iridynamics.common.blockEntity.equipment.CrushingBoardBlockEntity;
import com.atodium.iridynamics.common.tool.ToolHammer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

public class CrushingBoardBlock extends Block implements EntityBlock {
    public static final VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    public CrushingBoardBlock(Block.Properties properties) {
        super(properties);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.CRUSHING_BOARD.get() ? ITickable.ticker() : null;
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
        level.getBlockEntity(pos, ModBlockEntities.CRUSHING_BOARD.get()).ifPresent((board) -> {
            if (board.isEmpty()) player.setItemInHand(hand, board.addItem(stack));
            else {
                if (result.getDirection() == Direction.UP && stack.getItem() instanceof MaterialToolItem toolItem && toolItem.getToolInfo() == ToolHammer.INSTANCE) {
                    if (board.crush(player)) toolItem.damageItem(stack, ToolHammer.INSTANCE.getInteractionDamage());
                } else ItemHandlerHelper.giveItemToPlayer(player, board.takeItem());
            }
        });
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level.isClientSide) return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (!player.isCreative() && state.canHarvestBlock(level, pos, player))
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModBlocks.CRUSHING_BOARD.get()));
        level.getBlockEntity(pos, ModBlockEntities.CRUSHING_BOARD.get()).ifPresent((board) -> ItemHandlerHelper.giveItemToPlayer(player, board.getInventory().getStackInSlot(0)));
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrushingBoardBlockEntity(pos, state);
    }
}