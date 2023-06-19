package com.atodium.iridynamics.api.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public interface OutputDecorator {
    ItemStack apply(ItemStack stack, ItemStack[] input);

    Serializer serializer();

    default void toNetwork(FriendlyByteBuf buf) {
        buf.writeResourceLocation(ModOutputDecorators.getId(this.serializer()));
        this.serializer().toNetwork(this, buf);
    }

    interface Serializer {
        OutputDecorator fromJson(JsonObject json);

        OutputDecorator fromNetwork(FriendlyByteBuf buf);

        void toNetwork(OutputDecorator decorator, FriendlyByteBuf buf);
    }
}