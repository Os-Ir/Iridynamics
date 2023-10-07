package com.atodium.iridynamics.common.multiblock;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.capability.LiquidContainerCapability;
import com.atodium.iridynamics.api.multiblock.MultiblockModule;
import com.atodium.iridynamics.api.multiblock.AssembledStructureInfo;
import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.blockEntity.factory.smelter.MultiblockCrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Collections;
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
        MultiblockModule.BlockSearchResult result = MultiblockModule.searchBlock(level, checkPoint, (state) -> state.is(ModBlocks.MULTIBLOCK_CRUCIBLE.get()));
        if (!result.isFilled() || !result.isCube()) return LazyOptional.empty();
        int size = result.size().getX();
        if (!MathUtil.between(size, 3, 7)) return LazyOptional.empty();
        return LazyOptional.of(() -> new MultiblockCrucibleData(result.root().getX(), result.root().getY(), result.root().getZ(), size));
    }

    public static class MultiblockCrucibleData extends AssembledStructureInfo.StructureData {
        private int rx, ry, rz, size;
        private Set<BlockPos> allAssembledBlocks;
        private LiquidContainerCapability liquid;

        public MultiblockCrucibleData() {
            this.allAssembledBlocks = Collections.emptySet();
            this.liquid = new LiquidContainerCapability(0);
        }

        public MultiblockCrucibleData(int rx, int ry, int rz, int size) {
            this.rx = rx;
            this.ry = ry;
            this.rz = rz;
            this.size = size;
            this.allAssembledBlocks = DataUtil.allBlockPosBetween(rx, ry, rz, rx + size - 1, ry + size - 1, rz + size - 1);
            this.liquid = new LiquidContainerCapability(MultiblockCrucibleBlockEntity.CAPACITY * this.size * this.size * this.size);
        }

        @Override
        public Set<BlockPos> allAssembledBlocks() {
            return this.allAssembledBlocks;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("size", this.size);
            tag.putInt("rx", this.rx);
            tag.putInt("ry", this.ry);
            tag.putInt("rz", this.rz);
            tag.put("liquid", this.liquid.serializeNBT());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            this.size = tag.getInt("size");
            this.rx = tag.getInt("rx");
            this.ry = tag.getInt("ry");
            this.rz = tag.getInt("rz");
            this.allAssembledBlocks = DataUtil.allBlockPosBetween(this.rx, this.ry, this.rz, this.rx + this.size - 1, this.ry + this.size - 1, this.rz + this.size - 1);
            this.liquid = new LiquidContainerCapability(MultiblockCrucibleBlockEntity.CAPACITY * this.size * this.size * this.size);
            this.liquid.deserializeNBT(tag.getCompound("liquid"));
        }
    }
}