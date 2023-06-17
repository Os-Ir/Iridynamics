package com.atodium.iridynamics.api.util.data;

import com.google.gson.Gson;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class DataUtil {
    public static final Gson GSON = new Gson();

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
}