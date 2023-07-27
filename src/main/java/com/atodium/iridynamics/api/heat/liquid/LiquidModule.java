package com.atodium.iridynamics.api.heat.liquid;

import com.atodium.iridynamics.api.capability.LiquidContainerCapability;
import com.atodium.iridynamics.api.heat.HeatModule;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import java.util.Map;

public class LiquidModule {
    public static void addLiquidContainer(ServerLevel level, BlockPos pos) {
        LiquidContainerSavedData.get(level).addLiquidContainer(pos);
    }

    public static void addLiquidContainer(ServerLevel level, BlockPos pos, int count) {
        LiquidContainerSavedData.get(level).addLiquidContainer(pos, count);
    }

    public static void removeLiquidContainer(ServerLevel level, BlockPos pos) {
        LiquidContainerSavedData.get(level).removeLiquidContainer(pos);
    }

    public static void removeLiquidContainer(ServerLevel level, BlockPos pos, int count) {
        LiquidContainerSavedData.get(level).removeLiquidContainer(pos, count);
    }

    public static void removeAllLiquidContainer(ServerLevel level, BlockPos pos) {
        LiquidContainerSavedData.get(level).removeAllLiquidContainer(pos);
    }

    public static boolean hasLiquidContainer(ServerLevel level, BlockPos pos) {
        return LiquidContainerSavedData.get(level).hasLiquidContainer(pos);
    }

    public static void addItemLiquidContainer(AttachCapabilitiesEvent<ItemStack> event, int capacity) {
        event.addCapability(LiquidContainerCapability.KEY, new LiquidContainerCapability(capacity));
    }

    public static int pourSameMaterial(LiquidContainerCapability source, LiquidContainerCapability target) {
        double temperature = source.getTemperature();
        int targetCapacity = target.liquidCapacity();
        if (target.isEmpty()) {
            ImmutableSet<Map.Entry<MaterialBase, Integer>> materials = source.getAllMaterials().entrySet();
            for (Map.Entry<MaterialBase, Integer> entry : materials) {
                MaterialBase material = entry.getKey();
                if (material.getHeatInfo().getMeltingPoint() > temperature) continue;
                int add = Math.min(entry.getValue(), targetCapacity);
                target.addMaterial(material, add);
                target.setTemperature(temperature);
                source.addMaterial(material, -add);
                if (source.isEmpty()) source.setEnergy(0);
                else source.setTemperature(temperature);
                return add;
            }
            return 0;
        }
        if (target.getMaterialTypes() != 1) return 0;
        MaterialBase material = target.getAllMaterials().keySet().stream().toList().get(0);
        int exist = target.getMaterialUnit(material);
        if (exist == targetCapacity || !source.hasMaterial(material)) return 0;
        int add = Math.min(source.getMaterialUnit(material), targetCapacity - exist);
        target.addMaterial(material, add);
        target.increaseEnergy(material.getHeatInfo().getMoleEnergy(HeatModule.ATMOSPHERIC_PRESSURE, temperature) * add / 144.0);
        source.addMaterial(material, -add);
        if (!source.isEmpty()) source.setTemperature(temperature);
        return add;
    }
}