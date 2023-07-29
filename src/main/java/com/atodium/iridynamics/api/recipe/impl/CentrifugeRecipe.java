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

public record CentrifugeRecipe(ResourceLocation id, IngredientIndex input, OutputProvider output, double torque,
                               double angle) implements ISpecialRecipe<ItemStackContainer> {
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
    public RecipeSerializer<CentrifugeRecipe> getSerializer() {
        return ModRecipeSerializers.CENTRIFUGE.get();
    }

    @Override
    public RecipeType<CentrifugeRecipe> getType() {
        return ModRecipeTypes.CENTRIFUGE.get();
    }

    public static class Serializer extends RecipeSerializerImpl<ItemStackContainer, CentrifugeRecipe> {
        @Override
        public CentrifugeRecipe fromJson(ResourceLocation id, JsonObject json) {
            System.out.println(new CentrifugeRecipe(id, IngredientIndex.fromJson(json.getAsJsonObject("input")), OutputProvider.fromJson(json.getAsJsonObject("output")), json.get("torque").getAsDouble(), json.has("angle") ? json.get("angle").getAsDouble() : json.get("turns").getAsDouble()));
            return new CentrifugeRecipe(id, IngredientIndex.fromJson(json.getAsJsonObject("input")), OutputProvider.fromJson(json.getAsJsonObject("output")), json.get("torque").getAsDouble(), json.has("angle") ? json.get("angle").getAsDouble() : json.get("turns").getAsDouble());
        }

        @Override
        public CentrifugeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new CentrifugeRecipe(id, IngredientIndex.fromNetwork(buf), OutputProvider.fromNetwork(buf), buf.readDouble(), buf.readDouble());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CentrifugeRecipe recipe) {
            recipe.input.toNetwork(buf);
            recipe.output.toNetwork(buf);
            buf.writeDouble(recipe.torque);
            buf.writeDouble(recipe.angle);
        }
    }
}