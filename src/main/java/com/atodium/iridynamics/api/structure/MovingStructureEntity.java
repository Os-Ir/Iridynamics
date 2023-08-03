package com.atodium.iridynamics.api.structure;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public abstract class MovingStructureEntity<T extends MovingStructure<T>> extends Entity {
    protected T structure;

    public MovingStructureEntity(EntityType<MovingStructureEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        if (this.structure == null) {
            this.discard();
            return;
        }
        this.structure.tick();
        super.tick();
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}