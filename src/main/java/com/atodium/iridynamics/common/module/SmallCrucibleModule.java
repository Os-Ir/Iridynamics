package com.atodium.iridynamics.common.module;

import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.InventoryCapability;
import com.atodium.iridynamics.api.capability.LiquidContainerCapability;
import com.atodium.iridynamics.api.heat.MaterialHeatInfo;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.heat.HeatModule;
import com.atodium.iridynamics.api.module.LiquidContainerModule;
import com.atodium.iridynamics.common.blockEntity.equipment.SmallCrucibleBlockEntity;
import com.atodium.iridynamics.common.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.items.IItemHandlerModifiable;

public class SmallCrucibleModule {
    public static final double INVENTORY_RESISTANCE = 0.02, HEAT_CAPACITY = 16000.0, ITEM_RESISTANCE = 0.2;
    public static final double[] RESISTANCE = new double[]{0.03, 0.3, 0.2, 0.2, 0.2, 0.2};
    public static final int CAPACITY = 576;

    public static boolean validateItem(ItemStack stack) {
        if (!stack.getCapability(HeatCapability.HEAT).isPresent() || !MaterialEntry.containsMaterialEntry(stack))
            return false;
        return MaterialEntry.getItemMaterialEntry(stack).material().hasHeatInfo();
    }

    public static void updateData(ItemStack stack) {
        if (stack.getItem() != ModItems.SMALL_CRUCIBLE.get())
            throw new IllegalArgumentException("ItemStack [ " + stack + " ] is not small crucible");
        updateData(stack.getCapability(InventoryCapability.INVENTORY).orElseThrow(NullPointerException::new), (LiquidContainerCapability) stack.getCapability(LiquidContainerCapability.LIQUID_CONTAINER).orElseThrow(NullPointerException::new), (HeatCapability) stack.getCapability(HeatCapability.HEAT).orElseThrow(NullPointerException::new));
    }

    public static void updateData(SmallCrucibleBlockEntity crucible) {
        updateData(crucible.getInventory(), crucible.getLiquidContainer(), crucible.getHeat());
    }

    public static void updateData(IItemHandlerModifiable inventory, LiquidContainerCapability container, HeatCapability heat) {
        if (!container.isEmpty()) HeatModule.heatExchange(heat, container, INVENTORY_RESISTANCE);
        for (int i = 0; i < 4; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!validateItem(stack)) continue;
            MaterialEntry entry = MaterialEntry.getItemMaterialEntry(stack);
            MaterialHeatInfo info = MaterialEntry.getItemMaterialEntry(stack).material().getHeatInfo();
            HeatCapability itemHeat = (HeatCapability) stack.getCapability(HeatCapability.HEAT).orElseThrow(NullPointerException::new);
            HeatModule.heatExchange(heat, itemHeat, INVENTORY_RESISTANCE + itemHeat.getResistance());
            if (itemHeat.getTemperature() > info.getMeltingPoint()) {
                container.addMaterial(entry.material(), entry.shape().getUnit());
                container.increaseEnergy(itemHeat.getEnergy());
                inventory.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    public static void setupBlock(SmallCrucibleBlockEntity block, ItemStack item) {
        if (item.getItem() == ModItems.SMALL_CRUCIBLE.get()) {
            block.getInventory().deserializeNBT(((InventoryCapability) item.getCapability(InventoryCapability.INVENTORY).orElseThrow(NullPointerException::new)).serializeNBT());
            block.getLiquidContainer().deserializeNBT(((LiquidContainerCapability) item.getCapability(LiquidContainerCapability.LIQUID_CONTAINER).orElseThrow(NullPointerException::new)).serializeNBT());
            block.getHeat().setEnergy(item.getCapability(HeatCapability.HEAT).orElseThrow(NullPointerException::new).getEnergy());
        }
    }

    public static void initItem(AttachCapabilitiesEvent<ItemStack> event, ItemStack stack) {
        if (stack.getItem() != ModItems.SMALL_CRUCIBLE.get())
            throw new IllegalArgumentException("ItemStack [ " + stack + " ] is not small crucible");
        LiquidContainerModule.addItemLiquidContainer(event, CAPACITY);
        HeatModule.addItemHeat(event, stack, HEAT_CAPACITY, ITEM_RESISTANCE);
        HeatModule.addItemInventory(event, 4);
    }

    public static ItemStack createItem(SmallCrucibleBlockEntity block) {
        ItemStack stack = new ItemStack(ModItems.SMALL_CRUCIBLE.get());
        ((InventoryCapability) stack.getCapability(InventoryCapability.INVENTORY).orElseThrow(NullPointerException::new)).deserializeNBT(block.getInventory().serializeNBT());
        ((LiquidContainerCapability) stack.getCapability(LiquidContainerCapability.LIQUID_CONTAINER).orElseThrow(NullPointerException::new)).deserializeNBT(block.getLiquidContainer().serializeNBT());
        ((HeatCapability) stack.getCapability(HeatCapability.HEAT).orElseThrow(NullPointerException::new)).deserializeNBT(block.getHeat().serializeNBT());
        return stack;
    }
}