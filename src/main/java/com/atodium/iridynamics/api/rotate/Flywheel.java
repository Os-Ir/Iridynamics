package com.atodium.iridynamics.api.rotate;

import com.atodium.iridynamics.api.util.math.IntFraction;
import com.atodium.iridynamics.api.util.math.MathUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class Flywheel implements IRotateNode {
    public static final Serializer SERIALIZER = new Serializer();

    private final Direction direction;
    private double angle, angularVelocity;

    public Flywheel(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Serializer serializer() {
        return SERIALIZER;
    }

    @Override
    public boolean isConnectable(Direction direction) {
        return this.direction == direction || this.direction == direction.getOpposite();
    }

    @Override
    public boolean isRelated(Direction from, Direction to) {
        return (this.direction == from && this.direction == to.getOpposite()) || (this.direction == to && this.direction == from.getOpposite());
    }

    @Override
    public IntFraction getRelation(Direction from, Direction to) {
        if (this.isRelated(from, to)) return IntFraction.NEG_ONE;
        return null;
    }

    @Override
    public double getAngle(Direction direction) {
        return direction == this.direction ? this.angle : direction == this.direction.getOpposite() ? MathUtil.castAngle(-this.angle) : 0.0;
    }

    @Override
    public double getAngularVelocity(Direction direction) {
        return direction == this.direction ? this.angularVelocity : direction == this.direction.getOpposite() ? -this.angularVelocity : 0.0;
    }

    @Override
    public void setAngle(Direction direction, double angle) {
        if (direction == this.direction) this.angle = MathUtil.castAngle(angle);
        if (direction == this.direction.getOpposite()) this.angle = MathUtil.castAngle(-angle);
    }

    @Override
    public void setAngularVelocity(Direction direction, double angularVelocity) {
        if (direction == this.direction) this.angularVelocity = angularVelocity;
        if (direction == this.direction.getOpposite()) this.angularVelocity = -angularVelocity;
    }

    @Override
    public double getInertia(Direction direction) {
        if (this.isConnectable(direction)) return 400.0;
        return 0.0;
    }

    @Override
    public double getTorque(Direction direction) {
        return 0.0;
    }

    @Override
    public double getFriction(Direction direction) {
        if (this.isConnectable(direction)) return 0.6;
        return 0.0;
    }

    @Override
    public double maxAngularVelocity(Direction direction) {
        return 300.0;
    }

    public static class Serializer implements IRotateNode.Serializer<Flywheel> {
        @Override
        public Flywheel deserialize(CompoundTag tag) {
            return new Flywheel(Direction.from3DDataValue(tag.getInt("direction")));
        }

        @Override
        public CompoundTag serialize(Flywheel node) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("direction", node.direction.get3DDataValue());
            return tag;
        }

        @Override
        public CompoundTag writeSyncTag(Flywheel node) {
            CompoundTag tag = new CompoundTag();
            tag.putDouble("angle", node.angle);
            tag.putDouble("angularVelocity", node.angularVelocity);
            return tag;
        }

        @Override
        public void readSyncTag(Flywheel node, CompoundTag tag) {
            node.angle = tag.getDouble("angle");
            node.angularVelocity = tag.getDouble("angularVelocity");
        }
    }
}