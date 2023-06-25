package com.atodium.iridynamics.common.block.equipment;

import com.atodium.iridynamics.api.tool.MaterialToolItem;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.atodium.iridynamics.common.blockEntity.equipment.CarvingTableBlockEntity;
import com.atodium.iridynamics.common.tool.ToolChisel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

public class CarvingTableBlock extends Block implements EntityBlock {
    public static final IntegerProperty HEIGHT = IntegerProperty.create("height", 0, 4);
    public static final VoxelShape[] SHAPES = {
            box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)
    };

    public CarvingTableBlock(Block.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(HEIGHT, 0));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(HEIGHT)];
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        level.getBlockEntity(pos, ModBlockEntities.CARVING_TABLE.get()).ifPresent((table) -> {
            Vec3 location = MathUtil.minus(result.getLocation(), pos);
            if (table.isEmpty()) player.setItemInHand(hand, table.addItem(stack));
            else {
                if (result.getDirection() == Direction.UP && item instanceof MaterialToolItem toolItem && toolItem.getToolInfo() == ToolChisel.INSTANCE && MathUtil.between(location.x, 0.125, 0.875) && MathUtil.between(location.z, 0.125, 0.875)) {
                    if (table.carve((((int) Math.floor(location.x * 16)) - 2), ((int) Math.floor(location.z * 16)) - 2))
                        toolItem.damageItem(stack, ToolChisel.INSTANCE.getInteractionDamage());
                } else ItemHandlerHelper.giveItemToPlayer(player, table.takeItem());
            }
        });
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level.isClientSide) return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        boolean harvest = state.canHarvestBlock(level, pos, player);
        if (!player.isCreative() && harvest)
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModBlocks.CARVING_TABLE.get()));
        level.getBlockEntity(pos, ModBlockEntities.CARVING_TABLE.get()).ifPresent((table) -> {
            ItemHandlerHelper.giveItemToPlayer(player, table.getInventory().getStackInSlot(0));
        });
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CarvingTableBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(HEIGHT));
    }
}