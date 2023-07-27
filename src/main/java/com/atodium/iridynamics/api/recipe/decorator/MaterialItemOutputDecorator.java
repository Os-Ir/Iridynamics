package com.atodium.iridynamics.api.recipe.decorator;

import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.common.item.MaterialItem;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class MaterialItemOutputDecorator implements OutputDecorator {
    public static final Serializer SERIALIZER = new Serializer();

    private final int source;

    public MaterialItemOutputDecorator(int source) {
        this.source = source;
    }

    @Override
    public ItemStack apply(ItemStack stack, ItemStack[] input) {
        if (MaterialItem.isMaterialItem(stack) && MaterialEntry.containsMaterialEntry(input[this.source]))
            MaterialItem.setItemMaterial(stack, MaterialEntry.getItemMaterialEntry(input[this.source]).material());
        return stack;
    }

    @Override
    public Serializer serializer() {
        return SERIALIZER;
    }

    public static class Serializer implements OutputDecorator.Serializer {
        @Override
        public OutputDecorator fromJson(JsonObject json) {
            return new MaterialItemOutputDecorator(json.has("source") ? json.get("source").getAsInt() : 0);
        }

        @Override
        public OutputDecorator fromNetwork(FriendlyByteBuf buf) {
            return new MaterialItemOutputDecorator(buf.readInt());
        }

        @Override
        public void toNetwork(OutputDecorator decorator, FriendlyByteBuf buf) {
            if (decorator instanceof MaterialItemOutputDecorator material) buf.writeInt(material.source);
        }
    }
}