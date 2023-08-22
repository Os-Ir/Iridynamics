package com.atodium.iridynamics.api.model;

import com.atodium.iridynamics.Iridynamics;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Random;

public class ModelUtil {
    public static final BlockState EMPTY_BLOCK_STATE = Blocks.AIR.defaultBlockState();
    public static final BlockRenderDispatcher EMPTY_BLOCK_RENDER_DISPATCHER = tryCreateEmptyDispatcher();
    public static final IModelData EMPTY_MODEL_DATA = new IModelData() {
        @Override
        public boolean hasProperty(ModelProperty<?> prop) {
            return false;
        }

        @Override
        public <T> T getData(ModelProperty<T> prop) {
            return null;
        }

        @Override
        public <T> T setData(ModelProperty<T> prop, T data) {
            return null;
        }
    };

    private static BlockRenderDispatcher tryCreateEmptyDispatcher() {
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        BlockRenderDispatcher emptyDispatcher = new BlockRenderDispatcher(null, null, null);
        try {
            for (Field field : BlockRenderDispatcher.class.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(emptyDispatcher, field.get(dispatcher));
            }
            ObfuscationReflectionHelper.setPrivateValue(BlockRenderDispatcher.class, emptyDispatcher, new ModelBlockRenderer(Minecraft.getInstance().getBlockColors()), "f_110900_");
        } catch (Exception e) {
            Iridynamics.LOGGER.error("Failed to create empty BlockRenderDispatcher", e);
            return dispatcher;
        }
        return emptyDispatcher;
    }

    public static Random emptyRandom() {
        return new Random(0);
    }

    public static TransformableVertexList createVertexData(BakedModel model) {
        return createVertexData(model, EMPTY_BLOCK_STATE, new PoseStack());
    }

    public static TransformableVertexList createVertexData(BakedModel model, PoseStack transform) {
        return createVertexData(model, EMPTY_BLOCK_STATE, transform);
    }

    public static TransformableVertexList createVertexData(BakedModel model, BlockState state) {
        return createVertexData(model, EMPTY_BLOCK_STATE, new PoseStack());
    }

    public static TransformableVertexList createVertexData(BakedModel model, BlockState state, PoseStack transform) {
        BufferBuilder builder = new BufferBuilder(512);
        EMPTY_BLOCK_RENDER_DISPATCHER.getModelRenderer().tesselateBlock(EmptyRenderLevel.INSTANCE, model, state, BlockPos.ZERO, transform, builder, false, emptyRandom(), 0, OverlayTexture.NO_OVERLAY, EMPTY_MODEL_DATA);
        return createVertexList(builder);
    }

    public static TransformableVertexList createVertexList(BufferBuilder builder) {
        builder.end();
        Pair<BufferBuilder.DrawState, ByteBuffer> data = builder.popNextBuffer();
        return new TransformableVertexList(data.getFirst(), data.getSecond());
    }
}