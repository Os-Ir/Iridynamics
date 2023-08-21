package com.atodium.iridynamics.common.block.factory;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.pipe.LiquidPipeModule;
import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.api.util.data.PosDirection;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.atodium.iridynamics.common.blockEntity.factory.LiquidPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Random;

public class LiquidPipeBlock extends Block implements EntityBlock {
    public static final IntegerProperty CONNECTION = IntegerProperty.create("connection", 0, 0b111111);
    public static final int[][] DIRECTION_RANGE = new int[][]{{3, 13, 0, 3, 3, 13}, {3, 13, 13, 16, 3, 13}, {3, 13, 3, 13, 0, 3}, {3, 13, 3, 13, 13, 16}, {0, 3, 3, 13, 3, 13}, {13, 16, 3, 13, 3, 13}};
    public static final VoxelShape[] SHAPE = new VoxelShape[128];

    private final MaterialBase material;

    public LiquidPipeBlock(Block.Properties properties, MaterialBase material) {
        super(properties);
        this.material = material;
        this.registerDefaultState(this.getStateDefinition().any().setValue(CONNECTION, connectionIndex()));
    }

    public static int connectionIndex(Direction... connected) {
        int index = 0;
        for (Direction direction : connected) index |= (1 << direction.get3DDataValue());
        return index;
    }

    public static boolean isConnected(int index, Direction direction) {
        return (index & (1 << direction.get3DDataValue())) != 0;
    }

    public static int changeConnection(int index, Direction direction) {
        return index ^ (1 << direction.get3DDataValue());
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.LIQUID_PIPE.get() ? ITickable.ticker() : null;
    }

    public MaterialBase material() {
        return this.material;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = state.getValue(CONNECTION);
        if (SHAPE[index] == null) {
            int minX = 3;
            int minY = 3;
            int minZ = 3;
            int maxX = 13;
            int maxY = 13;
            int maxZ = 13;
            for (Direction direction : DataUtil.DIRECTIONS)
                if (isConnected(index, direction)) {
                    minX = Math.min(minX, DIRECTION_RANGE[direction.get3DDataValue()][0]);
                    minY = Math.min(minY, DIRECTION_RANGE[direction.get3DDataValue()][2]);
                    minZ = Math.min(minZ, DIRECTION_RANGE[direction.get3DDataValue()][4]);
                    maxX = Math.max(maxX, DIRECTION_RANGE[direction.get3DDataValue()][1]);
                    maxY = Math.max(maxY, DIRECTION_RANGE[direction.get3DDataValue()][3]);
                    maxZ = Math.max(maxZ, DIRECTION_RANGE[direction.get3DDataValue()][5]);
                }
            SHAPE[index] = box(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return SHAPE[index];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(CONNECTION, connectionIndex(context.getClickedFace().getOpposite()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state != oldState) level.scheduleTick(pos, this, 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        LiquidPipeModule.updatePipeBlock(level, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        ServerLevel serverLevel = (ServerLevel) level;
        if (player.getItemInHand(hand).is(Items.STICK))
            LiquidPipeModule.tryAddFluid(serverLevel, new PosDirection(pos, result.getDirection()), Fluids.WATER, 500);
        else if (player.getItemInHand(hand).is(Items.IRON_INGOT))
            level.getBlockEntity(pos, ModBlockEntities.LIQUID_PIPE.get()).ifPresent((pipe) -> pipe.updateBlockState(changeConnection(state.getValue(CONNECTION), result.getDirection())));
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level.isClientSide) return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (!player.isCreative() && state.canHarvestBlock(level, pos, player))
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(this));
        LiquidPipeModule.removePipeBlock((ServerLevel) level, pos);
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LiquidPipeBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(CONNECTION));
    }
}