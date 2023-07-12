package com.atodium.iridynamics.common.entity;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.module.moving.MovingStructureEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final RegistryObject<EntityType<BulletEntity>> BULLET = Iridynamics.REGISTRY.entity("bullet", () -> EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC).sized(0.125f, 0.125f).build("bullet")).register();
    public static final RegistryObject<EntityType<MovingStructureEntity>> MOVING_STRUCTURE = Iridynamics.REGISTRY.entity("moving_structure", () -> EntityType.Builder.of(MovingStructureEntity::new, MobCategory.MISC).build("moving_structure")).register();

    public static void init() {

    }
}