package com.atodium.iridynamics.api.heat;

import com.atodium.iridynamics.api.capability.ForgingCapability;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.InventoryCapability;
import com.atodium.iridynamics.api.heat.impl.MaterialPhasePortrait;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class HeatModule {
    public static final double ATMOSPHERIC_PRESSURE = 101300.0;
    public static final double CELSIUS_ZERO = 273.15;
    public static final double AMBIENT_TEMPERATURE = CELSIUS_ZERO + 25.0;

    public static final double RESISTANCE_BLOCK_DEFAULT = 3.0;
    public static final double RESISTANCE_AIR_FLOW = 0.1;
    public static final double RESISTANCE_AIR_STATIC = 10.0;

    public static void addItemHeat(AttachCapabilitiesEvent<ItemStack> event, ItemStack stack, double capacity) {
        addItemHeat(event, stack, capacity, 0.0);
    }

    public static void addItemHeat(AttachCapabilitiesEvent<ItemStack> event, ItemStack stack, double capacity, double resistance) {
        event.addCapability(HeatCapability.KEY, new HeatCapability(HeatProcessModule.checkItemHeatProcess(stack, capacity), resistance));
    }

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
            else
                heatExchange(heat, AMBIENT_TEMPERATURE, heat.getResistance(direction) + RESISTANCE_BLOCK_DEFAULT);
        }
    }

    public static void addItemInventory(AttachCapabilitiesEvent<ItemStack> event, int slots) {
        event.addCapability(InventoryCapability.KEY, new InventoryCapability(slots));
    }

    public static void addMaterialItemCapability(AttachCapabilitiesEvent<ItemStack> event, MaterialEntry entry) {
        MaterialBase material = entry.material();
        SolidShape shape = entry.shape();
        if (material.hasHeatInfo()) {
            event.addCapability(HeatCapability.KEY, new HeatCapability(new MaterialPhasePortrait(material.getHeatInfo(), shape.getUnit() / 144.0)));
            if (shape.hasForgeShape()) event.addCapability(ForgingCapability.KEY, new ForgingCapability(shape));
        }
    }
}