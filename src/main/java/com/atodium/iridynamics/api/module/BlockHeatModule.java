package com.atodium.iridynamics.api.module;

import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.IHeat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

public class BlockHeatModule {
    public static void blockHeatExchange(Level level, BlockPos pos, BlockState state, BlockEntity entity, boolean withAllCapabilities) {
        for (Direction direction : Direction.values()) {
            LazyOptional<IHeat> optional = entity.getCapability(HeatCapability.HEAT, direction);
            if (!optional.isPresent()) continue;
            IHeat heat = optional.orElseThrow(NullPointerException::new);
            BlockPos besidePos = pos.relative(direction);
            BlockState besideState = level.getBlockState(besidePos);
            BlockEntity besideEntity = level.getBlockEntity(besidePos);
            if (besideEntity != null) {
                LazyOptional<IHeat> besideOptional = besideEntity.getCapability(HeatCapability.HEAT, direction.getOpposite());
                if (besideOptional.isPresent()) {
                    IHeat besideHeat = besideOptional.orElseThrow(NullPointerException::new);
                    if (withAllCapabilities || direction == Direction.UP || direction == Direction.NORTH || direction == Direction.EAST)
                        ItemHeatModule.heatExchange(heat, besideHeat, heat.getResistance(direction) + besideHeat.getResistance(direction.getOpposite()));
                } else
                    ItemHeatModule.heatExchange(heat, ItemHeatModule.AMBIENT_TEMPERATURE, heat.getResistance(direction) + ItemHeatModule.RESISTANCE_BLOCK_DEFAULT);
            } else if (besideState.isAir())
                ItemHeatModule.heatExchange(heat, ItemHeatModule.AMBIENT_TEMPERATURE, heat.getResistance(direction) + ItemHeatModule.RESISTANCE_AIR_FLOW);
            else
                ItemHeatModule.heatExchange(heat, ItemHeatModule.AMBIENT_TEMPERATURE, heat.getResistance(direction) + ItemHeatModule.RESISTANCE_BLOCK_DEFAULT);
        }
    }
}