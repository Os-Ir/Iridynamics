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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CarvingCapability implements ICarving, ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation KEY = Iridynamics.rl("carving");
    public static final Capability<ICarving> CARVING = CapabilityManager.get(new CapabilityToken<>() {
    });

    private long flag;
    private final int originalThickness;
    private final int[][] thicknessMap;

    public CarvingCapability(int originalThickness) {
        this.originalThickness = originalThickness;
        this.thicknessMap = new int[12][12];
        for (int i = 0; i < 12; i++) for (int j = 0; j < 12; j++) this.thicknessMap[i][j] = originalThickness;
    }

    @Override
    public boolean processed() {
        return this.flag != 0;
    }

    private void updateFlag() {
        this.flag = System.currentTimeMillis();
    }

    @Override
    public int getOriginalThickness() {
        return this.originalThickness;
    }

    @Override
    public boolean carve(int x, int y) {
        if (!this.validatePos(x, y) || this.thicknessMap[x][y] == 0) return false;
        this.thicknessMap[x][y]--;
        this.updateFlag();
        return true;
    }

    @Override
    public int getThickness(int x, int y) {
        if (this.validatePos(x, y)) return this.thicknessMap[x][y];
        return 0;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CARVING) return LazyOptional.of(() -> this).cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag thicknessTag = new ListTag();
        for (int j = 0; j < 12; j++)
            for (int i = 0; i < 12; i++) thicknessTag.add(j * 12 + i, IntTag.valueOf(this.thicknessMap[i][j]));
        tag.put("thickness", thicknessTag);
        tag.putLong("flag", this.flag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ListTag thicknessTag = tag.getList("thickness", Tag.TAG_INT);
        for (int j = 0; j < 12; j++)
            for (int i = 0; i < 12; i++) this.thicknessMap[i][j] = thicknessTag.getInt(j * 12 + i);
        this.flag = tag.getLong("flag");
    }
}