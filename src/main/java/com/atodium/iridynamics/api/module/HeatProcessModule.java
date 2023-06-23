package com.atodium.iridynamics.api.module;

import com.atodium.iridynamics.api.heat.IPhasePortrait;
import com.atodium.iridynamics.api.heat.impl.HeatProcessPhasePortrait;
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.RecipeUtil;
import com.atodium.iridynamics.api.recipe.impl.HeatRecipe;
import net.minecraft.world.item.ItemStack;

public class HeatProcessModule {
    public static IPhasePortrait checkItemHeatProcess(ItemStack stack, double capacity) {
        HeatRecipe recipe = RecipeUtil.getRecipe(ModRecipeTypes.HEAT.get(), RecipeUtil.container(stack));
        if (recipe != null) return new HeatProcessPhasePortrait(capacity, recipe.temperature(), recipe.energy());
        else return new SolidPhasePortrait(capacity);
    }
}