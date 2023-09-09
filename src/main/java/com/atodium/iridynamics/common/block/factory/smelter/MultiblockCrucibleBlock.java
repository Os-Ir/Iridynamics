package com.atodium.iridynamics.common.block.factory.smelter;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.atodium.iridynamics.common.blockEntity.factory.smelter.MultiblockCrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

public class MultiblockCrucibleBlock extends Block implements EntityBlock {
    public static final VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 15.9, 16.0);

    public MultiblockCrucibleBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.MULTIBLOCK_CRUCIBLE.get() ? ITickable.ticker() : null;
    }

    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof ItemEntity itemEntity && itemEntity.isAlive())
            level.getBlockEntity(pos, ModBlockEntities.MULTIBLOCK_CRUCIBLE.get()).ifPresent((crucible) -> {
                if (crucible.tryAddItem(itemEntity.getItem()).isEmpty()) itemEntity.discard();
            });
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level.isClientSide) return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (!player.isCreative() && state.canHarvestBlock(level, pos, player))
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModBlocks.MULTIBLOCK_CRUCIBLE.get()));
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MultiblockCrucibleBlockEntity(pos, state);
    }
}