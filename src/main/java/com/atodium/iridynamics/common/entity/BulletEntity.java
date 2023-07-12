package com.atodium.iridynamics.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class BulletEntity extends ProjectileBaseEntity {
    public BulletEntity(EntityType<BulletEntity> type, Level level) {
        super(type, level);
    }

    public BulletEntity(Level level, double x, double y, double z) {
        super(ModEntities.BULLET.get(), level);
        this.setPos(x, y, z);
        this.setBaseDamage(2);
    }

    public BulletEntity(Level level, LivingEntity entity) {
        this(level, entity.getX(), entity.getEyeY() - 0.1, entity.getZ());
        this.setOwner(entity);
    }

    @Override
    public ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public int maxTicksInGround() {
        return 0;
    }

    @Override
    public double getCollisionWidth() {
        return 0.125;
    }

    @Override
    public void defineSynchedData() {

    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {

    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {

    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}