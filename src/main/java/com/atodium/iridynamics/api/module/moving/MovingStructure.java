package com.atodium.iridynamics.api.module.moving;

import net.minecraft.world.phys.AABB;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class MovingStructure {
    protected List<AABB> collisionVolume;
    protected MovingStructureEntity entity;

    public MovingStructure() {
        this.collisionVolume = Lists.newArrayList();
    }
}