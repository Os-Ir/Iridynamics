package com.atodium.iridynamics.client.model.tool;

import com.atodium.iridynamics.api.material.type.FluidMaterial;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.IModelLoader;

public class CellModelLoader implements IModelLoader<CellModel> {
    public static final CellModelLoader INSTANCE = new CellModelLoader();

    @Override
    public CellModel read(JsonDeserializationContext context, JsonObject obj) {
        FluidMaterial material = null;
        if (obj.has("material"))
            material = MaterialBase.getMaterialByName(GsonHelper.getAsString(obj, "material")) instanceof FluidMaterial fluidMaterial ? fluidMaterial : null;
        return new CellModel(material);
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {

    }
}