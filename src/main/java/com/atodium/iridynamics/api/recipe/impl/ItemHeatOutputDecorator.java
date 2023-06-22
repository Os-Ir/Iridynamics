package com.atodium.iridynamics.api.recipe.impl;

import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.recipe.OutputDecorator;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class ItemHeatOutputDecorator implements OutputDecorator {
    public static final ItemHeatOutputDecorator INSTANCE = new ItemHeatOutputDecorator();
    public static final Serializer SERIALIZER = new Serializer();

    private ItemHeatOutputDecorator() {

    }

    @Override
    public ItemStack apply(ItemStack stack, ItemStack[] input) {
        if (input.length != 1) return stack;
        input[0].getCapability(HeatCapability.HEAT).ifPresent((heatInput) -> stack.getCapability(HeatCapability.HEAT).ifPresent((heatOutput) -> heatOutput.setTemperature(heatInput.getTemperature())));
        return stack;
    }

    @Override
    public Serializer serializer() {
        return SERIALIZER;
    }

    public static class Serializer implements OutputDecorator.Serializer {
        @Override
        public OutputDecorator fromJson(JsonObject json) {
            return INSTANCE;
        }

        @Override
        public OutputDecorator fromNetwork(FriendlyByteBuf buf) {
            return INSTANCE;
        }

        @Override
        public void toNetwork(OutputDecorator decorator, FriendlyByteBuf buf) {

        }
    }
}
