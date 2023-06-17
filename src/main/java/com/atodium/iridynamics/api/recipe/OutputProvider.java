package com.atodium.iridynamics.api.recipe;

import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

public record OutputProvider(ItemStack base, OutputDecorator[] decorators) {
    public static final OutputProvider EMPTY = new OutputProvider(ItemStack.EMPTY, new OutputDecorator[0]);

    public static OutputProvider fromNetwork(FriendlyByteBuf buf) {
        ItemStack base = buf.readItem();
        int length = buf.readInt();
        if (length == 0) return new OutputProvider(base, new OutputDecorator[0]);
        OutputDecorator[] decorators = new OutputDecorator[length];
        for (int i = 0; i < length; i++) decorators[i] = ModOutputDecorators.fromNetwork(buf);
        return new OutputProvider(base, decorators);
    }

    public static OutputProvider fromJson(JsonObject json) {
        ItemStack base = ItemStack.EMPTY;
        if (json.has("base")) base = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("base"));
        else if (json.has("material_base")) {
            JsonObject materialOutput = json.getAsJsonObject("material_output");
            base = MaterialEntry.getMaterialItemStack(SolidShape.getShapeByName(materialOutput.get("shape").getAsString()), MaterialBase.getMaterialByName(materialOutput.get("material").getAsString()), materialOutput.get("count").getAsInt());
        }
        if (!json.has("decorators")) return new OutputProvider(base, new OutputDecorator[0]);
        JsonArray decoratorsJson = json.get("decorators").getAsJsonArray();
        OutputDecorator[] decorators = new OutputDecorator[decoratorsJson.size()];
        for (int i = 0; i < decorators.length; i++)
            decorators[i] = ModOutputDecorators.fromJson(decoratorsJson.get(i).getAsJsonObject());
        return new OutputProvider(base, decorators);
    }

    public ItemStack apply(ItemStack... input) {
        ItemStack output = this.base.copy();
        for (OutputDecorator decorator : this.decorators) decorator.apply(output, input);
        return output;
    }

    public ItemStack withoutDecorate() {
        return this.base.copy();
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeItem(this.base.copy());
        buf.writeInt(this.decorators.length);
        for (OutputDecorator decorator : this.decorators) decorator.toNetwork(buf);
    }
}