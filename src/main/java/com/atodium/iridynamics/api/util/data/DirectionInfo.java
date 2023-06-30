package com.atodium.iridynamics.api.util.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;

public record DirectionInfo(BlockPos pos, Direction direction) {
    public ChunkPos chunk() {
        return new ChunkPos(this.pos);
    }

    public DirectionInfo relative() {
        return new DirectionInfo(this.pos.relative(this.direction), this.direction.getOpposite());
    }

    public Direction opposite() {
        return this.direction.getOpposite();
    }

    public DirectionInfo change(Direction direction) {
        if (this.direction == direction) return this;
        return new DirectionInfo(this.pos, direction);
    }
}