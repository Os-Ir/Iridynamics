package com.atodium.iridynamics.api.recipe;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.recipe.impl.DryingRecipe;
import com.atodium.iridynamics.api.recipe.impl.PileHeatRecipe;
import com.atodium.iridynamics.api.recipe.impl.WashingRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final RegistryObject<RecipeType<PileHeatRecipe>> PILE_HEAT = register("pile_heat");
    public static final RegistryObject<RecipeType<DryingRecipe>> DRYING = register("drying");
    public static final RegistryObject<RecipeType<WashingRecipe>> WASHING = register("washing");

    public static void init() {

    }

    private static <C extends Container, R extends Recipe<C>> RegistryObject<RecipeType<R>> register(String name) {
        return Iridynamics.REGISTRY.recipeType(name, () -> new RecipeType<R>() {
            @Override
            public String toString() {
                return name;
            }
        }).register();
    }
}