package com.atodium.iridynamics.common.block.rotate;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.rotate.RotateModule;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.atodium.iridynamics.common.blockEntity.rotate.AxleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Random;

public class AxleBlock extends Block implements EntityBlock {
    public static final DirectionProperty DIRECTION = BlockStateProperties.FACING;
    public static final VoxelShape SHAPE_NS = box(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);
    public static final VoxelShape SHAPE_WE = box(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);
    public static final VoxelShape SHAPE_UD = box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

    private final AxleType type;

    public AxleBlock(BlockBehaviour.Properties properties, AxleType type) {
        super(properties);
        this.type = type;
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH));
    }

    public AxleType axleType() {
        return this.type;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.AXLE.get() ? ITickable.ticker() : null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch (state.getValue(DIRECTION)) {
            case NORTH, SOUTH -> {
                return SHAPE_NS;
            }
            case WEST, EAST -> {
                return SHAPE_WE;
            }
            default -> {
                return SHAPE_UD;
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(DIRECTION, context.getClickedFace().getOpposite());
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
        level.getBlockEntity(pos, ModBlockEntities.AXLE.get()).ifPresent((axle) -> axle.setupRotate(level));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level.isClientSide) return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        RotateModule.removeRotateBlock((ServerLevel) level, pos);
        boolean harvest = state.canHarvestBlock(level, pos, player);
        if (!player.isCreative() && harvest)
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(state.getBlock()));
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AxleBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(DIRECTION));
    }

    public enum AxleType {
        WOOD(RotateModule.WOOD_MAX_ANGULAR_VELOCITY, 10.0, 0.4), BRONZE(RotateModule.BRONZE_MAX_ANGULAR_VELOCITY, 50.0, 0.2), STEEL(RotateModule.STEEL_MAX_ANGULAR_VELOCITY, 40.0, 0.1);

        private final double maxAngularVelocity, inertia, friction;

        AxleType(double maxAngularVelocity, double inertia, double friction) {
            this.maxAngularVelocity = maxAngularVelocity;
            this.inertia = inertia;
            this.friction = friction;
        }

        public double maxAngularVelocity() {
            return this.maxAngularVelocity;
        }

        public double inertia() {
            return this.inertia;
        }

        public double friction() {
            return this.friction;
        }

        public AxleBlock block() {
            switch (this) {
                case WOOD -> {
                    return (AxleBlock) ModBlocks.WOOD_AXLE.get();
                }
                case BRONZE -> {
                    return (AxleBlock) ModBlocks.BRONZE_AXLE.get();
                }
                case STEEL -> {
                    return (AxleBlock) ModBlocks.STEEL_AXLE.get();
                }
            }
            throw new IllegalArgumentException("Illegal axle type [ " + this + " ]");
        }
    }
}