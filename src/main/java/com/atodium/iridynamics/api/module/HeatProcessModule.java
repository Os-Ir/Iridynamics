package com.atodium.iridynamics.api.module;

import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.IHeat;
import com.atodium.iridynamics.api.heat.IPhasePortrait;
import com.atodium.iridynamics.api.heat.impl.HeatProcessPhasePortrait;
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.RecipeUtil;
import com.atodium.iridynamics.api.recipe.container.ItemStackContainer;
import com.atodium.iridynamics.api.recipe.impl.HeatRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

public class HeatProcessModule {
    public static IPhasePortrait checkItemHeatProcess(ItemStack stack, double capacity) {
        HeatRecipe recipe = RecipeUtil.getRecipe(ModRecipeTypes.HEAT.get(), RecipeUtil.container(stack));
        if (recipe != null) return new HeatProcessPhasePortrait(capacity, recipe.temperature(), recipe.energy());
        else return new SolidPhasePortrait(capacity);
    }

    public static ItemStack getHeatProcessResult(ItemStack stack) {
        LazyOptional<IHeat> optional = stack.getCapability(HeatCapability.HEAT);
        if (!optional.isPresent()) return stack;
        IHeat heat = optional.orElseThrow(NullPointerException::new);
        if (!(heat.getPhasePortrait() instanceof HeatProcessPhasePortrait process)) return stack;
        if (process.progress(heat.getEnergy()) < 1.0) return stack;
        ItemStackContainer container = RecipeUtil.container(stack);
        HeatRecipe recipe = RecipeUtil.getRecipe(ModRecipeTypes.HEAT.get(), container);
        return recipe == null ? stack : recipe.assemble(container);
    }

    public static void updateHeatProcess(IItemHandlerModifiable inventory) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            LazyOptional<IHeat> optional = stack.getCapability(HeatCapability.HEAT);
            if (!optional.isPresent()) continue;
            IHeat heat = optional.orElseThrow(NullPointerException::new);
            if (!(heat.getPhasePortrait() instanceof HeatProcessPhasePortrait process)) continue;
            if (process.progress(heat.getEnergy()) < 1.0) continue;
            ItemStackContainer container = RecipeUtil.container(stack);
            HeatRecipe recipe = RecipeUtil.getRecipe(ModRecipeTypes.HEAT.get(), container);
            if (recipe != null) inventory.setStackInSlot(i, recipe.assemble(container));
        }
    }
}