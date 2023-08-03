package com.atodium.iridynamics.api.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.logging.Level;

public abstract class MovingStructure<T extends MovingStructure<T>> {
    protected List<AABB> collisionVolume;
    protected MovingStructureEntity<T> entity;
    protected List<Restriction> restrictions;

    public MovingStructure() {
        this.collisionVolume = Lists.newArrayList();
        this.restrictions = Lists.newArrayList();
    }

    public abstract StructureType<T> type();

    public abstract LazyOptional<MovingStructureEntity<T>> assemble(Level level, BlockPos pos);

    public abstract void tick();

    public void attachRestriction(Restriction restriction) {
        this.restrictions.add(restriction);
    }

    public Vec3 position() {
        if (this.entity != null) return this.entity.position();
        return Vec3.ZERO;
    }
}