package com.atodium.iridynamics.api.heat;

import com.atodium.iridynamics.api.util.data.ItemDelegate;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FuelInfo {
    public static final UnorderedRegistry<ItemDelegate, FuelInfo> ITEM_FUEL = new UnorderedRegistry<>();

    private final String name;
    private final double calorificValue, flashPoint, maxTemperature;

    private FuelInfo(String name, double calorificValue, double flashPoint, double maxTemperature) {
        this.name = name;
        this.calorificValue = calorificValue;
        this.flashPoint = flashPoint;
        this.maxTemperature = maxTemperature;
    }

    public static boolean containsItemInfo(ItemStack stack) {
        return containsItemInfo(ItemDelegate.of(stack));
    }

    public static boolean containsItemInfo(Item item) {
        return containsItemInfo(ItemDelegate.of(item));
    }

    public static boolean containsItemInfo(ItemDelegate item) {
        return ITEM_FUEL.containsKey(item);
    }

    public static FuelInfo of(String name, double calorificValue, double flashPoint, double maxTemperature) {
        return new FuelInfo(name, calorificValue, flashPoint, maxTemperature);
    }

    public static boolean hasFuelInfo(ItemStack stack) {
        return ITEM_FUEL.containsKey(ItemDelegate.of(stack));
    }

    public static FuelInfo getFuelInfoForItem(ItemStack stack) {
        return getFuelInfoForItem(ItemDelegate.of(stack.getItem()));
    }

    public static FuelInfo getFuelInfoForItem(Item item) {
        return getFuelInfoForItem(ItemDelegate.of(item));
    }

    public static FuelInfo getFuelInfoForItem(ItemDelegate item) {
        return ITEM_FUEL.get(item);
    }

    public void registerForItem(Item item) {
        this.registerForItem(ItemDelegate.of(item));
    }

    public void registerForItem(ItemDelegate item) {
        ITEM_FUEL.register(item, this);
    }

    public String name() {
        return this.name;
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