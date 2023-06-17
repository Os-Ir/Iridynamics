package com.atodium.iridynamics.api.tool;

import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.google.common.collect.Maps;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.util.Map;
import java.util.function.Predicate;

public class ToolRenderInfo {
    private final IToolInfo tool;
    private final int layers;
    private final Map<Integer, Map<MaterialBase, ResourceLocation>> specialTexture;

    public ToolRenderInfo(IToolInfo tool) {
        this.tool = tool;
        this.layers = tool.getDefaultMaterial().size();
        this.specialTexture = Maps.newHashMap();
        for (int i = 0; i < this.layers; i++) this.specialTexture.put(i, Maps.newHashMap());
    }

    public ToolRenderInfo(IToolInfo tool, Map<Integer, Map<MaterialBase, ResourceLocation>> specialTexture) {
        this.tool = tool;
        this.layers = tool.getDefaultMaterial().size();
        this.specialTexture = specialTexture;
    }

    public Map<Integer, Map<MaterialBase, ResourceLocation>> getSpecialTextureMap() {
        return this.specialTexture;
    }

    public ResourceLocation getSpecialTexture(int layer, MaterialBase material) {
        if (layer < this.layers) {
            Map<MaterialBase, ResourceLocation> map = this.specialTexture.get(layer);
            if (map.containsKey(material)) return map.get(material);
        }
        return null;
    }

    public Material getSpecialTextureMaterial(int layer, MaterialBase material) {
        if (layer < this.layers) {
            Map<MaterialBase, ResourceLocation> map = this.specialTexture.get(layer);
            if (map.containsKey(material)) return ModelLoaderRegistry.blockMaterial(map.get(material));
        }
        return null;
    }

    public ToolRenderInfo putSpecialTexture(int layer, MaterialBase material, ResourceLocation location) {
        if (layer < this.layers) this.specialTexture.get(layer).put(material, location);
        return this;
    }

    public void registerSpecialTexture(MaterialBase material, Predicate<Material> adder) {
        for (int i = 0; i < this.layers; i++) {
            Map<MaterialBase, ResourceLocation> map = this.specialTexture.get(i);
            if (map.containsKey(material)) adder.test(ModelLoaderRegistry.blockMaterial(map.get(material)));
        }
    }
}