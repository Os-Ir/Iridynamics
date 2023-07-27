package com.atodium.iridynamics.api.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.extensions.IForgeRecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class RecipeSerializerImpl<C extends Container, R extends Recipe<C>> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<R>, IForgeRecipeSerializer<R> {

}