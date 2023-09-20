package com.atodium.iridynamics.client.renderer;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class RendererUtil {
    @SuppressWarnings("deprecation")
    public static final ResourceLocation BLOCKS_ATLAS = TextureAtlas.LOCATION_BLOCKS;
    public static final Tesselator TESSELATOR = Tesselator.getInstance();
    public static final BufferBuilder BUFFER_BUILDER = TESSELATOR.getBuilder();

    private static TextureAtlasSprite whiteTexture;

    public static TextureAtlasSprite whiteTexture() {
        if (whiteTexture == null)
            whiteTexture = Minecraft.getInstance().getTextureAtlas(RendererUtil.BLOCKS_ATLAS).apply(Iridynamics.rl("block/white"));
        return whiteTexture;
    }

    public static void bindTexture(ResourceLocation location) {
        RenderSystem.setShaderTexture(0, location);
    }

    public static void drawScaledTexturedRect(PoseStack transform, float x, float y, float width, float height, float textureX, float textureY, float textureWidth, float textureHeight) {
        drawScaledTexturedRect(transform, x, y, width, height, textureX, textureY, textureWidth, textureHeight, 1.0f);
    }

    public static BlockPos castPosition(Position vec) {
        return new BlockPos(vec.x(), vec.y(), vec.z());
    }

    /**
     * 将材质的指定部分缩放绘制到指定界面范围内
     *
     * @param x             绘制在界面中的x坐标
     * @param y             绘制在界面中的y坐标
     * @param width         绘制在界面中的宽度
     * @param height        绘制在界面中的高度
     * @param textureX      材质绘制部分开始位置的x占比, 属于[0, 1]
     * @param textureY      材质绘制部分开始位置的y占比, 属于[0, 1]
     * @param textureWidth  材质绘制部分宽度的x占比, 属于[0, 1]
     * @param textureHeight 材质绘制部分高度的x占比, 属于[0, 1]
     * @param alpha         不透明度, 属于[0, 1]
     */
    public static void drawScaledTexturedRect(PoseStack transform, float x, float y, float width, float height, float textureX, float textureY, float textureWidth, float textureHeight, float alpha) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BUFFER_BUILDER.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix4f = transform.last().pose();
        BUFFER_BUILDER.vertex(matrix4f, x, y + height, 0.0f).uv(textureX, textureY + textureHeight).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, x + width, y + height, 0.0f).uv(textureX + textureWidth, textureY + textureHeight).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, x + width, y, 0.0f).uv(textureX + textureWidth, textureY).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, x, y, 0.0f).uv(textureX, textureY).endVertex();
        BUFFER_BUILDER.end();
        BufferUploader.end(BUFFER_BUILDER);
    }

    public static void fill(PoseStack transform, float xi, float yi, float width, float height, int color) {
        int a = (color >> 24) & 0xff;
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;
        RenderSystem.setShaderColor(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        BUFFER_BUILDER.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix4f = transform.last().pose();
        float xa = xi + width;
        float ya = yi + height;
        BUFFER_BUILDER.vertex(matrix4f, xi, ya, 0.0f).color(r, g, b, a).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, xa, ya, 0.0f).color(r, g, b, a).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, xa, yi, 0.0f).color(r, g, b, a).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, xi, yi, 0.0f).color(r, g, b, a).endVertex();
        BUFFER_BUILDER.end();
        BufferUploader.end(BUFFER_BUILDER);
        RenderSystem.disableBlend();
    }

    public static void fillInRange(PoseStack transform, float x, float y, float width, float height, int color, float minX, float maxX, float minY, float maxY) {
        float xi = Mth.clamp(x, minX, maxX);
        float xa = Mth.clamp(x + width, minX, maxX);
        float yi = Mth.clamp(y, minY, maxY);
        float ya = Mth.clamp(y + height, minY, maxY);
        if (MathUtil.isEquals(xi, xa) || MathUtil.isEquals(yi, ya)) return;
        int a = (color >> 24) & 0xff;
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;
        RenderSystem.setShaderColor(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        BUFFER_BUILDER.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix4f = transform.last().pose();
        BUFFER_BUILDER.vertex(matrix4f, xi, ya, 0.0f).color(r, g, b, a).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, xa, ya, 0.0f).color(r, g, b, a).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, xa, yi, 0.0f).color(r, g, b, a).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, xi, yi, 0.0f).color(r, g, b, a).endVertex();
        BUFFER_BUILDER.end();
        BufferUploader.end(BUFFER_BUILDER);
        RenderSystem.disableBlend();
    }

    public static void transformToDirection(PoseStack transform, Direction direction, double width) {
        if (direction.get2DDataValue() >= 0) {
            transform.translate(width, 0.0, width);
            transform.mulPose(Vector3f.YP.rotationDegrees(getDirectionAngel(direction)));
            transform.translate(-width, 0.0, -width);
        } else {
            transform.translate(0.0, width, width);
            transform.mulPose(Vector3f.XP.rotationDegrees(direction == Direction.UP ? 90.0f : -90.0f));
            transform.translate(0.0, -width, -width);
        }
    }

    public static void transformToDirection(PoseStack transform, Direction direction) {
        if (direction.get2DDataValue() >= 0) {
            transform.translate(0.5, 0.0, 0.5);
            transform.mulPose(Vector3f.YP.rotationDegrees(getDirectionAngel(direction)));
            transform.translate(-0.5, 0.0, -0.5);
        } else {
            transform.translate(0.0, 0.5, 0.5);
            transform.mulPose(Vector3f.XP.rotationDegrees(direction == Direction.UP ? 90.0f : -90.0f));
            transform.translate(0.0, -0.5, -0.5);
        }
    }

    public static float getDirectionAngel(Direction direction) {
        switch (direction) {
            case NORTH -> {
                return 0.0f;
            }
            case SOUTH -> {
                return 180.0f;
            }
            case WEST -> {
                return 90.0f;
            }
            case EAST -> {
                return 270.0f;
            }
        }
        return 0.0f;
    }

    public static void renderCuboid(PoseStack transform, VertexConsumer consumer, TextureAtlasSprite sprite, int combinedLight, int combinedOverlay, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        int xSize = Math.round(16.0f * (maxX - minX));
        int ySize = Math.round(16.0f * (maxY - minY));
        int zSize = Math.round(16.0f * (maxZ - minZ));
        renderFace(transform, consumer, sprite, combinedLight, combinedOverlay, Direction.EAST, minX, minY, minZ, maxX, maxY, maxZ, 1.0f, 0.0f, 0.0f, zSize, ySize);
        renderFace(transform, consumer, sprite, combinedLight, combinedOverlay, Direction.WEST, minX, minY, minZ, maxX, maxY, maxZ, -1.0f, 0.0f, 0.0f, zSize, ySize);
        renderFace(transform, consumer, sprite, combinedLight, combinedOverlay, Direction.UP, minX, minY, minZ, maxX, maxY, maxZ, 0.0f, 1.0f, 0.0f, zSize, xSize);
        renderFace(transform, consumer, sprite, combinedLight, combinedOverlay, Direction.DOWN, minX, minY, minZ, maxX, maxY, maxZ, 0.0f, -1.0f, 0.0f, zSize, xSize);
        renderFace(transform, consumer, sprite, combinedLight, combinedOverlay, Direction.SOUTH, minX, minY, minZ, maxX, maxY, maxZ, 0.0f, 0.0f, 1.0f, xSize, ySize);
        renderFace(transform, consumer, sprite, combinedLight, combinedOverlay, Direction.NORTH, minX, minY, minZ, maxX, maxY, maxZ, 0.0f, 0.0f, -1.0f, xSize, ySize);
    }

    public static void renderColorCuboid(PoseStack transform, VertexConsumer consumer, int color, int combinedLight, int combinedOverlay, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        renderColorFace(transform, consumer, color, combinedLight, combinedOverlay, Direction.EAST, minX, minY, minZ, maxX, maxY, maxZ, 1.0f, 0.0f, 0.0f);
        renderColorFace(transform, consumer, color, combinedLight, combinedOverlay, Direction.WEST, minX, minY, minZ, maxX, maxY, maxZ, -1.0f, 0.0f, 0.0f);
        renderColorFace(transform, consumer, color, combinedLight, combinedOverlay, Direction.UP, minX, minY, minZ, maxX, maxY, maxZ, 0.0f, 1.0f, 0.0f);
        renderColorFace(transform, consumer, color, combinedLight, combinedOverlay, Direction.DOWN, minX, minY, minZ, maxX, maxY, maxZ, 0.0f, -1.0f, 0.0f);
        renderColorFace(transform, consumer, color, combinedLight, combinedOverlay, Direction.SOUTH, minX, minY, minZ, maxX, maxY, maxZ, 0.0f, 0.0f, 1.0f);
        renderColorFace(transform, consumer, color, combinedLight, combinedOverlay, Direction.NORTH, minX, minY, minZ, maxX, maxY, maxZ, 0.0f, 0.0f, -1.0f);
    }

    public static void renderFace(PoseStack transform, VertexConsumer consumer, TextureAtlasSprite sprite, int combinedLight, int combinedOverlay, Direction direction, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float nx, float ny, float nz, float uSize, float vSize, float uStart, float vStart) {
        float[][] vertices = buildVertices(direction, minX, minY, minZ, maxX, maxY, maxZ);
        float du = uStart * (sprite.getU(1.0) - sprite.getU(0.0));
        float dv = vStart * (sprite.getV(1.0) - sprite.getV(0.0));
        for (float[] vertex : vertices)
            renderVertex(transform, consumer, combinedLight, combinedOverlay, vertex[0], vertex[1], vertex[2], nx, ny, nz, du + sprite.getU(vertex[3] * uSize), dv + sprite.getV(vertex[4] * vSize));
    }

    public static void renderFace(PoseStack transform, VertexConsumer consumer, TextureAtlasSprite sprite, int combinedLight, int combinedOverlay, Direction direction, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float nx, float ny, float nz, float uSize, float vSize) {
        float[][] vertices = buildVertices(direction, minX, minY, minZ, maxX, maxY, maxZ);
        for (float[] vertex : vertices)
            renderVertex(transform, consumer, combinedLight, combinedOverlay, vertex[0], vertex[1], vertex[2], nx, ny, nz, sprite.getU(vertex[3] * uSize), sprite.getV(vertex[4] * vSize));
    }

    public static void renderColorFace(PoseStack transform, VertexConsumer consumer, int color, int combinedLight, int combinedOverlay, Direction direction, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float nx, float ny, float nz) {
        float[][] vertices = buildVertices(direction, minX, minY, minZ, maxX, maxY, maxZ);
        for (float[] vertex : vertices)
            renderColorVertex(transform, consumer, color, combinedLight, combinedOverlay, vertex[0], vertex[1], vertex[2], nx, ny, nz, whiteTexture().getU(vertex[3] * 16.0f), whiteTexture().getV(vertex[4] * 16.0f));
    }

    public static void renderVertex(PoseStack transform, VertexConsumer consumer, int combinedLight, int combinedOverlay, float x, float y, float z, float nx, float ny, float nz, float u, float v) {
        float shade = getShade(nx, ny, nz);
        consumer.vertex(transform.last().pose(), x, y, z).color(shade, shade, shade, 1.0f).uv(u, v).uv2(combinedLight).overlayCoords(combinedOverlay).normal(transform.last().normal(), nx, ny, nz).endVertex();
    }

    public static void renderColorVertex(PoseStack transform, VertexConsumer consumer, int color, int combinedLight, int combinedOverlay, float x, float y, float z, float nx, float ny, float nz, float u, float v) {
        float shade = getShade(nx, ny, nz);
        consumer.vertex(transform.last().pose(), x, y, z).color(((color >> 16) & 0xff) / 255.0f * shade, ((color >> 8) & 0xff) / 255.0f * shade, (color & 0xff) / 255.0f * shade, 1.0f).uv(u, v).uv2(combinedLight).overlayCoords(combinedOverlay).normal(transform.last().normal(), nx, ny, nz).endVertex();
    }

    public static float getShade(float nx, float ny, float nz) {
        return switch (getClosestDirection(nx, ny, nz)) {
            case DOWN -> 0.5f;
            case UP -> 1.0f;
            case NORTH, SOUTH -> 0.8f;
            case WEST, EAST -> 0.6f;
        };
    }

    public static Direction getClosestDirection(float nx, float ny, float nz) {
        float value = 0.0f;
        Direction ret = Direction.UP;
        for (Direction direction : Direction.values()) {
            Vec3i directionNormal = direction.getNormal();
            float t = nx * directionNormal.getX() + ny * directionNormal.getY() + nz * directionNormal.getZ();
            if (t > value) {
                value = t;
                ret = direction;
            }
        }
        return ret;
    }

    public static float[][] buildVertices(Direction direction, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        switch (direction) {
            case EAST -> {
                return buildPositiveXVertices(minX, minY, minZ, maxX, maxY, maxZ);
            }
            case WEST -> {
                return buildNegativeXVertices(minX, minY, minZ, maxX, maxY, maxZ);
            }
            case UP -> {
                return buildPositiveYVertices(minX, minY, minZ, maxX, maxY, maxZ);
            }
            case DOWN -> {
                return buildNegativeYVertices(minX, minY, minZ, maxX, maxY, maxZ);
            }
            case SOUTH -> {
                return buildPositiveZVertices(minX, minY, minZ, maxX, maxY, maxZ);
            }
            default -> {
                return buildNegativeZVertices(minX, minY, minZ, maxX, maxY, maxZ);
            }
        }
    }

    public static float[][] buildPositiveXVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new float[][]{
                {maxX, maxY, maxZ, 0.0f, 0.0f},
                {maxX, minY, maxZ, 0.0f, 1.0f},
                {maxX, minY, minZ, 1.0f, 1.0f},
                {maxX, maxY, minZ, 1.0f, 0.0f}
        };
    }

    public static float[][] buildNegativeXVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new float[][]{
                {minX, maxY, minZ, 0.0f, 0.0f},
                {minX, minY, minZ, 0.0f, 1.0f},
                {minX, minY, maxZ, 1.0f, 1.0f},
                {minX, maxY, maxZ, 1.0f, 0.0f}
        };
    }

    public static float[][] buildPositiveYVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new float[][]{
                {minX, maxY, minZ, 0.0f, 0.0f},
                {minX, maxY, maxZ, 0.0f, 1.0f},
                {maxX, maxY, maxZ, 1.0f, 1.0f},
                {maxX, maxY, minZ, 1.0f, 0.0f}
        };
    }

    public static float[][] buildNegativeYVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new float[][]{
                {minX, minY, maxZ, 0.0f, 0.0f},
                {minX, minY, minZ, 0.0f, 1.0f},
                {maxX, minY, minZ, 1.0f, 1.0f},
                {maxX, minY, maxZ, 1.0f, 0.0f}
        };
    }

    public static float[][] buildPositiveZVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new float[][]{
                {minX, maxY, maxZ, 0.0f, 0.0f},
                {minX, minY, maxZ, 0.0f, 1.0f},
                {maxX, minY, maxZ, 1.0f, 1.0f},
                {maxX, maxY, maxZ, 1.0f, 0.0f}
        };
    }

    public static float[][] buildNegativeZVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new float[][]{
                {maxX, maxY, minZ, 0.0f, 0.0f},
                {maxX, minY, minZ, 0.0f, 1.0f},
                {minX, minY, minZ, 1.0f, 1.0f},
                {minX, maxY, minZ, 1.0f, 0.0f}
        };
    }
}