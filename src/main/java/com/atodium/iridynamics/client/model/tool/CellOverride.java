package com.atodium.iridynamics.client.model.tool;

import com.atodium.iridynamics.api.material.type.FluidMaterial;
import com.atodium.iridynamics.common.item.CellItem;
import com.mojang.math.Transformation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.IModelConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CellOverride extends ItemOverrides {
    private final IModelConfiguration owner;
    private final Transformation transform;
    private final Map<FluidMaterial, BakedModel> cache;
    private BakedModel empty;

    public CellOverride(IModelConfiguration owner, Transformation transform) {
        this.owner = owner;
        this.transform = transform;
        this.cache = new ConcurrentHashMap<>();
    }

    private BakedModel bake(FluidMaterial material) {
        return CellModel.bakeMaterialModel(this.owner, ForgeModelBakery.defaultTextureGetter(), this.transform, ItemOverrides.EMPTY, material);
    }

    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
        if (this.empty == null) this.empty = bake(null);
        FluidMaterial material = CellItem.getCellItemMaterial(stack);
        return material == null ? this.empty : this.cache.computeIfAbsent(material, this::bake);
    }
}