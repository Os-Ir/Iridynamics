package com.atodium.iridynamics.api.heat;

import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FuelInfo {
    public static final UnorderedRegistry<Item, FuelInfo> ITEM_FUEL = new UnorderedRegistry<>();

    private final String name;
    private final double calorificValue, flashPoint, maxTemperature;

    private FuelInfo(String name, double calorificValue, double flashPoint, double maxTemperature) {
        this.name = name;
        this.calorificValue = calorificValue;
        this.flashPoint = flashPoint;
        this.maxTemperature = maxTemperature;
    }

    public static FuelInfo of(String name, double calorificValue, double flashPoint, double maxTemperature) {
        return new FuelInfo(name, calorificValue, flashPoint, maxTemperature);
    }

    public static boolean hasFuelInfo(ItemStack stack) {
        return ITEM_FUEL.containsKey(stack.getItem());
    }

    public static FuelInfo getFuelInfoForItem(ItemStack stack) {
        return getFuelInfoForItem(stack.getItem());
    }

    public static FuelInfo getFuelInfoForItem(Item item) {
        return ITEM_FUEL.get(item);
    }

    public void registerForItem(Item item) {
        ITEM_FUEL.register(item, this);
    }

    public String name() {
        return name;
    }

    public double calorificValue() {
        return this.calorificValue;
    }

    public double flashPoint() {
        return this.flashPoint;
    }

    public double maxTemperature() {
        return this.maxTemperature;
    }
}