package com.atodium.iridynamics.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;

import java.util.*;
import java.util.function.Function;

public class ColoredItemLayerModel implements IModelGeometry<ColoredItemLayerModel> {
    private static final Direction[] HORIZONTALS = {Direction.UP, Direction.DOWN};
    private static final Direction[] VERTICALS = {Direction.WEST, Direction.EAST};
    private static final LayerData EMPTY_LAYER_DATA = new LayerData(0x000000, 0, true);

    private final ImmutableList<Material> textures;
    private final ImmutableList<LayerData> layerData;

    public ColoredItemLayerModel() {
        this(ImmutableList.of());
    }

    public ColoredItemLayerModel(ImmutableList<Material> textures) {
        this(textures, ImmutableList.of());
    }

    public ColoredItemLayerModel(ImmutableList<Material> textures, ImmutableList<LayerData> layerData) {
        this.textures = textures;
        this.layerData = layerData;
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        ImmutableMap<ItemTransforms.TransformType, Transformation> transformMap = PerspectiveMapWrapper.getTransforms(new CompositeModelState(owner.getCombinedTransform(), modelTransform));
        Transformation transform = modelTransform.getRotation();
        TextureAtlasSprite particle = spriteGetter.apply(owner.isTexturePresent("particle") ? owner.resolveTexture("particle") : this.textures.get(0));
        ItemMultiLayerBakedModel.Builder builder = ItemMultiLayerBakedModel.builder(owner, particle, overrides, transformMap);
        // TODO: TexturePixelFlag标记设为尺寸可变的
        TexturePixelFlag pixels = this.textures.size() == 1 ? null : new TexturePixelFlag(16, 16);
        for (int i = 0; i < this.textures.size(); i++) {
            TextureAtlasSprite sprite = spriteGetter.apply(this.textures.get(i));
            LayerData data = this.getLayerData(i);
            RenderType type = ItemLayerModel.getLayerRenderType(data.light == 15);
            builder.addQuads(type, getQuadsForSprite(data.hasTint ? i : -1, sprite, transform, data.color, data.light));
        }
        return builder.build();
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        ImmutableList.Builder<Material> builder = ImmutableList.builder();
        for (int i = 0; owner.isTexturePresent("layer" + i); i++) builder.add(owner.resolveTexture("layer" + i));
        return builder.build();
    }

    private LayerData getLayerData(int index) {
        if (index < 0 || index >= this.layerData.size()) return EMPTY_LAYER_DATA;
        return this.layerData.get(index);
    }

    public static ImmutableList<BakedQuad> getQuadsForSprite(int tint, TextureAtlasSprite sprite, Transformation transform, int color, int light) {
        return getQuadsForSprite(tint, sprite, transform, color, light, null);
    }

    public static ImmutableList<BakedQuad> getQuadsForSprite(int tint, TextureAtlasSprite sprite, Transformation transform, int color, int light, TexturePixelFlag pixels) {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        int uMax = sprite.getWidth();
        int vMax = sprite.getHeight();
        FaceData faceData = new FaceData(uMax, vMax);
        boolean translucent = false;
        for (int i = 0; i < sprite.getFrameCount(); i++) {
            boolean ptu;
            boolean[] ptv = new boolean[uMax];
            Arrays.fill(ptv, true);
            for (int v = 0; v < vMax; v++) {
                ptu = true;
                for (int u = 0; u < uMax; u++) {
                    int alpha = sprite.getPixelRGBA(i, u, vMax - v - 1) >> 24 & 0xff;
                    boolean t = alpha / 255f <= 0.1f;
                    if (!t && alpha < 255) translucent = true;
                    if (ptu && !t) faceData.set(Direction.WEST, u, v);
                    if (!ptu && t) faceData.set(Direction.EAST, u - 1, v);
                    if (ptv[u] && !t) faceData.set(Direction.UP, u, v);
                    if (!ptv[u] && t) faceData.set(Direction.DOWN, u, v - 1);
                    ptu = t;
                    ptv[u] = t;
                }
                if (!ptu) faceData.set(Direction.EAST, uMax - 1, v);
            }
            for (int u = 0; u < uMax; u++) if (!ptv[u]) faceData.set(Direction.DOWN, u, vMax - 1);
        }
        for (Direction direction : HORIZONTALS) {
            for (int v = 0; v < vMax; v++) {
                int uStart = 0, uEnd = uMax;
                boolean building = false;
                for (int u = 0; u < uMax; u++) {
                    boolean canDraw = pixels == null || !pixels.get(u, v);
                    boolean face = canDraw && faceData.get(direction, u, v);
                    if (face) {
                        uEnd = u + 1;
                        if (!building) {
                            building = true;
                            uStart = u;
                        }
                    } else if (building) {
                        if (!canDraw || translucent) {
                            int off = direction == Direction.DOWN ? 1 : 0;
                            builder.add(buildSideQuad(transform, direction, tint, sprite, uStart, v + off, uEnd - uStart, color, light));
                            building = false;
                        }
                    }
                }
                if (building) {
                    int off = direction == Direction.DOWN ? 1 : 0;
                    builder.add(buildSideQuad(transform, direction, tint, sprite, uStart, v + off, uEnd - uStart, color, light));
                }
            }
        }
        for (Direction direction : VERTICALS) {
            for (int u = 0; u < uMax; u++) {
                int vStart = 0, vEnd = vMax;
                boolean building = false;
                for (int v = 0; v < vMax; v++) {
                    boolean canDraw = pixels == null || !pixels.get(u, v);
                    boolean face = canDraw && faceData.get(direction, u, v);
                    if (face) {
                        vEnd = v + 1;
                        if (!building) {
                            building = true;
                            vStart = v;
                        }
                    } else if (building) {
                        if (!canDraw || translucent) {
                            int off = direction == Direction.EAST ? 1 : 0;
                            builder.add(buildSideQuad(transform, direction, tint, sprite, u + off, vStart, vEnd - vStart, color, light));
                            building = false;
                        }
                    }
                }
                if (building) {
                    int off = direction == Direction.EAST ? 1 : 0;
                    builder.add(buildSideQuad(transform, direction, tint, sprite, u + off, vStart, vEnd - vStart, color, light));
                }
            }
        }
        builder.add(buildQuad(transform, Direction.NORTH, sprite, tint, color, light,
                0, 0, 7.5f / 16f, sprite.getU0(), sprite.getV1(),
                0, 1, 7.5f / 16f, sprite.getU0(), sprite.getV0(),
                1, 1, 7.5f / 16f, sprite.getU1(), sprite.getV0(),
                1, 0, 7.5f / 16f, sprite.getU1(), sprite.getV1()
        ));
        builder.add(buildQuad(transform, Direction.SOUTH, sprite, tint, color, light,
                0, 0, 8.5f / 16f, sprite.getU0(), sprite.getV1(),
                1, 0, 8.5f / 16f, sprite.getU1(), sprite.getV1(),
                1, 1, 8.5f / 16f, sprite.getU1(), sprite.getV0(),
                0, 1, 8.5f / 16f, sprite.getU0(), sprite.getV0()
        ));
        if (pixels != null && sprite.getFrameCount() > 0) {
            for (int v = 0; v < vMax; v++) {
                for (int u = 0; u < uMax; u++) {
                    int alpha = sprite.getPixelRGBA(0, u, vMax - v - 1) >> 24 & 0xff;
                    if (alpha / 255f > 0.1f) pixels.set(u, v);
                }
            }
        }
        return builder.build();
    }

    private static BakedQuad buildSideQuad(Transformation transform, Direction direction, int tint, TextureAtlasSprite sprite, int u, int v, int size, int color, int light) {
        final float eps = 1e-2f;
        int width = sprite.getWidth();
        int height = sprite.getHeight();
        float x0 = (float) u / width;
        float y0 = (float) v / height;
        float x1 = x0, y1 = y0;
        float z0 = 7.5f / 16.0f, z1 = 8.5f / 16.0f;
        switch (direction) {
            case WEST:
                z0 = 8.5f / 16f;
                z1 = 7.5f / 16f;
            case EAST:
                y1 = (float) (v + size) / height;
                break;
            case DOWN:
                z0 = 8.5f / 16f;
                z1 = 7.5f / 16f;
            case UP:
                x1 = (float) (u + size) / width;
                break;
            default:
                throw new IllegalArgumentException("Can not handle z-oriented side");
        }
        float dx = direction.getNormal().getX() * eps / width;
        float dy = direction.getNormal().getY() * eps / height;
        float u0 = 16f * (x0 - dx);
        float u1 = 16f * (x1 - dx);
        float v0 = 16f * (1f - y0 - dy);
        float v1 = 16f * (1f - y1 - dy);
        return buildQuad(transform, (direction.getAxis() == Direction.Axis.Y ? direction.getOpposite() : direction),
                sprite, tint, color, light,
                x0, y0, z0, sprite.getU(u0), sprite.getV(v0),
                x1, y1, z0, sprite.getU(u1), sprite.getV(v1),
                x1, y1, z1, sprite.getU(u1), sprite.getV(v1),
                x0, y0, z1, sprite.getU(u0), sprite.getV(v0));
    }


    public static Direction remap(Direction direction) {
        return direction.getAxis() == Direction.Axis.Y ? direction.getOpposite() : direction;
    }

    public static BakedQuad buildQuad(Transformation transform, Direction direction, TextureAtlasSprite sprite, int tint, int color, int light,
                                      float x0, float y0, float z0, float u0, float v0,
                                      float x1, float y1, float z1, float u1, float v1,
                                      float x2, float y2, float z2, float u2, float v2,
                                      float x3, float y3, float z3, float u3, float v3) {
        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
        builder.setQuadTint(tint);
        builder.setQuadOrientation(direction);
        builder.setApplyDiffuseLighting(false);
        boolean hasTransform = !transform.isIdentity();
        IVertexConsumer consumer = hasTransform ? new TRSRTransformer(builder, transform) : builder;
        putVertex(consumer, direction, x0, y0, z0, u0, v0, color, light);
        putVertex(consumer, direction, x1, y1, z1, u1, v1, color, light);
        putVertex(consumer, direction, x2, y2, z2, u2, v2, color, light);
        putVertex(consumer, direction, x3, y3, z3, u3, v3, color, light);
        return builder.build();
    }

    public static void putVertex(IVertexConsumer consumer, Direction direction, float x, float y, float z, float u, float v, int color, int light) {
        VertexFormat format = consumer.getVertexFormat();
        ImmutableList<VertexFormatElement> elements = format.getElements();
        int size = elements.size();
        for (int i = 0; i < size; i++) {
            VertexFormatElement element = elements.get(i);
            outer:
            switch (element.getUsage()) {
                case POSITION:
                    consumer.put(i, x, y, z, 1.0f);
                    break;
                case COLOR:
                    consumer.put(i, ((color >> 16) & 0xff) / 255.0f, ((color >> 8) & 0xff) / 255.0f, ((color) & 0xff) / 255.0f, ((color >> 24) & 0xff) / 255.0f);
                    break;
                case NORMAL:
                    consumer.put(i, (float) direction.getStepX(), (float) direction.getStepY(), (float) direction.getStepZ(), 0.0f);
                    break;
                case UV:
                    switch (element.getIndex()) {
                        case 0:
                            consumer.put(i, u, v, 0.0f, 1.0f);
                            break outer;
                        case 2:
                            float l = (light << 4) / 32768.0f;
                            consumer.put(i, l, l, 0.0f, 1.0f);
                            break outer;
                    }
                default:
                    consumer.put(i);
                    break;
            }
        }
    }

    private static class FaceData {
        private final EnumMap<Direction, BitSet> data;
        private final int vMax;

        FaceData(int uMax, int vMax) {
            this.data = new EnumMap<>(Direction.class);
            this.vMax = vMax;
            this.data.put(Direction.WEST, new BitSet(uMax * vMax));
            this.data.put(Direction.EAST, new BitSet(uMax * vMax));
            this.data.put(Direction.UP, new BitSet(uMax * vMax));
            this.data.put(Direction.DOWN, new BitSet(uMax * vMax));
        }

        public void set(Direction facing, int u, int v) {
            this.data.get(facing).set(getIndex(u, v));
        }

        public boolean get(Direction facing, int u, int v) {
            return this.data.get(facing).get(getIndex(u, v));
        }

        private int getIndex(int u, int v) {
            return v * this.vMax + u;
        }
    }

    private record LayerData(int color, int light, boolean hasTint) {

    }
}