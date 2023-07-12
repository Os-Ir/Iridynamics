package com.atodium.iridynamics.api.util.data;

import com.google.gson.Gson;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DataUtil {
    public static final Gson GSON = new Gson();
    public static final Direction[] DIRECTIONS = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    public static final Direction[] HORIZONTALS = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    @SuppressWarnings("deprecation")
    public static String writeItemToString(Item item) {
        return Registry.ITEM.getKey(item).toString();
    }

    public static Item readItemFromString(String string) {
        return readItemFromLocation(new ResourceLocation(string));
    }

    @SuppressWarnings("deprecation")
    public static Item readItemFromLocation(ResourceLocation location) {
        return Registry.ITEM.get(location);
    }

    public static int height(Object[][] grid) {
        return grid.length;
    }

    public static int width(Object[][] grid) {
        return grid[0].length;
    }

    public static <T> void align(T[][] origin, T[][] align) {
        align(origin, align, Objects::nonNull, null);
    }

    public static <T> void align(T[][] origin, T[][] align, Predicate<T> nonNull, T nullValue) {
        int height = height(origin), width = width(origin);
        if (height == 0 || width == 0)
            throw new IllegalArgumentException("Can not align a grid with non positive side length");
        int moveX = 0, moveY = 0;
        outer:
        for (int x = 0; x < width; x++)
            for (T[] ts : origin)
                if (nonNull.test(ts[x])) {
                    moveX = x;
                    break outer;
                }
        outer:
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                if (nonNull.test(origin[y][x])) {
                    moveY = y;
                    break outer;
                }
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                if (x + moveX >= width || y + moveY >= height) align[y][x] = nullValue;
                else align[y][x] = origin[y + moveY][x + moveX];
    }

    public static void updateAllItems(Player player, Consumer<ItemStack> consumer) {
        Inventory inventory = player.getInventory();
        inventory.items.forEach(consumer);
        inventory.armor.forEach(consumer);
        inventory.offhand.forEach(consumer);
    }
}