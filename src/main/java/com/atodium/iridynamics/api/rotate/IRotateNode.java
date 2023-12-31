package com.atodium.iridynamics.api.rotate;

import com.atodium.iridynamics.api.util.math.IntFraction;
import net.minecraft.core.Direction;

public interface IRotateNode {
    boolean isConnectable(Direction direction);

    boolean isRelated(Direction from, Direction to);

    IntFraction getRelation(Direction from, Direction to);

    double getAngle(Direction direction);

    double getAngularVelocity(Direction direction);

    void setAngle(Direction direction, double angle);

    void setAngularVelocity(Direction direction, double angularVelocity);

    double getInertia(Direction direction);

    double getTorque(Direction direction);

    double getFriction(Direction direction);

    double maxAngularVelocity(Direction direction);
}