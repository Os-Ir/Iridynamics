package com.atodium.iridynamics.api.module.rotate;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.IRotateNode;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import net.minecraft.resources.ResourceLocation;

public class RotateModule {
    public static final UnorderedRegistry<ResourceLocation, IRotateNode.Serializer> SERIALIZERS = new UnorderedRegistry<>();

    public static void init() {
        SERIALIZERS.register(Iridynamics.rl("axle"), Axle.SERIALIZER);
        SERIALIZERS.register(Iridynamics.rl("gearbox"), Gearbox.SERIALIZER);
    }
}