package com.atodium.iridynamics.common.multiblock;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.multiblock.assembled.AssembledStructureInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Map;
import java.util.Set;

public class MultiblockCrucible extends AssembledStructureInfo<MultiblockCrucible.MultiblockCrucibleData> {
    public static final ResourceLocation ID = Iridynamics.rl("multiblock_crucible");
    public static final MultiblockCrucible INSTANCE = new MultiblockCrucible();

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public MultiblockCrucibleData createEmptyData() {
        return new MultiblockCrucibleData();
    }

    @Override
    public LazyOptional<MultiblockCrucibleData> validate(ServerLevel level, BlockPos checkPoint) {
        return null;
    }

    public static class MultiblockCrucibleData extends AssembledStructureInfo.StructureData {
        private int rx, ry, rz, sizeX, sizeY, sizeZ;

        public MultiblockCrucibleData() {

        }

        public MultiblockCrucibleData(int rx, int ry, int rz, int sizeX, int sizeY, int sizeZ) {
            this.rx = rx;
            this.ry = ry;
            this.rz = rz;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.sizeZ = sizeZ;
        }

        @Override
        public Set<BlockPos> allAssembledBlocks() {
            return null;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("sizeX", this.sizeX);
            tag.putInt("sizeY", this.sizeY);
            tag.putInt("sizeZ", this.sizeZ);
            tag.putInt("rx", this.rx);
            tag.putInt("ry", this.ry);
            tag.putInt("rz", this.rz);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            this.sizeX = tag.getInt("sizeX");
            this.sizeY = tag.getInt("sizeY");
            this.sizeZ = tag.getInt("sizeZ");
            this.rx = tag.getInt("rx");
            this.ry = tag.getInt("ry");
            this.rz = tag.getInt("rz");
        }
    }
}