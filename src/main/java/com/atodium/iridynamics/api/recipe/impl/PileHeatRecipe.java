package com.atodium.iridynamics.api.recipe.impl;

import com.atodium.iridynamics.api.recipe.IngredientIndex;
import com.atodium.iridynamics.api.recipe.ModRecipeSerializers;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.OutputProvider;
import com.atodium.iridynamics.api.recipe.container.ItemStackContainer;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public record PileHeatRecipe(ResourceLocation id, IngredientIndex input,
                             OutputProvider output, double temperature,
                             double energy) implements ISpecialRecipe<ItemStackContainer> {
    private static List<PileHeatRecipe> cache = null;

    public static void resetCache() {
        cache = null;
    }

    public static List<PileHeatRecipe> getAllRecipes(Level level) {
        if (cache == null) cache = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.PILE_HEAT.get());
        return cache;
    }

    public static PileHeatRecipe getRecipe(ItemStack stack, Level level) {
        ItemStackContainer container = new ItemStackContainer(stack);
        for (PileHeatRecipe recipe : getAllRecipes(level)) if (recipe.matches(container, level)) return recipe;
        return null;
    }

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
    public RecipeSerializer<PileHeatRecipe> getSerializer() {
        return ModRecipeSerializers.PILE_HEAT.get();
    }

    @Override
    public RecipeType<PileHeatRecipe> getType() {
        return ModRecipeTypes.PILE_HEAT.get();
    }

    public static class Serializer extends RecipeSerializerImpl<ItemStackContainer, PileHeatRecipe> {
        @Override
        public PileHeatRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new PileHeatRecipe(id, IngredientIndex.fromJson(json.getAsJsonObject("input")), OutputProvider.fromJson(json.getAsJsonObject("output")), json.get("temperature").getAsDouble(), json.get("energy").getAsDouble());
        }

        @Override
        public PileHeatRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new PileHeatRecipe(id, IngredientIndex.fromNetwork(buf), OutputProvider.fromNetwork(buf), buf.readDouble(), buf.readDouble());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, PileHeatRecipe recipe) {
            recipe.input.toNetwork(buf);
            recipe.output.toNetwork(buf);
            buf.writeDouble(recipe.temperature);
            buf.writeDouble(recipe.energy);
        }
    }
}