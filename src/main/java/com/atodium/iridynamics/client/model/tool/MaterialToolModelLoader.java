package com.atodium.iridynamics.client.model.tool;

import com.atodium.iridynamics.api.module.ToolModule;
import com.atodium.iridynamics.api.tool.MaterialToolBase;
import com.atodium.iridynamics.common.tool.ToolHammer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.IModelLoader;

public class MaterialToolModelLoader implements IModelLoader<MaterialToolModel> {
    public static final MaterialToolModelLoader INSTANCE = new MaterialToolModelLoader();

    @Override
    public MaterialToolModel read(JsonDeserializationContext context, JsonObject obj) {
        MaterialToolBase tool = null;
        if (obj.has("tool"))
            tool = (MaterialToolBase) ToolModule.TOOL_INFO.get(new ResourceLocation(GsonHelper.getAsString(obj, "tool")));
        if (tool == null) tool = ToolHammer.INSTANCE;
        return new MaterialToolModel(tool, null);
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {

    }
}