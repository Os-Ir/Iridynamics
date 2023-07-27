package com.atodium.iridynamics.api.recipe.impl;

import com.atodium.iridynamics.api.recipe.IngredientIndex;
import com.atodium.iridynamics.api.recipe.ModRecipeSerializers;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.OutputProvider;
import com.atodium.iridynamics.api.recipe.container.ItemStackContainer;
import com.atodium.iridynamics.api.recipe.RecipeSerializerImpl;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record HeatRecipe(ResourceLocation id, IngredientIndex input, OutputProvider output, double temperature,
                         double energy) implements ISpecialRecipe<ItemStackContainer> {
    public IngredientIndex getInput() {
        return this.input;
    }

    public OutputProvider getOutput() {
        return this.output;
    }

    @Override
    public boolean matches(ItemStackContainer container, Level level) {
        return this.input.test(container.getItem());
    }

    @Override
    public ItemStack getResultItem() {
        return this.output.withoutDecorate();
    }

    @Override
    public ItemStack assemble(ItemStackContainer inventory) {
        return this.output.apply(inventory.getItem());
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<HeatRecipe> getSerializer() {
        return ModRecipeSerializers.HEAT.get();
    }

    @Override
    public RecipeType<HeatRecipe> getType() {
        return ModRecipeTypes.HEAT.get();
    }

    public static class Serializer extends RecipeSerializerImpl<ItemStackContainer, HeatRecipe> {
        @Override
        public HeatRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new HeatRecipe(id, IngredientIndex.fromJson(json.getAsJsonObject("input")), OutputProvider.fromJson(json.getAsJsonObject("output")), json.get("temperature").getAsDouble(), json.get("energy").getAsDouble());
        }

        @Override
        public HeatRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new HeatRecipe(id, IngredientIndex.fromNetwork(buf), OutputProvider.fromNetwork(buf), buf.readDouble(), buf.readDouble());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, HeatRecipe recipe) {
            recipe.input.toNetwork(buf);
            recipe.output.toNetwork(buf);
            buf.writeDouble(recipe.temperature);
            buf.writeDouble(recipe.energy);
        }
    }
}