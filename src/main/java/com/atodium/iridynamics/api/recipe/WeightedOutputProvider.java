package com.atodium.iridynamics.api.recipe;

import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.common.item.MaterialItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class WeightedOutputProvider extends OutputProvider {
    public static final WeightedOutputProvider EMPTY = new WeightedOutputProvider(ItemStack.EMPTY, new OutputDecorator[0], 0.0);

    private final double weights;

    public WeightedOutputProvider(ItemStack base, OutputDecorator[] decorators, double weights) {
        super(base, decorators);
        this.weights = weights;
    }

    public static WeightedOutputProvider fromNetwork(FriendlyByteBuf buf) {
        ItemStack base = buf.readItem();
        int length = buf.readInt();
        if (length == 0) return new WeightedOutputProvider(base, new OutputDecorator[0], buf.readDouble());
        OutputDecorator[] decorators = new OutputDecorator[length];
        for (int i = 0; i < length; i++) decorators[i] = ModOutputDecorators.fromNetwork(buf);
        return new WeightedOutputProvider(base, decorators, buf.readDouble());
    }

    public static WeightedOutputProvider fromJson(JsonObject json) {
        ItemStack base = ItemStack.EMPTY;
        if (json.has("base")) base = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("base"));
        else if (json.has("material_base")) {
            JsonObject materialOutput = json.getAsJsonObject("material_base");
            base = MaterialEntry.getMaterialItemStack(SolidShape.getShapeByName(materialOutput.get("shape").getAsString()), MaterialBase.getMaterialByName(materialOutput.get("material").getAsString()), materialOutput.has("count") ? materialOutput.get("count").getAsInt() : 1);
        }
        if (!json.has("decorators"))
            return new WeightedOutputProvider(base, new OutputDecorator[0], json.get("weights").getAsDouble());
        JsonArray decoratorsJson = json.get("decorators").getAsJsonArray();
        OutputDecorator[] decorators = new OutputDecorator[decoratorsJson.size()];
        for (int i = 0; i < decorators.length; i++)
            decorators[i] = ModOutputDecorators.fromJson(decoratorsJson.get(i).getAsJsonObject());
        return new WeightedOutputProvider(base, decorators, json.get("weights").getAsDouble());
    }

    public double weights() {
        return this.weights;
    }

    public void toNetwork(FriendlyByteBuf buf) {
        super.toNetwork(buf);
        buf.writeDouble(this.weights);
    }
}