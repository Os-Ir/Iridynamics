package com.atodium.iridynamics.api.recipe;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.util.data.SimpleJsonLoader;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.OnDatapackSyncEvent;

import java.util.Map;

public class JsonRecipeLoader extends SimpleJsonLoader {
    public static final JsonRecipeLoader INSTANCE = new JsonRecipeLoader();
    public static final String ROOT = "iridynamics_recipe";

    public JsonRecipeLoader() {
        super(ROOT);
    }

    public void onDatapackSync(OnDatapackSyncEvent event) {

    }

    @Override
    public void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            Iridynamics.LOGGER.debug("Applying Json Recipe {}", entry.getKey());
        }
    }
}