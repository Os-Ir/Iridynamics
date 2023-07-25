package com.atodium.iridynamics.api.rotate;

import com.atodium.iridynamics.api.util.math.IntFraction;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public interface IRotateNode {
    Serializer<?> serializer();

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

    interface Serializer<T extends IRotateNode> {
        T deserialize(CompoundTag tag);

        CompoundTag serialize(T node);

        CompoundTag writeSyncTag(T node);

        void readSyncTag(T node, CompoundTag tag);

        @SuppressWarnings("unchecked")
        default CompoundTag serializeRaw(IRotateNode node) {
            return this.serialize((T) node);
        }

        @SuppressWarnings("unchecked")
        default CompoundTag writeSyncTagRaw(IRotateNode node) {
            return this.writeSyncTag((T) node);
        }

        @SuppressWarnings("unchecked")
        default void readSyncTagRaw(IRotateNode node, CompoundTag tag) {
            this.readSyncTag((T) node, tag);
        }
    }
}