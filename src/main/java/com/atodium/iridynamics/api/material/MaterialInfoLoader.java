package com.atodium.iridynamics.api.material;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.heat.MaterialHeatInfo;
import com.atodium.iridynamics.api.heat.SubMaterialHeatInfo;
import com.atodium.iridynamics.api.material.alloy.AlloyModule;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.util.data.MonotonicMap;
import com.atodium.iridynamics.api.util.data.SimpleJsonLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.OnDatapackSyncEvent;

import java.util.Map;

public class MaterialInfoLoader extends SimpleJsonLoader {
    public static final MaterialInfoLoader INSTANCE = new MaterialInfoLoader();
    public static final String ROOT = "iridynamics_material";

    public MaterialInfoLoader() {
        super(ROOT);
    }

    @Override
    public void onDatapackSync(OnDatapackSyncEvent event) {

    }

    @Override
    public void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
        Iridynamics.LOGGER.info("Applying material infos");
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            JsonObject json = entry.getValue().getAsJsonObject();
            MaterialBase material = MaterialBase.getMaterialByName(json.get("type").getAsString());
            Iridynamics.LOGGER.debug("Applying material info {} for Material {}", entry.getKey(), material.getName());
            if (json.has("tool_property")) {
                JsonObject toolPropertyJson = json.getAsJsonObject("tool_property");
                material.setToolPropertyInfo(new MaterialToolPropertyInfo(toolPropertyJson.get("durability").getAsInt(), toolPropertyJson.get("harvest_level").getAsInt(), toolPropertyJson.get("efficiency").getAsFloat()));
            }
            if (json.has("render")) {
                JsonObject renderJson = json.getAsJsonObject("render");
                MaterialRenderInfo renderInfo = new MaterialRenderInfo(renderJson.has("alpha") ? renderJson.get("alpha").getAsInt() : 0xff, renderJson.get("color").getAsInt(), renderJson.has("light") ? renderJson.get("light").getAsInt() : 0);
                if (renderJson.has("special_texture"))
                    renderJson.getAsJsonArray("special_texture").forEach((element) -> {
                        JsonObject obj = element.getAsJsonObject();
                        renderInfo.putSpecialTexture(SolidShape.getShapeByName(obj.get("shape").getAsString()), new ResourceLocation(obj.get("location").getAsString()));
                    });
                material.setRenderInfo(renderInfo);
            }
            if (json.has("physical")) {
                JsonObject physicalJson = json.getAsJsonObject("physical");
                double density = physicalJson.get("density").getAsDouble();
                double heatCapacity = physicalJson.get("heat_capacity").getAsDouble();
                material.setPhysicalInfo(new MaterialPhysicalInfo(density, physicalJson.get("thermal_conductivity").getAsDouble(), heatCapacity, physicalJson.has("calorific_value") ? physicalJson.get("calorific_value").getAsDouble() : 0.0));
                if (json.has("heat")) {
                    JsonObject heatJson = json.getAsJsonObject("heat");
                    String type = heatJson.get("type").getAsString();
                    double moleCapacity = heatCapacity * density / 9.0;
                    switch (type) {
                        case "simple_solid" ->
                                material.setHeatInfo(MaterialHeatInfo.getSimplified(SubMaterialHeatInfo.getSimplified(Phase.SOLID, moleCapacity)));
                        case "simple_liquid" ->
                                material.setHeatInfo(MaterialHeatInfo.getSimplified(SubMaterialHeatInfo.getSimplified(Phase.LIQUID, moleCapacity)));
                        case "simple_solid_liquid" ->
                                material.setHeatInfo(MaterialHeatInfo.getSimplified(SubMaterialHeatInfo.builder().putCapacity(Phase.SOLID, moleCapacity).putCapacity(Phase.LIQUID, moleCapacity).setCriticalPoints(MonotonicMap.<Phase>builder().addData(0.0, Phase.SOLID).addData(heatJson.get("melting_point").getAsDouble(), Phase.LIQUID).build()).build()));
                    }
                }
            }
            if (json.has("alloy")) {
                JsonArray alloyJson = json.getAsJsonArray("alloy");
                Object2IntMap<MaterialBase> composition = new Object2IntOpenHashMap<>();
                alloyJson.forEach((element) -> {
                    JsonObject obj = element.getAsJsonObject();
                    composition.put(MaterialBase.getMaterialByName(obj.get("material").getAsString()), obj.get("weight").getAsInt());
                });
                AlloyModule.registerAlloy(material, composition);
            }
        }
    }
}