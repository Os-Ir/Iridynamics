package com.atodium.iridynamics.api.registry;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class RecipeTypeBuilder<C extends Container, R extends Recipe<C>> extends AbstractBuilder<RecipeType<R>> {
    protected final Supplier<RecipeType<R>> supplier;

    protected RecipeTypeBuilder(ModRegistry registry, String name, Supplier<RecipeType<R>> supplier) {
        super(registry, name);
        this.supplier = supplier;
    }

    public static <C extends Container, R extends Recipe<C>> RecipeTypeBuilder<C, R> builder(ModRegistry registry, String name, Supplier<RecipeType<R>> supplier) {
        return new RecipeTypeBuilder<>(registry, name, supplier);
    }

    @Override
    public RegistryObject<RecipeType<R>> register() {
        super.register();
        return this.registry.getRecipeTypeRegistry().register(this.name, this.supplier);
    }
}