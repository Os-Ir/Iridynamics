package com.atodium.iridynamics.api.recipe;

import com.atodium.iridynamics.api.recipe.container.ItemStackContainer;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Map;

public class RecipeUtil {
    private static final Map<RecipeType<?>, List<Recipe<?>>> RECIPE_CACHE = Maps.newHashMap();

    public static RecipeManager getRecipeManager() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) return server.getRecipeManager();
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) return level.getRecipeManager();
        throw new IllegalStateException("Can not get RecipeManager from MinecraftServer and ClientLevel");
    }

    public static void clearCache() {
        RECIPE_CACHE.clear();
    }

    public static ItemStackContainer container(Item item) {
        return container(item, 1);
    }

    public static ItemStackContainer container(Item item, int count) {
        return new ItemStackContainer(new ItemStack(item, count));
    }

    public static ItemStackContainer container(ItemStack stack) {
        return new ItemStackContainer(stack);
    }

    public static <C extends Container, T extends Recipe<C>> List<T> getAllRecipes(RecipeType<T> type) {
        return getAllRecipes(getRecipeManager(), type);
    }

    @SuppressWarnings("unchecked")
    public static <C extends Container, T extends Recipe<C>> List<T> getAllRecipes(RecipeManager manager, RecipeType<T> type) {
        if (!RECIPE_CACHE.containsKey(type)) RECIPE_CACHE.put(type, (List<Recipe<?>>) manager.getAllRecipesFor(type));
        return (List<T>) RECIPE_CACHE.get(type);
    }

    public static <C extends Container, T extends Recipe<C>> T getRecipe(RecipeType<T> type, C container) {
        for (T recipe : getAllRecipes(getRecipeManager(), type)) if (recipe.matches(container, null)) return recipe;
        return null;
    }

    public static <C extends Container, T extends Recipe<C>> T getRecipe(Level level, RecipeType<T> type, C container) {
        for (T recipe : getAllRecipes(level.getRecipeManager(), type))
            if (recipe.matches(container, level)) return recipe;
        return null;
    }
}
