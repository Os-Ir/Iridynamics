package com.atodium.iridynamics.api.recipe.impl;

import com.atodium.iridynamics.api.capability.IPottery;
import com.atodium.iridynamics.api.capability.PotteryCapability;
import com.atodium.iridynamics.api.recipe.IngredientIndex;
import com.atodium.iridynamics.api.recipe.ModRecipeSerializers;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.OutputProvider;
import com.atodium.iridynamics.api.recipe.container.ItemStackContainer;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record PotteryRecipe(ResourceLocation id, IngredientIndex input, OutputProvider output,
                            int[] carved) implements ISpecialRecipe<ItemStackContainer> {
    @Override
    public boolean matches(ItemStackContainer inventory, Level level) {
        ItemStack stack = inventory.getItem();
        if (!this.input.test(stack)) return false;
        IPottery pottery = stack.getCapability(PotteryCapability.POTTERY).orElseThrow(NullPointerException::new);
        return true;
    }

    public boolean matchesCarving(ItemStackContainer inventory, Level level) {
        ItemStack stack = inventory.getItem();
        if (!this.input.test(stack)) return false;
        IPottery pottery = stack.getCapability(PotteryCapability.POTTERY).orElseThrow(NullPointerException::new);
        for (int i = 0; i < 12; i++)
            if (!MathUtil.between(pottery.getCarved(i), this.carved[i] - 1, this.carved[i] + 1)) return false;
        return true;
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
    public RecipeSerializer<PotteryRecipe> getSerializer() {
        return ModRecipeSerializers.POTTERY.get();
    }

    @Override
    public RecipeType<PotteryRecipe> getType() {
        return ModRecipeTypes.POTTERY.get();
    }

    public static class Serializer extends RecipeSerializerImpl<ItemStackContainer, PotteryRecipe> {
        @Override
        public PotteryRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray radiusJson = json.getAsJsonArray("carved");
            int[] radius = new int[radiusJson.size()];
            for (int i = 0; i < radius.length; i++) radius[i] = radiusJson.get(i).getAsInt();
            return new PotteryRecipe(id, IngredientIndex.fromJson(json.getAsJsonObject("input")), OutputProvider.fromJson(json.getAsJsonObject("output")), radius);
        }

        @Override
        public PotteryRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readInt();
            int[] radius = new int[size];
            for (int i = 0; i < size; i++) radius[i] = buf.readInt();
            return new PotteryRecipe(id, IngredientIndex.fromNetwork(buf), OutputProvider.fromNetwork(buf), radius);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, PotteryRecipe recipe) {
            buf.writeInt(recipe.carved.length);
            for (int r : recipe.carved) buf.writeInt(r);
            recipe.input.toNetwork(buf);
            recipe.output.toNetwork(buf);
        }
    }
}