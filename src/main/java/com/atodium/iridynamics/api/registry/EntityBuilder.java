package com.atodium.iridynamics.api.registry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class EntityBuilder<H extends Entity> extends AbstractBuilder<EntityType<H>> {
    protected final Supplier<EntityType<H>> supplier;

    protected EntityBuilder(ModRegistry registry, String name, Supplier<EntityType<H>> supplier) {
        super(registry, name);
        this.supplier = supplier;
    }

    public static <H extends Entity> EntityBuilder<H> builder(ModRegistry registry, String name, Supplier<EntityType<H>> supplier) {
        return new EntityBuilder<>(registry, name, supplier);
    }

    @Override
    public RegistryObject<EntityType<H>> register() {
        super.register();
        return this.registry.getEntityRegistry().register(this.name, this.supplier);
    }
}