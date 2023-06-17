package com.atodium.iridynamics.client.model;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class DynamicTextureLoader {
    public static final DynamicTextureLoader INSTANCE = new DynamicTextureLoader();

    private final Map<ResourceLocation, Boolean> textureExistence;
    private final Set<ResourceLocation> missingTextures;

    public DynamicTextureLoader() {
        this.textureExistence = Maps.newHashMap();
        this.missingTextures = Sets.newHashSet();
    }

    public void init(RegisterClientReloadListenersEvent event) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TextureStitchEvent.Post.class, (stitchEvent) -> this.clearCache());
    }

    public void clearCache() {
        this.textureExistence.clear();
        this.missingTextures.clear();
    }

    public boolean checkTextureExistence(ResourceManager manager, ResourceLocation location) {
        Boolean existence = this.textureExistence.get(location);
        if (existence == null) {
            existence = manager.hasResource(new ResourceLocation(location.getNamespace(), "textures/" + location.getPath() + ".png"));
            this.textureExistence.put(location, existence);
        }
        return existence;
    }

    public void addMissingTexture(ResourceLocation location) {
        if (!this.missingTextures.contains(location)) {
            this.missingTextures.add(location);
            Iridynamics.LOGGER.debug("Texture {} does not exist in the ResourceManager", location);
        }
    }

    public Predicate<Material> getTextureAdder(Collection<Material> textures) {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        return (texture) -> {
            ResourceLocation location = texture.texture();
            if (!RendererUtil.BLOCKS_ATLAS.equals(texture.atlasLocation()) || this.checkTextureExistence(manager, location)) {
                textures.add(texture);
                return true;
            }
            this.addMissingTexture(location);
            return false;
        };
    }
}