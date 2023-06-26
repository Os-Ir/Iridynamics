package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.Iridynamics;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PotteryCapability implements IPottery, ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation KEY = Iridynamics.rl("pottery");
    public static final Capability<IPottery> POTTERY = CapabilityManager.get(new CapabilityToken<>() {
    });

    private long flag;
    private final int[] carved;

    public PotteryCapability() {
        this.carved = new int[12];
    }

    @Override
    public boolean processed() {
        return this.flag != 0;
    }

    private void updateFlag() {
        this.flag = System.currentTimeMillis();
    }

    @Override
    public boolean carve(int h, int carve) {
        if (!this.validateHeight(h)) return false;
        this.carved[h] = Math.min(this.carved[h] + carve, 50);
        this.updateFlag();
        return true;
    }

    @Override
    public int getCarved(int h) {
        if (this.validateHeight(h)) return this.carved[h];
        return 0;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == POTTERY) return LazyOptional.of(() -> this).cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag carvedTag = new ListTag();
        for (int i = 0; i < 12; i++) carvedTag.add(i, IntTag.valueOf(this.carved[i]));
        tag.put("carved", carvedTag);
        tag.putLong("flag", this.flag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ListTag carvedTag = tag.getList("carved", Tag.TAG_INT);
        for (int i = 0; i < 12; i++) this.carved[i] = carvedTag.getInt(i);
        this.flag = tag.getLong("flag");
    }
}