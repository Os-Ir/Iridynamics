package com.atodium.iridynamics.api.heat;

import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.IHeat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

public class HeatUtil {
    public static final double ATMOSPHERIC_PRESSURE = 101300.0;
    public static final double CELSIUS_ZERO = 273.15;
    public static final double AMBIENT_TEMPERATURE = CELSIUS_ZERO + 25.0;

    public static final double RESISTANCE_BLOCK_DEFAULT = 10.0;
    public static final double RESISTANCE_AIR_FLOW = 0.3;
    public static final double RESISTANCE_AIR_STATIC = 30.0;

    public static void heatExchange(IHeat cap, double temperature, double resistance) {
        if (cap == null) return;
        double exchange = (cap.getTemperature() - temperature) / resistance;
        cap.increaseEnergy(-exchange);
    }

    public static void heatExchange(IHeat capA, IHeat capB, double resistance) {
        if (capA == null || capB == null) return;
        double exchange = (capA.getTemperature() - capB.getTemperature()) / resistance;
        capA.increaseEnergy(-exchange);
        capB.increaseEnergy(exchange);
    }

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
                        heatExchange(heat, besideHeat, heat.getResistance(direction) + besideHeat.getResistance(direction.getOpposite()));
                } else
                    heatExchange(heat, AMBIENT_TEMPERATURE, heat.getResistance(direction) + RESISTANCE_BLOCK_DEFAULT);
            } else if (besideState.isAir())
                heatExchange(heat, AMBIENT_TEMPERATURE, heat.getResistance(direction) + RESISTANCE_AIR_FLOW);
            else heatExchange(heat, AMBIENT_TEMPERATURE, heat.getResistance(direction) + RESISTANCE_BLOCK_DEFAULT);
        }
    }
}