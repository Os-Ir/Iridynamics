package com.atodium.iridynamics.client.model.tool;

import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.tool.MaterialToolBase;
import com.atodium.iridynamics.api.tool.MaterialToolItem;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.math.Transformation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.IModelConfiguration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class MaterialToolOverride extends ItemOverrides {
    private final IModelConfiguration owner;
    private final Transformation transform;
    private final MaterialToolBase tool;
    private final int layers;
    private final Cache<Map<Integer, Pair<String, MaterialBase>>, BakedModel> cache;

    public MaterialToolOverride(IModelConfiguration owner, Transformation transform, MaterialToolBase tool, int layers) {
        this.owner = owner;
        this.transform = transform;
        this.tool = tool;
        this.layers = layers;
        this.cache = CacheBuilder.newBuilder().maximumSize(200).build();
    }

    private BakedModel bake(Map<Integer, Pair<String, MaterialBase>> materials) {
        return MaterialToolModel.bakeMaterialToolModel(this.owner, ForgeModelBakery.defaultTextureGetter(), this.transform, ItemOverrides.EMPTY, this.tool, this.layers, materials);
    }

    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
        Map<Integer, Pair<String, MaterialBase>> materials = MaterialToolItem.getToolAllMaterial(stack);
        if (materials.isEmpty()) return originalModel;
        return this.cache.asMap().computeIfAbsent(materials, this::bake);
    }
}