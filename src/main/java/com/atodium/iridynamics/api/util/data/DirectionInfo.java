package com.atodium.iridynamics.api.util.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;

public record DirectionInfo(BlockPos pos, Direction direction) {
    public static DirectionInfo load(CompoundTag tag) {
        return new DirectionInfo(new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")), Direction.from3DDataValue(tag.getInt("direction")));
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", this.pos.getX());
        tag.putInt("y", this.pos.getY());
        tag.putInt("z", this.pos.getZ());
        tag.putInt("direction", this.direction.get3DDataValue());
        return tag;
    }

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