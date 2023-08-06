package com.atodium.iridynamics.client.model.material;

import com.atodium.iridynamics.api.material.MaterialRenderInfo;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.client.model.ColoredItemLayerModel;
import com.atodium.iridynamics.client.model.DynamicTextureLoader;
import com.atodium.iridynamics.client.model.TexturePixelFlag;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class MaterialModel implements IModelGeometry<MaterialModel> {
    private final SolidShape shape;
    private final MaterialBase material;

    public MaterialModel(SolidShape shape, MaterialBase material) {
        this.shape = shape;
        this.material = material;
    }

    public static BakedModel bakeMaterialModel(IModelConfiguration owner, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transform, ItemOverrides overrides, SolidShape shape, MaterialBase material) {
        TextureAtlasSprite sprite;
        ImmutableList<BakedQuad> quads;
        if (material == null) {
            sprite = spriteGetter.apply(owner.resolveTexture("texture"));
            quads = ImmutableList.of();
        } else {
            MaterialRenderInfo renderInfo = material.getRenderInfo();
            Material texture = renderInfo.specialTextureMaterial(shape);
            if (texture == null) {
                sprite = spriteGetter.apply(owner.resolveTexture("texture"));
                ImmutableList.Builder<BakedQuad> quadBuilder = ImmutableList.builder();
                int layers = 1;
                while (owner.isTexturePresent("layer" + layers)) layers++;
                if (layers == 1) {
                    quadBuilder.addAll(ColoredItemLayerModel.getQuadsForSprite(0, sprite, transform, renderInfo.argb(), renderInfo.light()));
                } else {
                    TexturePixelFlag pixels = new TexturePixelFlag(16, 16);
                    quadBuilder.addAll(ColoredItemLayerModel.getQuadsForSprite(0, sprite, transform, renderInfo.argb(), renderInfo.light(), pixels));
                    for (int i = 1; i < layers; i++)
                        quadBuilder.addAll(ColoredItemLayerModel.getQuadsForSprite(i, spriteGetter.apply(owner.resolveTexture("layer" + i)), transform, 0xffffffff, 0, pixels));
                }
                quads = quadBuilder.build();
            } else {
                sprite = spriteGetter.apply(texture);
                quads = ColoredItemLayerModel.getQuadsForSprite(0, sprite, transform, renderInfo.argb(), renderInfo.light());
            }
        }
        return new BakedItemModel(quads, sprite, Maps.immutableEnumMap(PerspectiveMapWrapper.getTransforms(owner.getCombinedTransform())), overrides, true, owner.isSideLit());
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides originalOverrides, ResourceLocation modelLocation) {
        return bakeMaterialModel(owner, spriteGetter, Transformation.identity(), this.material == null ? new MaterialOverride(owner, Transformation.identity(), this.shape) : ItemOverrides.EMPTY, this.shape, this.material);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        Collection<Material> textures = Sets.newHashSet();
        textures.add(owner.resolveTexture("texture"));
        for (int i = 1; owner.isTexturePresent("layer" + i); i++) textures.add(owner.resolveTexture("layer" + i));
        Predicate<Material> adder = DynamicTextureLoader.INSTANCE.getTextureAdder(textures);
        if (this.material == null) {
            MaterialBase.REGISTRY.values().forEach((material) -> {
                if (this.shape.generateMaterial(material))
                    material.getRenderInfo().registerSpecialTexture(this.shape, adder);
            });
        } else this.material.getRenderInfo().registerSpecialTexture(this.shape, adder);
        return textures;
    }
}