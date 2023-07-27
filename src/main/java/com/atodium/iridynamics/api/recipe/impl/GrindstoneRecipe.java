package com.atodium.iridynamics.api.recipe.impl;

import com.atodium.iridynamics.api.module.ToolModule;
import com.atodium.iridynamics.api.recipe.IngredientIndex;
import com.atodium.iridynamics.api.recipe.ModRecipeSerializers;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.OutputProvider;
import com.atodium.iridynamics.api.recipe.container.ToolInventoryContainer;
import com.atodium.iridynamics.api.recipe.RecipeSerializerImpl;
import com.atodium.iridynamics.api.tool.IToolInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record GrindstoneRecipe(ResourceLocation id, IngredientIndex[] input, OutputProvider output,
                               IToolInfo[] tools) implements ISpecialRecipe<ToolInventoryContainer> {
    public void consume(ToolInventoryContainer container) {
        for (int i = 0; i < this.input.length; i++)
            if (!this.input[i].isEmpty() && this.input[i].testEqual(container.getItem(i)))
                this.input[i].consume(container.getItem(i));
        for (int i = 0; i < this.tools.length; i++)
            if (ToolModule.isToolNonnullEquals(this.tools[i], container.getTool(i)))
                ToolModule.toolCraftingDamage(container.getToolItemStack(i));
    }

    @Override
    public boolean matches(ToolInventoryContainer container, Level level) {
        for (int i = 0; i < this.input.length; i++)
            if ((!this.input[i].isEmpty() && !this.input[i].testEqual(container.getItem(i))) || (this.input[i].isEmpty() && !container.getItem(i).isEmpty()))
                return false;
        for (int i = this.input.length; i < container.getContainerSize(); i++)
            if (!container.getItem(i).isEmpty()) return false;
        for (int i = 0; i < this.tools.length; i++)
            if (!ToolModule.isToolEquals(this.tools[i], container.getTool(i))) return false;
        for (int i = this.tools.length; i < container.getToolCount(); i++)
            if (container.getTool(i) != null) return false;
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return this.output.withoutDecorate();
    }

    @Override
    public ItemStack assemble(ToolInventoryContainer inventory) {
        return this.output.apply(inventory.getAllItemStacks());
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<GrindstoneRecipe> getSerializer() {
        return ModRecipeSerializers.GRINDSTONE.get();
    }

    @Override
    public RecipeType<GrindstoneRecipe> getType() {
        return ModRecipeTypes.GRINDSTONE.get();
    }

    public static class Serializer extends RecipeSerializerImpl<ToolInventoryContainer, GrindstoneRecipe> {
        @Override
        public GrindstoneRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray inputJson = json.getAsJsonArray("input");
            IngredientIndex[] input = new IngredientIndex[inputJson.size()];
            for (int i = 0; i < input.length; i++)
                input[i] = IngredientIndex.fromJson(inputJson.get(i).getAsJsonObject());
            JsonArray toolJson = json.getAsJsonArray("tool");
            IToolInfo[] tools = new IToolInfo[toolJson.size()];
            for (int i = 0; i < tools.length; i++)
                tools[i] = ToolModule.TOOL_INFO.get(new ResourceLocation(toolJson.get(i).getAsString()));
            return new GrindstoneRecipe(id, input, OutputProvider.fromJson(json.getAsJsonObject("output")), tools);
        }

        @Override
        public GrindstoneRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            IngredientIndex[] input = new IngredientIndex[buf.readInt()];
            for (int i = 0; i < input.length; i++) input[i] = IngredientIndex.fromNetwork(buf);
            OutputProvider output = OutputProvider.fromNetwork(buf);
            IToolInfo[] tools = new IToolInfo[buf.readInt()];
            for (int i = 0; i < tools.length; i++) tools[i] = ToolModule.TOOL_INFO.get(buf.readResourceLocation());
            return new GrindstoneRecipe(id, input, output, tools);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, GrindstoneRecipe recipe) {
            buf.writeInt(recipe.input.length);
            for (IngredientIndex ingredient : recipe.input) ingredient.toNetwork(buf);
            recipe.output.toNetwork(buf);
            for (IToolInfo info : recipe.tools) buf.writeResourceLocation(info.getRegistryName());
        }
    }
}