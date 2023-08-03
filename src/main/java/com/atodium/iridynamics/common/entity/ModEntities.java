package com.atodium.iridynamics.common.entity;

import com.atodium.iridynamics.Iridynamics;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final RegistryObject<EntityType<BulletEntity>> BULLET = Iridynamics.REGISTRY.entity("bullet", () -> EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC).sized(0.125f, 0.125f).build("bullet")).register();

    public static void init() {

    }
}