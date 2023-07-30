package com.atodium.iridynamics.common.block;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.heat.FuelInfo;
import com.atodium.iridynamics.api.heat.HeatModule;
import com.atodium.iridynamics.common.blockEntity.FuelBlockEntity;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Random;

public class FuelBlock extends Block implements EntityBlock {
    public static final BooleanProperty IGNITE = BooleanProperty.create("ignite");

    public FuelBlock(Block.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(IGNITE, false));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.FUEL.get() ? ITickable.ticker() : null;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(IGNITE) ? 15 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) level.scheduleTick(currentPos, this, 1);
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        FuelBlockEntity fuel = (FuelBlockEntity) level.getBlockEntity(pos);
        fuel.markUpdate();
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        level.getBlockEntity(pos, ModBlockEntities.FUEL.get()).ifPresent((fuel) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (FuelInfo.hasFuelInfo(stack)) fuel.addFuel(stack);
            else if (!state.getValue(IGNITE)) fuel.addIgniteStarter(stack);
            System.out.println("Fuel: " + (fuel.getTemperature() - HeatModule.AMBIENT_TEMPERATURE) + "K           remain: " + fuel.getRemainItems());
        });
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level.isClientSide) return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (!player.isCreative() && state.canHarvestBlock(level, pos, player))
            level.getBlockEntity(pos, ModBlockEntities.FUEL.get()).ifPresent((fuel) -> ItemHandlerHelper.giveItemToPlayer(player, fuel.getFuelItem().createStack((int) fuel.getRemainItems())));
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FuelBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(IGNITE));
    }
}