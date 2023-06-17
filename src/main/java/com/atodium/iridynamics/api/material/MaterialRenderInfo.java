package com.atodium.iridynamics.api.material;

import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.google.common.collect.Maps;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.util.Map;
import java.util.function.Predicate;

public class MaterialRenderInfo {
    private MaterialBase material;
    private final Map<SolidShape, ResourceLocation> specialTexture;
    private final int alpha, color, light;

    public MaterialRenderInfo() {
        this(0xffffff);
    }

    public MaterialRenderInfo(int color) {
        this(Maps.newHashMap(), 0xff, color, 0);
    }

    public MaterialRenderInfo(int alpha, int color, int light) {
        this(Maps.newHashMap(), alpha, color, light);
    }

    public MaterialRenderInfo(Map<SolidShape, ResourceLocation> specialTexture, int alpha, int color, int light) {
        this.specialTexture = specialTexture;
        this.alpha = 0xff & alpha;
        this.color = 0xffffff & color;
        this.light = Mth.clamp(light, 0, 15);
    }

    public static MaterialRenderInfo empty(MaterialBase material) {
        return new MaterialRenderInfo().setMaterial(material);
    }

    public MaterialRenderInfo putSpecialTexture(SolidShape shape, ResourceLocation location) {
        this.specialTexture.put(shape, location);
        return this;
    }

    public MaterialRenderInfo setMaterial(MaterialBase material) {
        this.material = material;
        return this;
    }

    public MaterialBase material() {
        return this.material;
    }

    public Map<SolidShape, ResourceLocation> allSpecialTextures() {
        return this.specialTexture;
    }

    public ResourceLocation specialTexture(SolidShape shape) {
        if (this.specialTexture.containsKey(shape)) return this.specialTexture.get(shape);
        return null;
    }

    public Material specialTextureMaterial(SolidShape shape) {
        if (this.specialTexture.containsKey(shape))
            return ModelLoaderRegistry.blockMaterial(this.specialTexture.get(shape));
        return null;
    }

    public void registerSpecialTexture(SolidShape shape, Predicate<Material> adder) {
        if (this.specialTexture.containsKey(shape))
            adder.test(ModelLoaderRegistry.blockMaterial(this.specialTexture.get(shape)));
    }

    public int alpha() {
        return this.alpha;
    }

    public int color() {
        return this.color;
    }

    public int RGBAColor() {
        return (this.alpha << 24) + this.color;
    }

    public int light() {
        return this.light;
    }
}