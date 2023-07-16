package com.atodium.iridynamics.api.rotate;

import com.atodium.iridynamics.api.util.math.IntFraction;
import com.atodium.iridynamics.api.util.math.MathUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class Handle implements IRotateNode {
    public static final Serializer SERIALIZER = new Serializer();

    private final Direction direction;
    private double torque;
    private double angle, angularVelocity;

    public Handle(Direction direction) {
        this.direction = direction;
    }

    public void setTorque(double torque) {
        this.torque = torque;
    }

    @Override
    public Serializer serializer() {
        return SERIALIZER;
    }

    @Override
    public boolean isConnectable(Direction direction) {
        return this.direction == direction;
    }

    @Override
    public boolean isRelated(Direction from, Direction to) {
        return false;
    }

    @Override
    public IntFraction getRelation(Direction from, Direction to) {
        return null;
    }

    @Override
    public double getAngle(Direction direction) {
        return direction == this.direction ? this.angle : 0.0;
    }

    @Override
    public double getAngularVelocity(Direction direction) {
        return direction == this.direction ? this.angularVelocity : 0.0;
    }

    @Override
    public void setAngle(Direction direction, double angle) {
        if (direction == this.direction) this.angle = MathUtil.castAngle(angle);
    }

    @Override
    public void setAngularVelocity(Direction direction, double angularVelocity) {
        if (direction == this.direction) this.angularVelocity = angularVelocity;
    }

    @Override
    public double getInertia(Direction direction) {
        if (this.isConnectable(direction)) return 20.0;
        return 0.0;
    }

    @Override
    public double getTorque(Direction direction) {
        return this.torque;
    }

    @Override
    public double getFriction(Direction direction) {
        if (this.isConnectable(direction)) return 0.1;
        return 0.0;
    }

    @Override
    public double maxAngularVelocity(Direction direction) {
        return 300.0;
    }

    public static class Serializer implements IRotateNode.Serializer {
        @Override
        public IRotateNode deserialize(CompoundTag tag) {
            return new Handle(Direction.from3DDataValue(tag.getInt("direction")));
        }

        @Override
        public CompoundTag serialize(IRotateNode node) {
            CompoundTag tag = new CompoundTag();
            Handle handle = (Handle) node;
            tag.putInt("direction", handle.direction.get3DDataValue());
            return tag;
        }

        @Override
        public CompoundTag writeSyncTag(IRotateNode node) {
            CompoundTag tag = new CompoundTag();
            Handle handle = (Handle) node;
            tag.putDouble("angle", handle.angle);
            tag.putDouble("angularVelocity", handle.angularVelocity);
            tag.putDouble("torque", handle.torque);
            return tag;
        }

        @Override
        public void readSyncTag(IRotateNode node, CompoundTag tag) {
            Handle handle = (Handle) node;
            handle.angle = tag.getDouble("angle");
            handle.angularVelocity = tag.getDouble("angularVelocity");
            handle.torque = tag.getDouble("torque");
        }
    }
}