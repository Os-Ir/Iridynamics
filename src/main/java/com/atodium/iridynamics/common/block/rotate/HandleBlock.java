package com.atodium.iridynamics.common.block.rotate;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.rotate.RotateModule;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.atodium.iridynamics.common.blockEntity.rotate.HandleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Random;

public class HandleBlock extends Block implements EntityBlock {
    public static final DirectionProperty DIRECTION = BlockStateProperties.FACING;

    public HandleBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.HANDLE.get() ? ITickable.ticker() : null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (result.getDirection() == state.getValue(DIRECTION).getOpposite()) {
            Vec3 location = MathUtil.transformPosition(MathUtil.minus(result.getLocation(), pos), Direction.NORTH, state.getValue(DIRECTION));
            boolean isLeft = location.x <= 0.5;
            level.getBlockEntity(pos, ModBlockEntities.HANDLE.get()).ifPresent((handle) -> handle.handle(isLeft));
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        System.out.println(context.getHorizontalDirection());
        return defaultBlockState().setValue(DIRECTION, context.getHorizontalDirection());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(DIRECTION, rot.rotate(state.getValue(DIRECTION)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(DIRECTION)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state != oldState) level.scheduleTick(pos, this, 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        level.getBlockEntity(pos, ModBlockEntities.HANDLE.get()).ifPresent((handle) -> handle.setupRotate(level));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level.isClientSide) return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        RotateModule.removeRotateBlock((ServerLevel) level, pos);
        boolean harvest = state.canHarvestBlock(level, pos, player);
        if (!player.isCreative() && harvest)
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModBlocks.HANDLE.get()));
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandleBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(DIRECTION));
    }
}