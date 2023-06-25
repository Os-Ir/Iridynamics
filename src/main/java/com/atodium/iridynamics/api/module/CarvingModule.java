package com.atodium.iridynamics.api.module;

import com.atodium.iridynamics.api.capability.CarvingCapability;
import com.atodium.iridynamics.api.util.data.ItemDelegate;
import com.google.common.collect.Maps;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import java.util.Map;

public class CarvingModule {
    public static final Map<ItemDelegate, Integer> COLOR = Maps.newHashMap();

    public static void addItemCarving(AttachCapabilitiesEvent<ItemStack> event, ItemStack stack, int thickness, int color) {
        event.addCapability(CarvingCapability.KEY, new CarvingCapability(thickness));
        COLOR.put(ItemDelegate.of(stack), color);
    }

    public static int getItemColor(ItemStack stack) {
        return getItemColor(ItemDelegate.of(stack));
    }

    public static int getItemColor(ItemDelegate item) {
        if (COLOR.containsKey(item)) return COLOR.get(item);
        return 0xffffff;
    }
}