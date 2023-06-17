package com.atodium.iridynamics.common;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.capability.ForgingCapability;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.InventoryCapability;
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
    }
}