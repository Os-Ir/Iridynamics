package com.atodium.iridynamics.api.research;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.util.data.SimpleJsonLoader;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.OnDatapackSyncEvent;

import java.util.Map;

public class ResearchNodeLoader extends SimpleJsonLoader {
    public static final ResearchNodeLoader INSTANCE = new ResearchNodeLoader();
    public static final String ROOT = "iridynamics_research";

    public ResearchNodeLoader() {
        super(ROOT);
    }

    @Override
    public void onDatapackSync(OnDatapackSyncEvent event) {

    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
        Iridynamics.LOGGER.info("Applying research node infos");
        ResearchModule.NODES.clear();
        Map<ResearchNode, Object2FloatMap<String>> correlationsToCache = Maps.newHashMap();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            JsonObject json = entry.getValue().getAsJsonObject();
            ResearchNodeType type = ResearchNodeType.getTypeByName(json.get("type").getAsString());
            ResearchNode node = new ResearchNode(type, json.get("name").getAsString(), type == ResearchNodeType.ROOT ? 0.0f : json.get("min_unlock_coefficient").getAsFloat());
            ResearchModule.registerResearchNode(node);
            Object2FloatMap<String> correlationsTo = new Object2FloatOpenHashMap<>();
            correlationsToCache.put(node, correlationsTo);
            JsonArray correlationsToJson = json.getAsJsonArray("correlations_to");
            correlationsToJson.forEach((element) -> {
                JsonObject correlationJson = element.getAsJsonObject();
                correlationsTo.put(correlationJson.get("name").getAsString(), correlationJson.get("correlation").getAsFloat());
            });
        }
        for (Map.Entry<ResearchNode, Object2FloatMap<String>> entry : correlationsToCache.entrySet()) {
            ResearchNode from = entry.getKey();
            entry.getValue().forEach((nameTo, correlation) -> {
                ResearchNode to = ResearchModule.getResearchNode(nameTo);
                from.putCorrelationTo(to, correlation);
                to.putCorrelationFrom(from, correlation);
            });
        }
    }
}