package com.atodium.iridynamics.api.blockEntity;

import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import com.atodium.iridynamics.api.util.math.IntFraction;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface IRotateNode {
    Serializer serializer();

    boolean isConnectable(Direction direction);

    boolean isRelated(Direction from, Direction to);

    IntFraction getRelation(Direction from, Direction to);

    double getAngle(Direction direction);

    double getAngularVelocity(Direction direction);

    void setAngle(Direction direction, double angle);

    void setAngularVelocity(Direction direction, double angularVelocity);

    double getTorque(Direction direction);

    double getFriction(Direction direction);

    interface Serializer {
        IRotateNode deserialize(CompoundTag tag);

        CompoundTag serialize(IRotateNode node);
    }
}