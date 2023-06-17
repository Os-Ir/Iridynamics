package com.atodium.iridynamics.client.model.tool;

import com.atodium.iridynamics.api.material.MaterialRenderInfo;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.tool.MaterialToolBase;
import com.atodium.iridynamics.api.tool.ToolRenderInfo;
import com.atodium.iridynamics.client.model.ColoredItemLayerModel;
import com.atodium.iridynamics.client.model.DynamicTextureLoader;
import com.atodium.iridynamics.client.model.TexturePixelFlag;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import org.apache.commons.compress.utils.Lists;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class MaterialToolModel implements IModelGeometry<MaterialToolModel> {
    private final MaterialToolBase tool;
    private final int layers;
    private final Map<Integer, org.apache.commons.lang3.tuple.Pair<String, MaterialBase>> materials;

    public MaterialToolModel(MaterialToolBase tool, Map<Integer, org.apache.commons.lang3.tuple.Pair<String, MaterialBase>> materials) {
        this.tool = tool;
        this.layers = tool.getDefaultMaterial().size();
        this.materials = materials;
    }

    public static BakedModel bakeMaterialToolModel(IModelConfiguration owner, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transform, ItemOverrides overrides, MaterialToolBase tool, int layers, Map<Integer, org.apache.commons.lang3.tuple.Pair<String, MaterialBase>> materials) {
        ImmutableMap<ItemTransforms.TransformType, Transformation> transformMap = PerspectiveMapWrapper.getTransforms(owner.getCombinedTransform());
        TextureAtlasSprite particle = spriteGetter.apply(owner.resolveTexture("layer0"));
        ImmutableList<BakedQuad> quads;
        List<BakedQuad> tempQuads = Lists.newArrayList();
        TexturePixelFlag pixelFlag = new TexturePixelFlag(16, 16);
        if (materials == null) for (int i = 0; i < layers; i++)
            tempQuads.addAll(ColoredItemLayerModel.getQuadsForSprite(i, spriteGetter.apply(owner.resolveTexture("layer" + i)), transform, 0xffffffff, 0, pixelFlag));
        else for (int i = 0; i < layers; i++) {
            ToolRenderInfo renderInfo = tool.getRenderInfo();
            MaterialBase material = materials.get(i).getRight();
            MaterialRenderInfo materialRenderInfo = material.getRenderInfo();
            Material specialTexture = renderInfo.getSpecialTextureMaterial(i, material);
            TextureAtlasSprite sprite;
            if (specialTexture == null) sprite = spriteGetter.apply(owner.resolveTexture("layer" + i));
            else sprite = spriteGetter.apply(specialTexture);
            tempQuads.addAll(ColoredItemLayerModel.getQuadsForSprite(i, sprite, transform, materialRenderInfo.RGBAColor(), materialRenderInfo.light(), pixelFlag));
        }
        quads = ImmutableList.copyOf(tempQuads);
        return new BakedItemModel(quads, particle, Maps.immutableEnumMap(transformMap), overrides, true, owner.isSideLit());
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides originalOverrides, ResourceLocation modelLocation) {
        ItemOverrides overrides = ItemOverrides.EMPTY;
        if (this.materials == null) {
            overrides = new MaterialToolOverride(owner, Transformation.identity(), this.tool, this.layers);
        }
        return bakeMaterialToolModel(owner, spriteGetter, Transformation.identity(), overrides, this.tool, this.layers, this.materials);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        Collection<Material> textures = Sets.newHashSet();
        for (int i = 0; owner.isTexturePresent("layer" + i); i++) textures.add(owner.resolveTexture("layer" + i));
        Predicate<Material> adder = DynamicTextureLoader.INSTANCE.getTextureAdder(textures);
        if (this.materials == null) {
            MaterialBase.REGISTRY.values().forEach((material) -> {
                for (int i = 0; i < this.layers; i++)
                    if (this.tool.validateMaterial(i, material))
                        this.tool.getRenderInfo().registerSpecialTexture(material, adder);
            });
        } else {
            this.materials.forEach((index, pair) -> {
                MaterialBase material = pair.getRight();
                if (this.tool.validateMaterial(index, material))
                    this.tool.getRenderInfo().registerSpecialTexture(material, adder);
            });
        }
        return textures;
    }
}