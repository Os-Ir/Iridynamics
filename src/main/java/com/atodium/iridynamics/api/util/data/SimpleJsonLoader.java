package com.atodium.iridynamics.api.util.data;

import com.atodium.iridynamics.Iridynamics;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.OnDatapackSyncEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class SimpleJsonLoader extends SimpleJsonResourceReloadListener {
    private final Gson internalGson;
    private final String internalRoot;

    public SimpleJsonLoader(String root) {
        this(root, new Gson());
    }

    public SimpleJsonLoader(String root, Gson gson) {
        super(gson, root);
        this.internalGson = gson;
        this.internalRoot = root;
    }

    public abstract void onDatapackSync(OnDatapackSyncEvent event);

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, JsonElement> map = Maps.newHashMap();
        int i = this.internalRoot.length() + 1;
        for (ResourceLocation location : manager.listResources(this.internalRoot, (name) -> name.endsWith(".json"))) {
            String path = location.getPath();
            ResourceLocation sub = new ResourceLocation(location.getNamespace(), path.substring(i, path.length() - 5));
            try {
                Resource resource = manager.getResource(location);
                try {
                    InputStream input = resource.getInputStream();
                    try {
                        Reader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
                        try {
                            JsonElement element = GsonHelper.fromJson(this.internalGson, reader, JsonElement.class);
                            if (element != null) {
                                JsonElement json = map.put(sub, element);
                                if (json != null)
                                    throw new IllegalStateException("Duplicate data file ignored with ID " + sub);
                            } else
                                Iridynamics.LOGGER.error("Couldn't load data file {} from {} as it's null or empty", sub, location);
                        } catch (Throwable e3) {
                            try {
                                reader.close();
                            } catch (Throwable e2) {
                                e3.addSuppressed(e2);
                            }
                            throw e3;
                        }
                        reader.close();
                    } catch (Throwable e4) {
                        try {
                            input.close();
                        } catch (Throwable throwable1) {
                            e4.addSuppressed(throwable1);
                        }
                        throw e4;
                    }
                    input.close();
                } catch (Throwable e5) {
                    try {
                        resource.close();
                    } catch (Throwable throwable) {
                        e5.addSuppressed(throwable);
                    }
                    throw e5;
                }
                resource.close();
            } catch (Throwable e1) {
                Iridynamics.LOGGER.error("Couldn't parse data file {} from {}", sub, location, e1);
            }
        }
        return map;
    }
}