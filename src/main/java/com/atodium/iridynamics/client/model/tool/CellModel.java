package com.atodium.iridynamics.client.model.tool;

import com.atodium.iridynamics.api.material.MaterialRenderInfo;
import com.atodium.iridynamics.api.material.type.FluidMaterial;
import com.atodium.iridynamics.client.model.ColoredItemLayerModel;
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

public class CellModel implements IModelGeometry<CellModel> {
    private final FluidMaterial material;

    public CellModel(FluidMaterial material) {
        this.material = material;
    }

    public static BakedModel bakeMaterialModel(IModelConfiguration owner, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transform, ItemOverrides overrides, FluidMaterial material) {
        TextureAtlasSprite spriteCell = spriteGetter.apply(owner.resolveTexture("texture0"));
        ImmutableList<BakedQuad> quads;
        if (material == null) quads = ColoredItemLayerModel.getQuadsForSprite(0, spriteCell, transform, 0xffffffff, 0);
        else {
            ImmutableList.Builder<BakedQuad> quadBuilder = ImmutableList.builder();
            quadBuilder.addAll(ColoredItemLayerModel.getQuadsForSprite(0, spriteCell, transform, 0xffffffff, 0));
            MaterialRenderInfo fluidRenderInfo = material.getRenderInfo();
            quadBuilder.addAll(ColoredItemLayerModel.getQuadsForSprite(1, spriteGetter.apply(owner.resolveTexture("texture1")), transform, fluidRenderInfo.argb(), fluidRenderInfo.light()));
            quads = quadBuilder.build();
        }
        return new BakedItemModel(quads, spriteCell, Maps.immutableEnumMap(PerspectiveMapWrapper.getTransforms(owner.getCombinedTransform())), overrides, true, owner.isSideLit());
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides originalOverrides, ResourceLocation modelLocation) {
        return bakeMaterialModel(owner, spriteGetter, Transformation.identity(), this.material == null ? new CellOverride(owner, Transformation.identity()) : ItemOverrides.EMPTY, this.material);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return Sets.newHashSet(owner.resolveTexture("texture0"), owner.resolveTexture("texture1"));
    }
}