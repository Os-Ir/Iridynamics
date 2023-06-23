package com.atodium.iridynamics.common.block.equipment;

import com.atodium.iridynamics.api.blockEntity.IIgnitable;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.module.ItemHeatModule;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.blockEntity.equipment.ForgeBlockEntity;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.atodium.iridynamics.common.blockEntity.PileBlockEntity;
import com.atodium.iridynamics.common.item.ModItems;
import com.atodium.iridynamics.common.tool.ToolIgniter;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

public class ForgeBlock extends Block implements EntityBlock {
    public static final DirectionProperty DIRECTION = BlockStateProperties.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    public ForgeBlock(Block.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.WEST));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.FORGE.get() ? ITickable.ticker() : null;
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
        level.getBlockEntity(pos, ModBlockEntities.FORGE.get()).ifPresent((forge) -> {
            System.out.println("Forge: " + (forge.getTemperature() - ItemHeatModule.AMBIENT_TEMPERATURE) + "K");
            ItemStack stack = player.getItemInHand(hand);
            Item item = stack.getItem();
            Vec3 location = MathUtil.transformPosition(MathUtil.minus(result.getLocation(), pos), state.getValue(DIRECTION));
            if (result.getDirection() == Direction.UP) {
                if (PileBlockEntity.PILE_ITEM.containsKey(item)) {
                    BlockPos posBelow = pos.below();
                    BlockState stateBelow = level.getBlockState(posBelow);
                    if (stateBelow.isAir()) {
                        if (level.setBlockAndUpdate(posBelow, ModBlocks.PILE.get().defaultBlockState()))
                            level.getBlockEntity(posBelow, ModBlockEntities.PILE.get()).ifPresent((pile) -> {
                                if (pile.setup(item) && !player.isCreative()) stack.shrink(1);
                            });
                    } else if (stateBelow.is(ModBlocks.PILE.get()))
                        level.getBlockEntity(posBelow, ModBlockEntities.PILE.get()).ifPresent((pile) -> {
                            if (pile.addContent(item) && !player.isCreative()) stack.shrink(1);
                        });
                    else if (stateBelow.is(ModBlocks.FUEL.get()))
                        level.getBlockEntity(posBelow, ModBlockEntities.FUEL.get()).ifPresent((fuel) -> fuel.addFuel(stack));
                } else if (item == ModItems.IGNITER.get()) {
                    BlockPos posBelow = pos.below();
                    BlockState stateBelow = level.getBlockState(posBelow);
                    if (level.getBlockEntity(posBelow) instanceof IIgnitable ignitable)
                        ToolIgniter.igniteBlock(stack, ignitable, Direction.UP);
                } else {
                    int slot = location.x > 0.5 ? 1 : 0;
                    ForgeBlockEntity.Inventory inventory = forge.getInventory();
                    ItemStack take = inventory.take(slot);
                    if (take.isEmpty()) player.setItemInHand(hand, inventory.put(slot, stack));
                    else ItemHandlerHelper.giveItemToPlayer(player, take);
                    forge.markDirty();
                    forge.markForSync();
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
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModBlocks.FORGE.get()));
        level.getBlockEntity(pos, ModBlockEntities.FORGE.get()).ifPresent((forge) -> {
            ItemHandlerHelper.giveItemToPlayer(player, forge.getInventory().left());
            ItemHandlerHelper.giveItemToPlayer(player, forge.getInventory().right());
        });
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ForgeBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(DIRECTION));
    }
}