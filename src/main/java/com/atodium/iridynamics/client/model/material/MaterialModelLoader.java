package com.atodium.iridynamics.client.model.material;

import com.atodium.iridynamics.api.material.ModSolidShapes;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.IModelLoader;

public class MaterialModelLoader implements IModelLoader<MaterialModel> {
    public static final MaterialModelLoader INSTANCE = new MaterialModelLoader();

    @Override
    public MaterialModel read(JsonDeserializationContext context, JsonObject obj) {
        SolidShape shape = null;
        MaterialBase material = null;
        if (obj.has("shape")) {
            shape = SolidShape.getShapeByName(GsonHelper.getAsString(obj, "shape"));
        }
        if (shape == null) {
            shape = ModSolidShapes.INGOT;
        }
        if (obj.has("material")) {
            material = MaterialBase.getMaterialByName(GsonHelper.getAsString(obj, "material"));
        }
        return new MaterialModel(shape, material);
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {

    }
}