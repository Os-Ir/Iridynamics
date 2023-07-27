package com.atodium.iridynamics.common.blockEntity.rotate;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.RotateMachineBlockEntity;
import com.atodium.iridynamics.api.item.InventoryUtil;
import com.atodium.iridynamics.api.recipe.impl.CentrifugeRecipe;
import com.atodium.iridynamics.common.block.rotate.CentrifugeBlock;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CentrifugeBlockEntity extends RotateMachineBlockEntity implements ITickable {
    private boolean recipeUpdateFlag;
    private final InventoryUtil.Inventory inventory;
    private CentrifugeRecipe recipe;
    private int progress;

    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CENTRIFUGE.get(), pos, state);
        this.inventory = InventoryUtil.inventory(1);
    }

    @Override
    public Direction direction() {
        return this.getBlockState().getValue(CentrifugeBlock.DIRECTION);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {

    }
}