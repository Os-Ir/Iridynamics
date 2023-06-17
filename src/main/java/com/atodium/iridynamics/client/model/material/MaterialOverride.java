package com.atodium.iridynamics.client.model.material;

import com.atodium.iridynamics.api.material.ModMaterials;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.common.item.MaterialItem;
import com.mojang.math.Transformation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.IModelConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MaterialOverride extends ItemOverrides {
    private final IModelConfiguration owner;
    private final Transformation transform;
    private final SolidShape shape;
    private final Map<MaterialBase, BakedModel> cache;

    public MaterialOverride(IModelConfiguration owner, Transformation transform, SolidShape shape) {
        this.owner = owner;
        this.transform = transform;
        this.shape = shape;
        this.cache = new ConcurrentHashMap<>();
    }

    private BakedModel bake(MaterialBase material) {
        return MaterialModel.bakeMaterialModel(this.owner, ForgeModelBakery.defaultTextureGetter(), this.transform, ItemOverrides.EMPTY, this.shape, material);
    }

    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
        Item item = stack.getItem();
        MaterialBase material = MaterialItem.getItemMaterial(stack);
        if (material == null) {
            material = ModMaterials.COPPER;
        }
        return this.cache.computeIfAbsent(material, this::bake);
    }
}