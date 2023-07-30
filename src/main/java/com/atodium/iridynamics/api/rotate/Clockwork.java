package com.atodium.iridynamics.api.rotate;

import com.atodium.iridynamics.api.util.math.IntFraction;
import com.atodium.iridynamics.api.util.math.MathUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class Clockwork implements IRotateNode {
    public static final Serializer SERIALIZER = new Serializer();

    private final Direction direction;
    private double angle, angularVelocity;

    public Clockwork(Direction direction) {
        this.direction = direction;
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
        return 30.0;
    }

    @Override
    public double getTorque(Direction direction) {
        return 0;
    }

    @Override
    public double getFriction(Direction direction) {
        if (this.isConnectable(direction)) return 1.0;
        return 0.0;
    }

    @Override
    public double maxAngularVelocity(Direction direction) {
        return 300.0;
    }

    public static class Serializer implements IRotateNode.Serializer<Clockwork> {
        @Override
        public Clockwork deserialize(CompoundTag tag) {
            return new Clockwork(Direction.from3DDataValue(tag.getInt("direction")));
        }

        @Override
        public CompoundTag serialize(Clockwork clockwork) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("direction", clockwork.direction.get3DDataValue());
            return tag;
        }

        @Override
        public CompoundTag writeSyncTag(Clockwork clockwork) {
            CompoundTag tag = new CompoundTag();
            tag.putDouble("angle", clockwork.angle);
            tag.putDouble("angularVelocity", clockwork.angularVelocity);
            return tag;
        }

        @Override
        public void readSyncTag(Clockwork clockwork, CompoundTag tag) {
            clockwork.angle = tag.getDouble("angle");
            clockwork.angularVelocity = tag.getDouble("angularVelocity");
        }
    }
}