package com.atodium.iridynamics.api.recipe.impl;

import com.atodium.iridynamics.api.recipe.*;
import com.atodium.iridynamics.api.recipe.container.ItemStackContainer;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record CrushingRecipe(ResourceLocation id, IngredientIndex input, OutputProvider output,
                             int count, double temperature) implements ISpecialRecipe<ItemStackContainer> {
    public void consume(ItemStackContainer container) {
        this.input.consume(container.getItem());
    }

    @Override
    public boolean matches(ItemStackContainer inventory, Level level) {
        return this.input.test(inventory.getItem());
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
    public RecipeSerializer<CrushingRecipe> getSerializer() {
        return ModRecipeSerializers.CRUSHING.get();
    }

    @Override
    public RecipeType<CrushingRecipe> getType() {
        return ModRecipeTypes.CRUSHING.get();
    }

    public static class Serializer extends RecipeSerializerImpl<ItemStackContainer, CrushingRecipe> {
        @Override
        public CrushingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new CrushingRecipe(id, IngredientIndex.fromJson(json.getAsJsonObject("input")), OutputProvider.fromJson(json.getAsJsonObject("output")), json.get("count").getAsInt(), json.has("temperature") ? json.get("temperature").getAsDouble() : -1.0);
        }

        @Override
        public CrushingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new CrushingRecipe(id, IngredientIndex.fromNetwork(buf), OutputProvider.fromNetwork(buf), buf.readInt(), buf.readDouble());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CrushingRecipe recipe) {
            recipe.input.toNetwork(buf);
            recipe.output.toNetwork(buf);
            buf.writeInt(recipe.count);
            buf.writeDouble(recipe.temperature);
        }
    }
}