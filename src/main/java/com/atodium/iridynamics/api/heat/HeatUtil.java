package com.atodium.iridynamics.api.heat;

import com.atodium.iridynamics.api.capability.ForgingCapability;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.InventoryCapability;
import com.atodium.iridynamics.api.heat.impl.MaterialPhasePortrait;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class HeatUtil {
    public static void addMaterialItemCapability(AttachCapabilitiesEvent<ItemStack> event, MaterialEntry entry) {
        MaterialBase material = entry.material();
        SolidShape shape = entry.shape();
        if (material.hasHeatInfo()) {
            event.addCapability(HeatCapability.KEY, new HeatCapability(new MaterialPhasePortrait(material.getHeatInfo(), shape.getUnit() / 144.0)));
            if (shape.hasForgeShape()) event.addCapability(ForgingCapability.KEY, new ForgingCapability(shape));
        }
    }

    public static void addItemInventory(AttachCapabilitiesEvent<ItemStack> event, int slots) {
        event.addCapability(InventoryCapability.KEY, new InventoryCapability(slots));
    }
}