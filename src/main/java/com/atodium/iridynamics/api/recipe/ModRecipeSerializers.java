package com.atodium.iridynamics.api.recipe;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.recipe.impl.DryingRecipe;
import com.atodium.iridynamics.api.recipe.impl.PileHeatRecipe;
import com.atodium.iridynamics.api.recipe.impl.WashingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final RegistryObject<RecipeSerializer<PileHeatRecipe>> PILE_HEAT = Iridynamics.REGISTRY.recipeSerializer("pile_heat", PileHeatRecipe.Serializer::new).register();
    public static final RegistryObject<RecipeSerializer<DryingRecipe>> DRYING = Iridynamics.REGISTRY.recipeSerializer("drying", DryingRecipe.Serializer::new).register();
    public static final RegistryObject<RecipeSerializer<WashingRecipe>> WASHING = Iridynamics.REGISTRY.recipeSerializer("washing", WashingRecipe.Serializer::new).register();

    public static void init() {

    }
}