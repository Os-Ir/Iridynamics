package com.atodium.iridynamics.api.registry;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class RecipeSerializerBuilder<C extends Container, R extends Recipe<C>> extends AbstractBuilder<RecipeSerializer<R>> {
    protected final Supplier<RecipeSerializer<R>> supplier;

    protected RecipeSerializerBuilder(ModRegistry registry, String name, Supplier<RecipeSerializer<R>> supplier) {
        super(registry, name);
        this.supplier = supplier;
    }

    public static <C extends Container, R extends Recipe<C>> RecipeSerializerBuilder<C, R> builder(ModRegistry registry, String name, Supplier<RecipeSerializer<R>> supplier) {
        return new RecipeSerializerBuilder<>(registry, name, supplier);
    }

    @Override
    public RegistryObject<RecipeSerializer<R>> register() {
        super.register();
        return this.registry.getRecipeSerializerRegistry().register(this.name, this.supplier);
    }
}