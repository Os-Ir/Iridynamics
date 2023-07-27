package com.atodium.iridynamics.api.recipe.impl;

import com.atodium.iridynamics.api.module.ToolModule;
import com.atodium.iridynamics.api.recipe.IngredientIndex;
import com.atodium.iridynamics.api.recipe.ModRecipeSerializers;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.OutputProvider;
import com.atodium.iridynamics.api.recipe.container.ToolInventoryContainer;
import com.atodium.iridynamics.api.recipe.RecipeSerializerImpl;
import com.atodium.iridynamics.api.tool.IToolInfo;
import com.atodium.iridynamics.api.util.data.DataUtil;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Map;

public record ToolCraftingRecipe(ResourceLocation id, IngredientIndex[][] input, OutputProvider output,
                                 IToolInfo[] tools) implements ISpecialRecipe<ToolInventoryContainer> {
    public static final int SIDE_LENGTH = 5;

    public void consume(ToolInventoryContainer container) {
        ItemStack[][] origin = container.toGrid(SIDE_LENGTH);
        ItemStack[][] align = new ItemStack[SIDE_LENGTH][SIDE_LENGTH];
        DataUtil.align(origin, align, (stack) -> !stack.isEmpty(), ItemStack.EMPTY);
        for (int x = 0; x < SIDE_LENGTH; x++)
            for (int y = 0; y < SIDE_LENGTH; y++)
                if (!this.input[y][x].isEmpty() && this.input[y][x].test(align[y][x]))
                    this.input[y][x].consume(align[y][x]);
        for (int i = 0; i < this.tools.length; i++)
            if (ToolModule.isToolNonnullEquals(this.tools[i], container.getTool(i)))
                ToolModule.toolCraftingDamage(container.getToolItemStack(i));
    }

    @Override
    public boolean matches(ToolInventoryContainer container, Level level) {
        ItemStack[][] origin = container.toGrid(SIDE_LENGTH);
        ItemStack[][] align = new ItemStack[SIDE_LENGTH][SIDE_LENGTH];
        DataUtil.align(origin, align, (stack) -> !stack.isEmpty(), ItemStack.EMPTY);
        for (int x = 0; x < SIDE_LENGTH; x++)
            for (int y = 0; y < SIDE_LENGTH; y++)
                if ((!this.input[y][x].isEmpty() && !this.input[y][x].test(align[y][x])) || (this.input[y][x].isEmpty() && !align[y][x].isEmpty()))
                    return false;
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
    public RecipeSerializer<ToolCraftingRecipe> getSerializer() {
        return ModRecipeSerializers.TOOL_CRAFTING.get();
    }

    @Override
    public RecipeType<ToolCraftingRecipe> getType() {
        return ModRecipeTypes.TOOL_CRAFTING.get();
    }

    public static class Serializer extends RecipeSerializerImpl<ToolInventoryContainer, ToolCraftingRecipe> {
        @Override
        public ToolCraftingRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonObject key = json.getAsJsonObject("key");
            JsonArray pattern = json.getAsJsonArray("pattern");
            Map<Character, IngredientIndex> ingredientMap = Maps.newHashMap();
            ingredientMap.put(' ', IngredientIndex.EMPTY);
            IngredientIndex[][] input = new IngredientIndex[SIDE_LENGTH][SIDE_LENGTH];
            for (int i = 0; i < SIDE_LENGTH; i++) {
                String s = pattern.get(i).getAsString();
                if (s == null || s.isEmpty()) s = " ".repeat(SIDE_LENGTH);
                if (s.length() < SIDE_LENGTH) s += " ".repeat(SIDE_LENGTH - s.length());
                char[] chars = s.toCharArray();
                for (int j = 0; j < SIDE_LENGTH; j++) {
                    char c = chars[j];
                    IngredientIndex ingredient;
                    if (ingredientMap.containsKey(c)) ingredient = ingredientMap.get(c);
                    else {
                        ingredient = IngredientIndex.fromJson(key.getAsJsonObject(String.valueOf(c)));
                        ingredientMap.put(c, ingredient);
                    }
                    input[i][j] = ingredient;
                }
            }
            JsonArray toolJson = json.getAsJsonArray("tool");
            IToolInfo[] tools = new IToolInfo[toolJson.size()];
            for (int i = 0; i < tools.length; i++)
                tools[i] = ToolModule.TOOL_INFO.get(new ResourceLocation(toolJson.get(i).getAsString()));
            return new ToolCraftingRecipe(id, input, OutputProvider.fromJson(json.getAsJsonObject("output")), tools);
        }

        @Override
        public ToolCraftingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int width = buf.readInt();
            int height = buf.readInt();
            IngredientIndex[][] input = new IngredientIndex[height][width];
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++) input[y][x] = IngredientIndex.fromNetwork(buf);
            OutputProvider output = OutputProvider.fromNetwork(buf);
            IToolInfo[] tools = new IToolInfo[buf.readInt()];
            for (int i = 0; i < tools.length; i++) tools[i] = ToolModule.TOOL_INFO.get(buf.readResourceLocation());
            return new ToolCraftingRecipe(id, input, output, tools);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ToolCraftingRecipe recipe) {
            int width = DataUtil.width(recipe.input);
            int height = DataUtil.height(recipe.input);
            buf.writeInt(width);
            buf.writeInt(height);
            for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) recipe.input[y][x].toNetwork(buf);
            recipe.output.toNetwork(buf);
            buf.writeInt(recipe.tools.length);
            for (IToolInfo info : recipe.tools) buf.writeResourceLocation(info.getRegistryName());
        }
    }
}