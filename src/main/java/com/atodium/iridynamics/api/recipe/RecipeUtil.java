package com.atodium.iridynamics.api.recipe;

import com.atodium.iridynamics.api.recipe.container.ItemStackContainer;
import com.atodium.iridynamics.api.util.data.DataUtil;
import com.google.common.collect.Lists;
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

    public static boolean hasRawRecipeManager() {
        return ServerLifecycleHooks.getCurrentServer() != null || Minecraft.getInstance().level != null;
    }

    public static RecipeManager getRawRecipeManager() {
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
        if (!hasRawRecipeManager()) return null;
        return getAllRecipes(getRawRecipeManager(), type);
    }

    public static <C extends Container, T extends Recipe<C>> List<T> getAllRecipes(RecipeManager manager, RecipeType<T> type) {
        if (!RECIPE_CACHE.containsKey(type)) RECIPE_CACHE.put(type, DataUtil.cast(manager.getAllRecipesFor(type)));
        return DataUtil.cast(RECIPE_CACHE.get(type));
    }

    public static <C extends Container, T extends Recipe<C>> T getRecipe(RecipeType<T> type, C container) {
        if (!hasRawRecipeManager()) return null;
        for (T recipe : getAllRecipes(getRawRecipeManager(), type)) if (recipe.matches(container, null)) return recipe;
        return null;
    }

    public static <C extends Container, T extends Recipe<C>> T getRecipe(Level level, RecipeType<T> type, C container) {
        for (T recipe : getAllRecipes(level.getRecipeManager(), type))
            if (recipe.matches(container, level)) return recipe;
        return null;
    }

    public static <C extends Container, T extends Recipe<C>> List<T> getAllValidRecipes(RecipeType<T> type, C container) {
        List<T> list = Lists.newArrayList();
        if (!hasRawRecipeManager()) return list;
        for (T recipe : getAllRecipes(getRawRecipeManager(), type)) if (recipe.matches(container, null)) list.add(recipe);
        return list;
    }

    public static <C extends Container, T extends Recipe<C>> List<T> getAllValidRecipes(Level level, RecipeType<T> type, C container) {
        List<T> list = Lists.newArrayList();
        for (T recipe : getAllRecipes(level.getRecipeManager(), type))
            if (recipe.matches(container, level)) list.add(recipe);
        return list;
    }
}
