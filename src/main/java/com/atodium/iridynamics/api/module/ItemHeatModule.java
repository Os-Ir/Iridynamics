package com.atodium.iridynamics.api.module;

import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.IHeat;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class ItemHeatModule {
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
}