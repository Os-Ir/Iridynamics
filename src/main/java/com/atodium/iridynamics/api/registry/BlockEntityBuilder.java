package com.atodium.iridynamics.api.registry;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockEntityBuilder<H extends BlockEntity> extends AbstractBuilder<BlockEntityType<H>> {
    protected final Supplier<BlockEntityType<H>> supplier;

    protected BlockEntityBuilder(ModRegistry registry, String name, Supplier<BlockEntityType<H>> supplier) {
        super(registry, name);
        this.supplier = supplier;
    }

    public static <H extends BlockEntity> BlockEntityBuilder<H> builder(ModRegistry registry, String name, Supplier<BlockEntityType<H>> supplier) {
        return new BlockEntityBuilder<>(registry, name, supplier);
    }

    @Override
    public RegistryObject<BlockEntityType<H>> register() {
        super.register();
        return this.registry.getBlockEntityRegistry().register(this.name, this.supplier);
    }
}