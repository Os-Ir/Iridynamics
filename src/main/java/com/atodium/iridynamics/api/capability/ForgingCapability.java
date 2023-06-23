package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.material.SolidShape;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ForgingCapability implements IForging, ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation KEY = Iridynamics.rl("forging");
    public static final Capability<IForging> FORGING = CapabilityManager.get(new CapabilityToken<>() {
    });

    private long flag;
    private final double[][] thicknessMap;

    public ForgingCapability(SolidShape shape) {
        this.thicknessMap = new double[7][7];
        int[] origin = shape.getForgeShape();
        for (int i = 0; i < 7; i++) for (int j = 0; j < 7; j++) this.thicknessMap[i][j] = origin[j * 7 + i];
    }

    public ForgingCapability(double[][] shape) {
        this.flag = 0;
        this.thicknessMap = new double[7][7];
        for (int i = 0; i < 7; i++) System.arraycopy(shape[i], 0, this.thicknessMap[i], 0, 7);
    }

    @Override
    public boolean processed() {
        return this.flag != 0;
    }

    private void updateFlag() {
        this.flag = System.currentTimeMillis();
    }

    @Override
    public boolean hit(int x, int y, double thickness, int range) {
        if (!this.validatePos(x, y)) return false;
        int minX = Mth.clamp(x - range, 0, 6);
        int maxX = Mth.clamp(x + range, 0, 6);
        int minY = Mth.clamp(y - range, 0, 6);
        int maxY = Mth.clamp(y + range, 0, 6);
        int area = (maxX - minX + 1) * (maxY - minY + 1);
        double average = 0.0;
        for (int i = minX; i <= maxX; i++) for (int j = minY; j <= maxY; j++) average += this.thicknessMap[i][j];
        if (average <= 0.0f) return false;
        average /= area;
        int minMX = Math.max(minX - 1, 0);
        int maxMX = Math.min(maxX + 1, 6);
        int minMY = Math.max(minY - 1, 0);
        int maxMY = Math.min(maxY + 1, 6);
        double remain = Math.max(average - thickness, 0.0);
        double moveArea = (maxMX - minMX + 1) * (maxMY - minMY + 1) - area;
        double move = Math.min(average, thickness) * area;
        if (moveArea > 0) {
            double moveAverage = move / moveArea;
            for (int i = minX; i <= maxX; i++) for (int j = minY; j <= maxY; j++) this.thicknessMap[i][j] = remain;
            for (int i = minMX; i <= maxMX; i++)
                for (int j = minMY; j <= maxMY; j++)
                    if ((i == minMX && i == minX - 1) || (i == maxMX && i == maxX + 1) || (j == minMY && j == minY - 1) || (j == maxMY && i == maxY + 1))
                        this.thicknessMap[i][j] += moveAverage;
        } else for (int i = minX; i <= maxX; i++) for (int j = minY; j <= maxY; j++) this.thicknessMap[i][j] = average;
        this.updateFlag();
        return true;
    }

    @Override
    public boolean carve(int x, int y) {
        if (!this.validatePos(x, y)) return false;
        int cast = (int) Math.floor(this.thicknessMap[x][y] / 0.25);
        if (this.thicknessMap[x][y] % 0.25 <= 0.001) this.thicknessMap[x][y] = (cast - 1) * 0.25;
        else this.thicknessMap[x][y] = cast * 0.25;
        this.updateFlag();
        return true;
    }

    @Override
    public double getThickness(int x, int y) {
        if (this.validatePos(x, y)) return this.thicknessMap[x][y];
        return 0.0;
    }

    @Override
    public double getMaxThickness() {
        double value = 0.0;
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 7; j++) if (this.thicknessMap[i][j] > value) value = this.thicknessMap[i][j];
        return value;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == FORGING) return LazyOptional.of(() -> this).cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag thicknessTag = new ListTag();
        for (int j = 0; j < 7; j++)
            for (int i = 0; i < 7; i++) thicknessTag.add(j * 7 + i, DoubleTag.valueOf(this.thicknessMap[i][j]));
        tag.put("thickness", thicknessTag);
        tag.putLong("flag", this.flag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ListTag thicknessTag = tag.getList("thickness", Tag.TAG_DOUBLE);
        for (int j = 0; j < 7; j++)
            for (int i = 0; i < 7; i++) this.thicknessMap[i][j] = thicknessTag.getDouble(j * 7 + i);
        this.flag = tag.getLong("flag");
    }
}