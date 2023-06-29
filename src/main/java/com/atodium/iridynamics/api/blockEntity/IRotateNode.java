package com.atodium.iridynamics.api.blockEntity;

import com.atodium.iridynamics.api.util.math.BigFraction;
import com.atodium.iridynamics.api.util.math.IntFraction;
import net.minecraft.core.Direction;

public interface IRotateNode {
    boolean isConnectable(Direction direction);

    double getAngle(Direction direction);

    double getAngularVelocity(Direction direction);

    void setAngle(Direction direction, double angle);

    void setAngularVelocity(Direction direction, double angularVelocity);

    boolean isRelated(Direction from, Direction to);

    IntFraction getRelation(Direction from, Direction to);

    double getTorque(Direction direction);

    double getFriction(Direction direction);
}