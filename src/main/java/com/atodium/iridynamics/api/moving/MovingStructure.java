package com.atodium.iridynamics.api.moving;

import net.minecraft.world.phys.AABB;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class MovingStructure {
    protected List<AABB> collisionVolume;
    // Entity

    public MovingStructure() {
        this.collisionVolume = Lists.newArrayList();
    }
}