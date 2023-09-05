package com.atodium.iridynamics.api.model;

import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;

public class SpecialJsonModel {
    private static final UnorderedRegistry<ResourceLocation, SpecialJsonModel> REGISTRY = new UnorderedRegistry<>();
    private static boolean closed;

    private final ResourceLocation location;
    private BakedModel model;

    public SpecialJsonModel(ResourceLocation location) {
        if (closed) throw new IllegalStateException("SpecialJsonModel should be registered before ModelRegistryEvent");
        this.location = location;
        REGISTRY.register(location, this);
    }

    public static void onModelRegistry(ModelRegistryEvent event) {
        REGISTRY.keySet().forEach(ForgeModelBakery::addSpecialModel);
        closed = true;
    }

    public static void onModelBake(ModelBakeEvent event) {
        REGISTRY.entrySet().forEach((entry) -> entry.getValue().model = event.getModelRegistry().get(entry.getKey()));
    }

    public static SpecialJsonModel getJsonModel(ResourceLocation location) {
        return REGISTRY.get(location);
    }

    public static TransformableModelVertexList createTransformableModel(ResourceLocation location) {
        if (!REGISTRY.containsKey(location)) return null;
        return ModelUtil.createBakedModelVertexData(REGISTRY.get(location).bakedModel());
    }

    public static TransformableModelVertexList createTransformableModel(ResourceLocation location, PoseStack transform) {
        if (!REGISTRY.containsKey(location)) return null;
        return ModelUtil.createBakedModelVertexData(REGISTRY.get(location).bakedModel(), transform);
    }

    public ResourceLocation location() {
        return this.location;
    }

    public BakedModel bakedModel() {
        return this.model;
    }
}