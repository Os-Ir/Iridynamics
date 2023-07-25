package com.atodium.iridynamics.api.heat.liquid;

import com.atodium.iridynamics.Iridynamics;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class LiquidContainerSavedData extends SavedData {
    public static final String ID = Iridynamics.MODID + "_liquid_container";

    private final Object2IntMap<BlockPos> liquidContainerCount;

    public LiquidContainerSavedData() {
        this.liquidContainerCount = new Object2IntOpenHashMap<>();
    }

    public static LiquidContainerSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(LiquidContainerSavedData::load, LiquidContainerSavedData::new, ID);
    }

    private static LiquidContainerSavedData load(CompoundTag tag) {
        LiquidContainerSavedData data = new LiquidContainerSavedData();
        ListTag liquidContainerCountTag = tag.getList("liquidContainerCount", Tag.TAG_COMPOUND);
        for (int i = 0; i < liquidContainerCountTag.size(); i++) {
            CompoundTag countTag = liquidContainerCountTag.getCompound(i);
            data.addLiquidContainer(new BlockPos(countTag.getInt("x"), countTag.getInt("y"), countTag.getInt("z")), countTag.getInt("count"));
        }
        return data;
    }

    public void addLiquidContainer(BlockPos pos) {
        this.addLiquidContainer(pos, 1);
    }

    public void addLiquidContainer(BlockPos pos, int count) {
        if (this.liquidContainerCount.containsKey(pos))
            this.liquidContainerCount.put(pos, this.liquidContainerCount.getInt(pos) + count);
        else this.liquidContainerCount.put(pos, count);
        this.setDirty();
    }

    public void removeLiquidContainer(BlockPos pos) {
        this.removeLiquidContainer(pos, 1);
    }

    public void removeLiquidContainer(BlockPos pos, int count) {
        int remain = this.liquidContainerCount.getInt(pos) - count;
        if (remain <= 0) this.liquidContainerCount.removeInt(pos);
        else this.liquidContainerCount.put(pos, remain);
        this.setDirty();
    }

    public void removeAllLiquidContainer(BlockPos pos) {
        this.liquidContainerCount.removeInt(pos);
        this.setDirty();
    }

    public boolean hasLiquidContainer(BlockPos pos) {
        return this.liquidContainerCount.containsKey(pos);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag liquidContainerCountTag = new ListTag();
        for (Object2IntMap.Entry<BlockPos> entry : this.liquidContainerCount.object2IntEntrySet()) {
            CompoundTag countTag = new CompoundTag();
            BlockPos pos = entry.getKey();
            countTag.putInt("x", pos.getX());
            countTag.putInt("y", pos.getY());
            countTag.putInt("z", pos.getZ());
            countTag.putInt("count", entry.getIntValue());
            liquidContainerCountTag.add(countTag);
        }
        tag.put("liquidContainerCount", liquidContainerCountTag);
        return tag;
    }
}