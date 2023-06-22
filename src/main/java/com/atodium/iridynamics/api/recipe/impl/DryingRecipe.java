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

public record DryingRecipe(ResourceLocation id, IngredientIndex input, OutputProvider output,
                           int tick) implements ISpecialRecipe<ItemStackContainer> {
    @Override
    public boolean matches(ItemStackContainer container, Level level) {
        ItemStack stack = container.getItem();
        return this.input.testItem(stack) && this.input.getCount() == stack.getCount();
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
            return new DryingRecipe(id, IngredientIndex.fromJson(json.getAsJsonObject("input")), OutputProvider.fromJson(json.getAsJsonObject("output")), json.get("tick").getAsInt());
        }

        @Override
        public DryingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new DryingRecipe(id, IngredientIndex.fromNetwork(buf), OutputProvider.fromNetwork(buf), buf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DryingRecipe recipe) {
            recipe.input.toNetwork(buf);
            recipe.output.toNetwork(buf);
            buf.writeInt(recipe.tick);
        }
    }
}