package com.atodium.iridynamics.api.recipe.impl;

import com.atodium.iridynamics.api.recipe.IngredientIndex;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.WeightedOutputProvider;
import com.atodium.iridynamics.api.recipe.container.ItemStackContainer;
import com.atodium.iridynamics.api.recipe.RecipeSerializerImpl;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record WashingRecipe(ResourceLocation id, IngredientIndex input, WeightedOutputProvider[] output,
                            int outputCount) implements ISpecialRecipe<ItemStackContainer> {
    public void consume(ItemStack stack) {
        stack.shrink(this.input.getCount());
    }

    @Override
    public boolean matches(ItemStackContainer container, Level level) {
        ItemStack stack = container.getItem();
        return this.input.test(stack);
    }

    @Override
    public ItemStack getResultItem() {
        double[] weightsArray = new double[this.output.length];
        for (int i = 0; i < this.output.length; i++) weightsArray[i] = this.output[i].weights();
        return this.output[MathUtil.getWeightedRandom(weightsArray)].withoutDecorate();
    }


    @Override
    public ItemStack assemble(ItemStackContainer inventory) {
        double[] weightsArray = new double[this.output.length];
        for (int i = 0; i < weightsArray.length; i++) weightsArray[i] = this.output[i].weights();
        return this.output[MathUtil.getWeightedRandom(weightsArray)].apply(inventory.getItem());
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<WashingRecipe> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<WashingRecipe> getType() {
        return ModRecipeTypes.WASHING.get();
    }

    public static class Serializer extends RecipeSerializerImpl<ItemStackContainer, WashingRecipe> {
        @Override
        public WashingRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray outputJson = json.getAsJsonArray("output");
            WeightedOutputProvider[] output = new WeightedOutputProvider[outputJson.size()];
            for (int i = 0; i < output.length; i++)
                output[i] = WeightedOutputProvider.fromJson(outputJson.get(i).getAsJsonObject());
            return new WashingRecipe(id, IngredientIndex.fromJson(json.getAsJsonObject("input")), output, json.get("output_count").getAsInt());
        }

        @Override
        public WashingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            IngredientIndex input = IngredientIndex.fromNetwork(buf);
            WeightedOutputProvider[] output = new WeightedOutputProvider[buf.readInt()];
            for (int i = 0; i < output.length; i++) output[i] = WeightedOutputProvider.fromNetwork(buf);
            return new WashingRecipe(id, input, output, buf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, WashingRecipe recipe) {
            recipe.input.toNetwork(buf);
            buf.writeInt(recipe.output.length);
            for (WeightedOutputProvider provider : recipe.output) provider.toNetwork(buf);
            buf.writeInt(recipe.outputCount);
        }
    }
}