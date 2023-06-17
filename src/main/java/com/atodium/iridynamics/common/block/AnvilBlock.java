package com.atodium.iridynamics.common.block;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.tool.IToolInfo;
import com.atodium.iridynamics.api.tool.MaterialToolItem;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.blockEntity.AnvilBlockEntity;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.atodium.iridynamics.common.tool.ToolChisel;
import com.atodium.iridynamics.common.tool.ToolHammer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

public class AnvilBlock extends Block implements EntityBlock {
    public static final DirectionProperty DIRECTION = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty HEIGHT_LEFT = IntegerProperty.create("height_left", 0, 7);
    public static final IntegerProperty HEIGHT_RIGHT = IntegerProperty.create("height_right", 0, 7);

    public static final VoxelShape[] SHAPES_NS = {
            box(0.0, 0.0, 3.0, 16.0, 9.0, 13.0),
            box(0.0, 0.0, 3.0, 16.0, 10.0, 13.0),
            box(0.0, 0.0, 3.0, 16.0, 11.0, 13.0),
            box(0.0, 0.0, 3.0, 16.0, 12.0, 13.0),
            box(0.0, 0.0, 3.0, 16.0, 13.0, 13.0),
            box(0.0, 0.0, 3.0, 16.0, 14.0, 13.0),
            box(0.0, 0.0, 3.0, 16.0, 15.0, 13.0),
            box(0.0, 0.0, 3.0, 16.0, 16.0, 13.0)
    };
    public static final VoxelShape[] SHAPES_WE = {
            box(3.0, 0.0, 0.0, 13.0, 9.0, 16.0),
            box(3.0, 0.0, 0.0, 13.0, 10.0, 16.0),
            box(3.0, 0.0, 0.0, 13.0, 11.0, 16.0),
            box(3.0, 0.0, 0.0, 13.0, 12.0, 16.0),
            box(3.0, 0.0, 0.0, 13.0, 13.0, 16.0),
            box(3.0, 0.0, 0.0, 13.0, 14.0, 16.0),
            box(3.0, 0.0, 0.0, 13.0, 15.0, 16.0),
            box(3.0, 0.0, 0.0, 13.0, 16.0, 16.0)
    };

    public AnvilBlock(Block.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.WEST).setValue(HEIGHT_LEFT, 0).setValue(HEIGHT_RIGHT, 0));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.ANVIL.get() ? ITickable.ticker() : null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int height = Math.max(state.getValue(HEIGHT_LEFT), state.getValue(HEIGHT_RIGHT));
        switch (state.getValue(DIRECTION)) {
            case NORTH, SOUTH -> {
                return SHAPES_NS[height];
            }
            case WEST, EAST -> {
                return SHAPES_WE[height];
            }
        }
        return SHAPES_NS[0];
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        level.getBlockEntity(pos, ModBlockEntities.ANVIL.get()).ifPresent((anvil) -> {
            Vec3 location = MathUtil.transformPosition(MathUtil.minus(result.getLocation(), pos), state.getValue(DIRECTION));
            if (result.getDirection() == Direction.UP && location.x > 0.125) {
                ItemStack stack = player.getItemInHand(hand);
                Item item = stack.getItem();
                int slot = location.x > 0.5625 ? 1 : 0;
                if (item instanceof MaterialToolItem toolItem && MathUtil.between(location.x, 0.125, 1.0) && MathUtil.between(location.z, 0.3125, 0.75)) {
                    IToolInfo info = toolItem.getToolInfo();
                    if (info == ToolHammer.INSTANCE) {
                        if (anvil.hit(slot, (((int) Math.floor(location.x * 16)) - 2) % 7, ((int) Math.floor(location.z * 16)) - 5, true))
                            toolItem.damageItem(stack, info.getInteractionDamage());
                    } else if (info == ToolChisel.INSTANCE) {
                        if (anvil.carve(slot, (((int) Math.floor(location.x * 16)) - 2) % 7, ((int) Math.floor(location.z * 16)) - 5))
                            toolItem.damageItem(stack, info.getInteractionDamage());
                    }
                } else {
                    ItemStack take = anvil.takeItem(slot);
                    if (take.isEmpty()) player.setItemInHand(hand, anvil.putItem(slot, stack));
                    else ItemHandlerHelper.giveItemToPlayer(player, take);
                }
            }
        });
        return InteractionResult.CONSUME;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(DIRECTION, context.getHorizontalDirection().getOpposite());
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
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level.isClientSide) return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        boolean harvest = state.canHarvestBlock(level, pos, player);
        if (!player.isCreative() && harvest)
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModBlocks.ANVIL.get()));
        level.getBlockEntity(pos, ModBlockEntities.ANVIL.get()).ifPresent((anvil) -> {
            ItemHandlerHelper.giveItemToPlayer(player, anvil.getInventory().left());
            ItemHandlerHelper.giveItemToPlayer(player, anvil.getInventory().right());
        });
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AnvilBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(DIRECTION).add(HEIGHT_LEFT).add(HEIGHT_RIGHT));
    }
}