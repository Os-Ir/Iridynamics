package com.atodium.iridynamics.api.rotate;

import com.atodium.iridynamics.api.util.math.IntFraction;
import com.atodium.iridynamics.api.util.math.MathUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class Machine implements IRotateNode {
    public static final Serializer SERIALIZER = new Serializer();

    private final Direction direction;
    private double inertia, friction, angle, angularVelocity;

    public Machine(Direction direction) {
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
        return this.inertia;
    }

    @Override
    public double getTorque(Direction direction) {
        return 0.0;
    }

    @Override
    public double getFriction(Direction direction) {
        return this.friction;
    }

    @Override
    public double maxAngularVelocity(Direction direction) {
        return 300.0;
    }

    public double tickAngleChange() {
        return Math.abs(this.angularVelocity / 20.0);
    }

    public void setInertia(double inertia) {
        this.inertia = inertia;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    public static class Serializer implements IRotateNode.Serializer<Machine> {
        @Override
        public Machine deserialize(CompoundTag tag) {
            return new Machine(Direction.from3DDataValue(tag.getInt("direction")));
        }

        @Override
        public CompoundTag serialize(Machine machine) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("direction", machine.direction.get3DDataValue());
            return tag;
        }

        @Override
        public CompoundTag writeSyncTag(Machine machine) {
            CompoundTag tag = new CompoundTag();
            tag.putDouble("angle", machine.angle);
            tag.putDouble("angularVelocity", machine.angularVelocity);
            return tag;
        }

        @Override
        public void readSyncTag(Machine machine, CompoundTag tag) {
            machine.angle = tag.getDouble("angle");
            machine.angularVelocity = tag.getDouble("angularVelocity");
        }
    }
}