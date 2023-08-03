package com.atodium.iridynamics.client.renderer.entity;

import com.atodium.iridynamics.api.structure.MovingStructure;
import com.atodium.iridynamics.api.structure.MovingStructureEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class StructureRenderer<T extends MovingStructure<T>> extends EntityRenderer<MovingStructureEntity<T>> {
    protected StructureRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(MovingStructureEntity<T> entity) {
        return null;
    }
}