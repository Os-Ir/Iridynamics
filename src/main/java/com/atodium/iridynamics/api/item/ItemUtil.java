package com.atodium.iridynamics.api.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ItemUtil {
    public static boolean spawnItem(Level level, double x, double y, double z, ItemStack stack) {
        return level.addFreshEntity(new ItemEntity(level, x, y, z, stack));
    }

    public static boolean spawnItem(Level level, Vec3 vec, ItemStack stack) {
        return spawnItem(level, vec.x, vec.y, vec.z, stack);
    }

    public static boolean spawnItem(Level level, BlockPos pos, ItemStack stack) {
        return spawnItem(level, pos.getX(), pos.getY(), pos.getZ(), stack);
    }
}