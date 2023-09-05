package com.atodium.iridynamics.common.blockEntity.rotate;

import com.atodium.iridynamics.api.blockEntity.ISavedDataTickable;
import com.atodium.iridynamics.api.blockEntity.ITipInfoRenderer;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.item.ItemDelegate;
import com.atodium.iridynamics.api.material.ModMaterials;
import com.atodium.iridynamics.api.material.ModSolidShapes;
import com.atodium.iridynamics.api.rotate.IRotateNode;
import com.atodium.iridynamics.api.rotate.RotateModule;
import com.atodium.iridynamics.api.util.math.IntFraction;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class GearboxBlockEntity extends SyncedBlockEntity implements IRotateNode, ISavedDataTickable, ITipInfoRenderer {
    public static final ItemDelegate SMALL_GEAR = ItemDelegate.of(ModSolidShapes.SMALL_GEAR, ModMaterials.IRON);
    public static final ItemDelegate GEAR = ItemDelegate.of(ModSolidShapes.GEAR, ModMaterials.IRON);

    private Direction directionA, directionB;
    private int gearA, gearB;
    private IntFraction ab, ba;
    private double angle, angularVelocity;

    public GearboxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GEARBOX.get(), pos, state);
    }

    @Override
    public void blockTick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        this.sendSyncPacket();
    }

    public void setupRotate(ServerLevel level) {
        RotateModule.updateRotateBlock(level, this.getBlockPos());
        this.sendSyncPacket();
    }

    public List<ItemStack> getAllGearItems() {
        List<ItemStack> items = Lists.newArrayList();
        if (this.directionA != null) items.add(this.gearA == 1 ? SMALL_GEAR.createStack() : GEAR.createStack());
        if (this.directionB != null) items.add(this.gearB == 1 ? SMALL_GEAR.createStack() : GEAR.createStack());
        return items;
    }

    public ItemStack updateGearbox(Direction direction, ItemStack stack) {
        int original = direction == this.directionA ? this.gearA : direction == this.directionB ? this.gearB : 0;
        if (original == 0) {
            if (this.getValidDirections() < 2) {
                if (SMALL_GEAR.is(stack)) {
                    if (this.directionA == null) this.updateGearbox(direction, null, 1, 0);
                    else this.updateGearbox(this.directionA, direction, this.gearA, 1);
                    stack.shrink(1);
                    RotateModule.updateRotateBlock((ServerLevel) this.level, this.getBlockPos());
                } else if (GEAR.is(stack)) {
                    if (this.directionA == null) this.updateGearbox(direction, null, 2, 0);
                    else this.updateGearbox(this.directionA, direction, this.gearA, 2);
                    stack.shrink(1);
                    RotateModule.updateRotateBlock((ServerLevel) this.level, this.getBlockPos());
                }
            }
        } else {
            if (this.directionA == direction) this.updateGearbox(this.directionB, null, this.gearB, 0);
            else this.updateGearbox(this.directionA, null, this.gearA, 0);
            RotateModule.updateRotateBlock((ServerLevel) this.level, this.getBlockPos());
            return original == 1 ? SMALL_GEAR.createStack() : GEAR.createStack();
        }
        return ItemStack.EMPTY;
    }

    private void updateGearbox(Direction directionA, Direction directionB, int gearA, int gearB) {
        this.directionA = directionA;
        this.directionB = directionB;
        this.gearA = gearA;
        this.gearB = gearB;
        if (gearA == 0 || gearB == 0) this.ab = this.ba = IntFraction.ONE;
        else {
            this.ab = new IntFraction(-this.gearA, this.gearB);
            this.ba = new IntFraction(-this.gearB, this.gearA);
        }
    }

    public double getRenderAngle(Direction direction, float partialTicks) {
        return MathUtil.castAngle(this.getAngle(direction));
    }

    @Override
    public float width() {
        return 2.0f;
    }

    @Override
    public float height() {
        return 1.0f;
    }

    @Override
    public void renderInfo(Camera camera, BlockHitResult result, PoseStack transform, MultiBufferSource buffer, float partialTicks) {
        transform.pushPose();
        transform.translate(0.0f, 1.0f, 0.001f);
        transform.scale(1.0f, -1.0f, 1.0f);
        transform.scale(0.125f, 0.125f, 1.0f);
        Minecraft.getInstance().font.draw(transform, "Test", 0.0f, 0.0f, 0x000000);
        transform.popPose();
    }

    public int getValidDirections() {
        return (directionA == null ? 0 : 1) + (directionB == null ? 0 : 1);
    }

    public int getGear(Direction direction) {
        if (direction == this.directionA) return this.gearA;
        if (direction == this.directionB) return this.gearB;
        return 0;
    }

    @Override
    public boolean isConnectable(Direction direction) {
        return direction == this.directionA || direction == this.directionB;
    }

    @Override
    public boolean isRelated(Direction from, Direction to) {
        return (from == this.directionA && to == this.directionB) || (from == this.directionB && to == this.directionA);
    }

    @Override
    public IntFraction getRelation(Direction from, Direction to) {
        if (from == this.directionA && to == this.directionB) return this.ab;
        if (from == this.directionB && to == this.directionA) return this.ba;
        return null;
    }

    @Override
    public double getAngle(Direction direction) {
        return direction == this.directionA ? this.angle : direction == this.directionB ? MathUtil.castAngle(-this.angle * this.gearA / this.gearB) : 0.0;
    }

    @Override
    public double getAngularVelocity(Direction direction) {
        return direction == this.directionA ? this.angularVelocity : direction == this.directionB ? -this.angularVelocity * this.gearA / this.gearB : 0.0;
    }

    @Override
    public void setAngle(Direction direction, double angle) {
        if (direction == this.directionA) this.angle = MathUtil.castAngle(angle);
        if (direction == this.directionB) this.angle = MathUtil.castAngle(-angle * this.gearB / this.gearA);
    }

    @Override
    public void setAngularVelocity(Direction direction, double angularVelocity) {
        if (direction == this.directionA) this.angularVelocity = angularVelocity;
        if (direction == this.directionB) this.angularVelocity = -angularVelocity * this.gearB / this.gearA;
    }

    @Override
    public double getInertia(Direction direction) {
        return 10.0;
    }

    @Override
    public double getTorque(Direction direction) {
        return 0.0;
    }

    @Override
    public double getFriction(Direction direction) {
        return 0.4;
    }

    @Override
    public double maxAngularVelocity(Direction direction) {
        return RotateModule.WOOD_MAX_ANGULAR_VELOCITY;
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {
        if (this.directionA != null) tag.putInt("directionA", this.directionA.get3DDataValue());
        if (this.directionB != null) tag.putInt("directionB", this.directionB.get3DDataValue());
        tag.putInt("gearA", this.gearA);
        tag.putInt("gearB", this.gearB);
        tag.putDouble("angle", this.angle);
        tag.putDouble("angularVelocity", this.angularVelocity);
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.updateGearbox(Direction.from3DDataValue(tag.getInt("directionA")), Direction.from3DDataValue(tag.getInt("directionB")), tag.getInt("gearA"), tag.getInt("gearB"));
        this.angle = tag.getDouble("angle");
        this.angularVelocity = tag.getDouble("angularVelocity");
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        if (this.directionA != null) tag.putInt("directionA", this.directionA.get3DDataValue());
        if (this.directionB != null) tag.putInt("directionB", this.directionB.get3DDataValue());
        tag.putInt("gearA", this.gearA);
        tag.putInt("gearB", this.gearB);
        tag.putDouble("angle", this.angle);
        tag.putDouble("angularVelocity", this.angularVelocity);
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.updateGearbox(tag.contains("directionA") ? Direction.from3DDataValue(tag.getInt("directionA")) : null, tag.contains("directionB") ? Direction.from3DDataValue(tag.getInt("directionB")) : null, tag.getInt("gearA"), tag.getInt("gearB"));
        this.angle = tag.getDouble("angle");
        this.angularVelocity = tag.getDouble("angularVelocity");
    }
}