package com.atodium.iridynamics.common.block;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.common.blockEntity.ChuteBlockEntity;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;

public class ChuteBlock extends Block implements EntityBlock {
    public static final DirectionProperty DIRECTION = DirectionProperty.create("direction", (direction) -> direction != Direction.UP);

    public ChuteBlock(Block.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.DOWN));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.CHUTE.get() ? ITickable.ticker() : null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        level.getBlockEntity(pos, ModBlockEntities.CHUTE.get()).ifPresent((chute) -> chute.openGui(player));
        return InteractionResult.CONSUME;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = Direction.DOWN;
        if (context.getClickedFace().get2DDataValue() >= 0) direction = context.getClickedFace().getOpposite();
        System.out.println(direction);
        return defaultBlockState().setValue(DIRECTION, direction);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot) {
        if (state.getValue(DIRECTION) == Direction.DOWN) return state;
        return state.setValue(DIRECTION, rot.rotate(state.getValue(DIRECTION)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (state.getValue(DIRECTION) == Direction.DOWN) return state;
        return state.rotate(mirror.getRotation(state.getValue(DIRECTION)));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level.isClientSide) return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        boolean harvest = state.canHarvestBlock(level, pos, player);
        if (!player.isCreative() && harvest)
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModBlocks.CHUTE.get()));
        level.getBlockEntity(pos, ModBlockEntities.CHUTE.get()).ifPresent((chute) -> {
            int slots = chute.getInventory().getSlots();
            for (int i = 0; i < slots; i++) ItemHandlerHelper.giveItemToPlayer(player, chute.getInventory().get(i));
        });
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChuteBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(DIRECTION));
    }
}