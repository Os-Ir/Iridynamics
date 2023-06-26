package com.atodium.iridynamics.common;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.capability.*;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Iridynamics.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandler {
    @SubscribeEvent
    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(HeatCapability.class);
        event.register(ForgingCapability.class);
        event.register(InventoryCapability.class);
        event.register(LiquidContainerCapability.class);
        event.register(HeatProcessCapability.class);
        event.register(CarvingCapability.class);
        event.register(PotteryCapability.class);
    }
}