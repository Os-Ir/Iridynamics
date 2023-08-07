package com.atodium.iridynamics.api.rotate;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.util.math.IntFraction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IRotateNode extends ITickable {
    @Override
    default void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) RotateModule.tryTick((ServerLevel) level, pos);
        this.nodeTick(level, pos, state);
    }

    void nodeTick(Level level, BlockPos pos, BlockState state);

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