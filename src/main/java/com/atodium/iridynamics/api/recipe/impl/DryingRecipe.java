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

public record DryingRecipe(ResourceLocation id, IngredientIndex input, OutputProvider output,
                           int tick) implements ISpecialRecipe<ItemStackContainer> {
    private static List<DryingRecipe> cache = null;

    public static void resetCache() {
        cache = null;
    }

    public static List<DryingRecipe> getAllRecipes(Level level) {
        if (cache == null) cache = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.DRYING.get());
        return cache;
    }

    public static DryingRecipe getRecipe(ItemStack stack, Level level) {
        ItemStackContainer container = new ItemStackContainer(stack);
        for (DryingRecipe recipe : getAllRecipes(level)) if (recipe.matches(container, level)) return recipe;
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
        ItemStack stack = container.getItemStack();
        return this.input.testItem(stack) && this.input.getCount() == stack.getCount();
    }

    @Override
    public ItemStack getResultItem() {
        return this.output.withoutDecorate();
    }

    @Override
    public ItemStack assemble(ItemStackContainer inventory) {
        return this.output.apply(inventory.getItemStack());
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<DryingRecipe> getSerializer() {
        return ModRecipeSerializers.DRYING.get();
    }

    @Override
    public RecipeType<DryingRecipe> getType() {
        return ModRecipeTypes.DRYING.get();
    }

    public static class Serializer extends RecipeSerializerImpl<ItemStackContainer, DryingRecipe> {
        @Override
        public DryingRecipe fromJson(ResourceLocation id, JsonObject json) {
            IngredientIndex input = IngredientIndex.fromJson(json.getAsJsonObject("input"));
            OutputProvider output = OutputProvider.fromJson(json.getAsJsonObject("output"));
            return new DryingRecipe(id, input, output, json.get("tick").getAsInt());
        }

        @Override
        public DryingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            IngredientIndex input = IngredientIndex.fromNetwork(buf);
            OutputProvider output = OutputProvider.fromNetwork(buf);
            return new DryingRecipe(id, input, output, buf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DryingRecipe recipe) {
            recipe.input.toNetwork(buf);
            recipe.output.toNetwork(buf);
            buf.writeInt(recipe.tick);
        }
    }
}