package com.atodium.iridynamics.api.module.rotate;

import com.atodium.iridynamics.api.blockEntity.IRotateNode;
import com.atodium.iridynamics.api.util.math.IntFraction;
import com.atodium.iridynamics.api.util.math.MathUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class Gearbox implements IRotateNode {
    public static final Serializer SERIALIZER = new Serializer();

    private final Direction directionA, directionB;
    private final int gearA, gearB;
    private final IntFraction ab, ba;
    private double angle, angularVelocity;

    public Gearbox(Direction directionA, Direction directionB, int gearA, int gearB) {
        this.directionA = directionA;
        this.directionB = directionB;
        this.gearA = gearA;
        this.gearB = gearB;
        this.ab = new IntFraction(-this.gearA, this.gearB);
        this.ba = new IntFraction(-this.gearB, this.gearA);
    }

    @Override
    public Serializer serializer() {
        return null;
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
    public double getTorque(Direction direction) {
        return 0.0;
    }

    @Override
    public double getFriction(Direction direction) {
        return 0.0;
    }

    public static class Serializer implements IRotateNode.Serializer {
        @Override
        public IRotateNode deserialize(CompoundTag tag) {
            return new Gearbox(Direction.from3DDataValue(tag.getInt("directionA")), Direction.from3DDataValue(tag.getInt("directionB")), tag.getInt("gearA"), tag.getInt("gearB"));
        }

        @Override
        public CompoundTag serialize(IRotateNode node) {
            CompoundTag tag = new CompoundTag();
            Gearbox gearbox = (Gearbox) node;
            tag.putInt("directionA", gearbox.directionA.get3DDataValue());
            tag.putInt("directionB", gearbox.directionB.get3DDataValue());
            tag.putInt("gearA", gearbox.gearA);
            tag.putInt("gearB", gearbox.gearB);
            return tag;
        }
    }
}