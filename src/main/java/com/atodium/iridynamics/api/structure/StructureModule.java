package com.atodium.iridynamics.api.structure;

import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class StructureModule {
    public static final UnorderedRegistry<ResourceLocation, StructureType<?>> STRUCTURES = new UnorderedRegistry<>();

    public static <T extends MovingStructure<T>> void registerStructure(ResourceLocation id, Supplier<MovingStructure<T>> supplier) {
        STRUCTURES.register(id, new StructureType<>(id, supplier));
    }

    public static <T extends MovingStructure<T>> void registerStructure(StructureType<T> type) {
        STRUCTURES.register(type.id(), type);
    }

    public static <T extends MovingStructure<T>> StructureType<T> getStructureById(ResourceLocation id) {
        return DataUtil.cast(STRUCTURES.get(id));
    }

    public static <T extends MovingStructure<T>> T createStructureById(ResourceLocation id) {
        if (STRUCTURES.containsKey(id)) return DataUtil.cast(STRUCTURES.get(id).create());
        return null;
    }
}