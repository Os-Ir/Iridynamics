package com.atodium.iridynamics.api.recipe;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.recipe.impl.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final RegistryObject<RecipeSerializer<HeatRecipe>> HEAT = Iridynamics.REGISTRY.recipeSerializer("heat", HeatRecipe.Serializer::new).register();
    public static final RegistryObject<RecipeSerializer<PileHeatRecipe>> PILE_HEAT = Iridynamics.REGISTRY.recipeSerializer("pile_heat", PileHeatRecipe.Serializer::new).register();
    public static final RegistryObject<RecipeSerializer<DryingRecipe>> DRYING = Iridynamics.REGISTRY.recipeSerializer("drying", DryingRecipe.Serializer::new).register();
    public static final RegistryObject<RecipeSerializer<WashingRecipe>> WASHING = Iridynamics.REGISTRY.recipeSerializer("washing", WashingRecipe.Serializer::new).register();
    public static final RegistryObject<RecipeSerializer<ToolCraftingRecipe>> TOOL_CRAFTING = Iridynamics.REGISTRY.recipeSerializer("tool_crafting", ToolCraftingRecipe.Serializer::new).register();
    public static final RegistryObject<RecipeSerializer<GrindstoneRecipe>> GRINDSTONE = Iridynamics.REGISTRY.recipeSerializer("grindstone", GrindstoneRecipe.Serializer::new).register();
    public static final RegistryObject<RecipeSerializer<CrushingRecipe>> CRUSHING = Iridynamics.REGISTRY.recipeSerializer("crushing", CrushingRecipe.Serializer::new).register();

    public static void init() {

    }
}