package com.atodium.iridynamics.api.recipe;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.recipe.impl.*;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final RegistryObject<RecipeType<HeatRecipe>> HEAT = register("heat");
    public static final RegistryObject<RecipeType<PileHeatRecipe>> PILE_HEAT = register("pile_heat");
    public static final RegistryObject<RecipeType<DryingRecipe>> DRYING = register("drying");
    public static final RegistryObject<RecipeType<WashingRecipe>> WASHING = register("washing");
    public static final RegistryObject<RecipeType<ToolCraftingRecipe>> TOOL_CRAFTING = register("tool_crafting");
    public static final RegistryObject<RecipeType<GrindstoneRecipe>> GRINDSTONE = register("grindstone");
    public static final RegistryObject<RecipeType<CrushingRecipe>> CRUSHING = register("crushing");
    public static final RegistryObject<RecipeType<PotteryRecipe>> POTTERY = register("pottery");
    public static final RegistryObject<RecipeType<CentrifugeRecipe>> CENTRIFUGE = register("centrifuge");
    public static final RegistryObject<RecipeType<GrinderRecipe>> GRINDER = register("grinder");

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