package com.atodium.iridynamics.common.entity;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.gui.TextureArea;
import com.atodium.iridynamics.network.ModNetworkHandler;
import com.atodium.iridynamics.network.ProjectileDataPacket;
import com.google.common.collect.Maps;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkDirection;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class ProjectileBaseEntity extends AbstractArrow {
    public static final TextureArea SHOOT_OVERLAY = TextureArea.createTexture(Iridynamics.rl("textures/gui/hud/shoot_overlay.png"), 0.0f, 0.0f, 0.5f, 1.0f);
    public static final TextureArea SHOOT_OVERLAY_KILLED = TextureArea.createTexture(Iridynamics.rl("textures/gui/hud/shoot_overlay.png"), 0.5f, 0.0f, 0.5f, 1.0f);

    @OnlyIn(Dist.CLIENT)
    public static final Map<AbstractClientPlayer, Pair<Long, Boolean>> LAST_HIT_TIME = Maps.newHashMap();

    protected BlockPos groundPos;
    protected BlockState lastGround;
    protected int inAirTime;

    public ProjectileBaseEntity(EntityType<? extends ProjectileBaseEntity> type, Level level) {
        super(type, level);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onPlayerHit(AbstractClientPlayer player, boolean killed) {
        LAST_HIT_TIME.put(player, Pair.of(System.currentTimeMillis(), killed));
    }

    @OnlyIn(Dist.CLIENT)
    public static Pair<Long, Boolean> playerLastHitTime(Player player) {
        for (Map.Entry<AbstractClientPlayer, Pair<Long, Boolean>> entry : LAST_HIT_TIME.entrySet())
            if (entry.getKey().getUUID().equals(player.getUUID())) return entry.getValue();
        return Pair.of(0L, false);
    }

    @Override
    public void tick() {
        this.baseTick();
        BlockState ground = null;
        if (this.groundPos != null) ground = this.level.getBlockState(this.groundPos);
        if (ground != null && ground.getMaterial() != Material.AIR) {
            VoxelShape shape = ground.getCollisionShape(this.level, this.groundPos);
            if (!shape.isEmpty()) for (AABB aabb : shape.toAabbs())
                if (aabb.contains(this.position())) {
                    this.inGround = true;
                    break;
                }
        }
        if (this.inGround) {
            if (ground == this.lastGround) {
                this.inGroundTime++;
                if (this.inGroundTime >= this.maxTicksInGround()) this.remove(RemovalReason.DISCARDED);
            } else {
                this.inGround = false;
                this.setDeltaMovement(this.getDeltaMovement().multiply(this.random.nextFloat() * 0.2, this.random.nextFloat() * 0.2, this.random.nextFloat() * 0.2));
                this.inAirTime = 0;
                this.inGroundTime = 0;
            }
        } else {
            this.inGroundTime = 0;
            this.inAirTime++;
            if (this.inAirTime >= this.maxTicksInAir()) {
                this.remove(RemovalReason.DISCARDED);
                return;
            }
            Vec3 pos = this.position();
            Vec3 velocity = this.getDeltaMovement();
            Vec3 nextPos = pos.add(velocity);
            BlockHitResult rayTrace = this.level.clip(new ClipContext(pos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (rayTrace.getType() == BlockHitResult.Type.BLOCK) nextPos = rayTrace.getLocation();
            List<Pair<Double, Entity>> entityHit = Lists.newArrayList();
            double distance = nextPos.distanceTo(pos);
            for (Entity entity : this.level.getEntities(this, this.getBoundingBox().expandTowards(velocity).inflate(1.0D), this::canHitEntity)) {
                AABB aabb = entity.getBoundingBox().inflate(this.getCollisionWidth() / 2);
                Optional<Vec3> optional = aabb.clip(pos, nextPos);
                if (optional.isPresent()) {
                    double d = pos.distanceTo(optional.get());
                    if (d < distance) entityHit.add(Pair.of(d, entity));
                }
            }
            Entity owner = this.getOwner();
            entityHit.removeIf((pair) -> {
                Entity entity = pair.getRight();
                return entity instanceof Player && owner instanceof Player && !((Player) owner).canHarmPlayer((Player) entity);
            });
            if (entityHit.isEmpty()) {
                if (rayTrace.getType() == BlockHitResult.Type.BLOCK && !ForgeEventFactory.onProjectileImpact(this, rayTrace)) {
                    this.onHitBlock(rayTrace);
                    this.hasImpulse = true;
                }
                velocity = this.getDeltaMovement();
                this.setPosRaw(this.getX() + velocity.x, this.getY() + velocity.y, this.getZ() + velocity.z);
            } else {
                entityHit.sort(Comparator.comparing(Pair::getLeft));
                int size = entityHit.size();
                double usedTick = 0;
                double currentMove = 0;
                double velo = velocity.length();
                double loss = this.getPierceVelocityLoss();
                for (int i = 0; i < size; i++) {
                    double p = entityHit.get(i).getLeft();
                    double add = (p - currentMove) / velo;
                    if (usedTick + add > 1) break;
                    Entity entity = entityHit.get(i).getRight();
                    this.onHitEntity(new EntityHitResult(entity));
                    usedTick += add;
                    currentMove = p;
                    velo = Math.max(velo - loss, 0);
                    this.setDeltaMovement(velocity.normalize().scale(velo));
                }
                currentMove += (1 - usedTick) * velo;
                if (currentMove - distance > -0.05) {
                    if (rayTrace.getType() == BlockHitResult.Type.BLOCK && !ForgeEventFactory.onProjectileImpact(this, rayTrace))
                        this.onHitBlock(rayTrace);
                    velocity = this.getDeltaMovement();
                    this.setPosRaw(pos.x + velocity.x, pos.y + velocity.y, pos.z + velocity.z);
                } else {
                    pos = pos.add(nextPos.subtract(pos).scale(currentMove / distance));
                    this.setPosRaw(pos.x, pos.y, pos.z);
                }
                this.hasImpulse = true;
            }
            velocity = this.getDeltaMovement();
            pos = this.position();
            double vx = velocity.x;
            double vy = velocity.y;
            double vz = velocity.z;
            double vh = Math.sqrt(vx * vx + vz * vz);
            this.setXRot((float) (Mth.atan2(vy, vh) * 180 / Math.PI));
            this.setYRot((float) (Mth.atan2(vx, vz) * 180 / Math.PI));
            this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
            this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
            double decay = this.isInWater() ? 0.6 : 0.99;
            this.setDeltaMovement(this.getDeltaMovement().scale(decay).add(0, -this.getGravity(), 0));
            this.setPos(pos.x, pos.y, pos.z);
            this.checkInsideBlocks();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult rayTrace) {
        Entity entity = rayTrace.getEntity();
        int damage = Mth.ceil(this.getDeltaMovement().length() * this.getBaseDamage());
        Entity owner = this.getOwner();
        DamageSource damageSource;
        if (owner == null) damageSource = DamageSource.arrow(this, this);
        else {
            damageSource = DamageSource.arrow(this, owner);
            if (owner instanceof LivingEntity) ((LivingEntity) owner).setLastHurtMob(entity);
        }
        if (entity.hurt(damageSource, damage)) {
            if (this.getOwner() instanceof ServerPlayer player && entity instanceof LivingEntity living)
                ModNetworkHandler.CHANNEL.sendTo(new ProjectileDataPacket(player.getUUID(), living.isDeadOrDying()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            if (entity instanceof LivingEntity living) {
                if (!this.level.isClientSide && owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(living, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, living);
                }
                this.doPostHurtEffects(living);
                living.invulnerableTime = 0;
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult rayTrace) {
        this.groundPos = rayTrace.getBlockPos();
        this.lastGround = this.level.getBlockState(this.groundPos);
        BlockState state = this.level.getBlockState(rayTrace.getBlockPos());
        state.onProjectileHit(this.level, state, rayTrace, this);
        Vec3 vector3d = rayTrace.getLocation().subtract(this.position());
        this.setDeltaMovement(vector3d);
        Vec3 vector3d1 = vector3d.normalize().scale(0.05);
        this.setPosRaw(this.getX() - vector3d1.x, this.getY() - vector3d1.y, this.getZ() - vector3d1.z);
        this.inGround = true;
    }

    @Override
    public boolean isCritArrow() {
        return false;
    }

    @Override
    public boolean shotFromCrossbow() {
        return false;
    }

    @Override
    public byte getPierceLevel() {
        return 0;
    }

    public int maxTicksInAir() {
        return 1200;
    }

    public double getGravity() {
        return 0.05;
    }

    public double getPierceVelocityLoss() {
        return 5;
    }

    @Override
    public abstract ItemStack getPickupItem();

    public abstract int maxTicksInGround();

    public abstract double getCollisionWidth();
}