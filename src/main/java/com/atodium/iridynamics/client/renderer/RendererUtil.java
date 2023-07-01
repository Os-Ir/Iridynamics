package com.atodium.iridynamics.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;

public final class RendererUtil {
    @SuppressWarnings("deprecation")
    public static final ResourceLocation BLOCKS_ATLAS = TextureAtlas.LOCATION_BLOCKS;
    public static final Tesselator TESSELATOR = Tesselator.getInstance();
    public static final BufferBuilder BUFFER_BUILDER = TESSELATOR.getBuilder();

    public static void bindTexture(ResourceLocation location) {
        RenderSystem.setShaderTexture(0, location);
    }

    public static void drawScaledTexturedRect(PoseStack transform, int x, int y, int width, int height, float textureX, float textureY, float textureWidth, float textureHeight) {
        drawScaledTexturedRect(transform, x, y, width, height, textureX, textureY, textureWidth, textureHeight, 1.0f);
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
    public static void drawScaledTexturedRect(PoseStack transform, int x, int y, int width, int height, float textureX, float textureY, float textureWidth, float textureHeight, float alpha) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BUFFER_BUILDER.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix4f = transform.last().pose();
        BUFFER_BUILDER.vertex(matrix4f, x, y + height, 0).uv(textureX, textureY + textureHeight).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, x + width, y + height, 0).uv(textureX + textureWidth, textureY + textureHeight).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, x + width, y, 0).uv(textureX + textureWidth, textureY).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, x, y, 0).uv(textureX, textureY).endVertex();
        BUFFER_BUILDER.end();
        BufferUploader.end(BUFFER_BUILDER);
    }

    public static void fill(PoseStack matrixStack, int x, int y, int width, int height, int color) {
        int a = (color >> 24) & 0xff;
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;
        RenderSystem.setShaderColor(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        BUFFER_BUILDER.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix4f = matrixStack.last().pose();
        BUFFER_BUILDER.vertex(matrix4f, x, y + height, 0).color(r, g, b, a).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, x + width, y + height, 0).color(r, g, b, a).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, x + width, y, 0).color(r, g, b, a).endVertex();
        BUFFER_BUILDER.vertex(matrix4f, x, y, 0).color(r, g, b, a).endVertex();
        BUFFER_BUILDER.end();
        BufferUploader.end(BUFFER_BUILDER);
        RenderSystem.disableBlend();
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
        renderFace(transform, consumer, sprite, combinedLight, combinedOverlay, buildPositiveXVertices(minX, minY, minZ, maxX, maxY, maxZ), 1.0f, 0.0f, 0.0f, zSize, ySize);
        renderFace(transform, consumer, sprite, combinedLight, combinedOverlay, buildNegativeXVertices(minX, minY, minZ, maxX, maxY, maxZ), -1.0f, 0.0f, 0.0f, zSize, ySize);
        renderFace(transform, consumer, sprite, combinedLight, combinedOverlay, buildPositiveYVertices(minX, minY, minZ, maxX, maxY, maxZ), 0.0f, 1.0f, 0.0f, zSize, xSize);
        renderFace(transform, consumer, sprite, combinedLight, combinedOverlay, buildNegativeYVertices(minX, minY, minZ, maxX, maxY, maxZ), 0.0f, -1.0f, 0.0f, zSize, xSize);
        renderFace(transform, consumer, sprite, combinedLight, combinedOverlay, buildPositiveZVertices(minX, minY, minZ, maxX, maxY, maxZ), 0.0f, 0.0f, 1.0f, xSize, ySize);
        renderFace(transform, consumer, sprite, combinedLight, combinedOverlay, buildNegativeZVertices(minX, minY, minZ, maxX, maxY, maxZ), 0.0f, 0.0f, -1.0f, xSize, ySize);
    }

    public static void renderCuboid(PoseStack transform, VertexConsumer consumer, TextureAtlasSprite sprite, int color, int combinedLight, int combinedOverlay, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        int xSize = Math.round(16.0f * (maxX - minX));
        int ySize = Math.round(16.0f * (maxY - minY));
        int zSize = Math.round(16.0f * (maxZ - minZ));
        renderFace(transform, consumer, sprite, color, combinedLight, combinedOverlay, buildPositiveXVertices(minX, minY, minZ, maxX, maxY, maxZ), 1.0f, 0.0f, 0.0f, zSize, ySize);
        renderFace(transform, consumer, sprite, color, combinedLight, combinedOverlay, buildNegativeXVertices(minX, minY, minZ, maxX, maxY, maxZ), -1.0f, 0.0f, 0.0f, zSize, ySize);
        renderFace(transform, consumer, sprite, color, combinedLight, combinedOverlay, buildPositiveYVertices(minX, minY, minZ, maxX, maxY, maxZ), 0.0f, 1.0f, 0.0f, zSize, xSize);
        renderFace(transform, consumer, sprite, color, combinedLight, combinedOverlay, buildNegativeYVertices(minX, minY, minZ, maxX, maxY, maxZ), 0.0f, -1.0f, 0.0f, zSize, xSize);
        renderFace(transform, consumer, sprite, color, combinedLight, combinedOverlay, buildPositiveZVertices(minX, minY, minZ, maxX, maxY, maxZ), 0.0f, 0.0f, 1.0f, xSize, ySize);
        renderFace(transform, consumer, sprite, color, combinedLight, combinedOverlay, buildNegativeZVertices(minX, minY, minZ, maxX, maxY, maxZ), 0.0f, 0.0f, -1.0f, xSize, ySize);
    }

    public static void renderFace(PoseStack transform, VertexConsumer consumer, TextureAtlasSprite sprite, int combinedLight, int combinedOverlay, float[][] vertices, float nx, float ny, float nz, float uSize, float vSize, float uStart, float vStart) {
        for (float[] vertex : vertices) {
            renderVertex(transform, consumer, combinedLight, combinedOverlay, vertex[0], vertex[1], vertex[2], nx, ny, nz, uStart + sprite.getU(vertex[3] * uSize), vStart + sprite.getV(vertex[4] * vSize));
        }
    }

    public static void renderFace(PoseStack transform, VertexConsumer consumer, TextureAtlasSprite sprite, int color, int combinedLight, int combinedOverlay, float[][] vertices, float nx, float ny, float nz, float uSize, float vSize, float uStart, float vStart) {
        for (float[] vertex : vertices) {
            renderVertex(transform, consumer, color, combinedLight, combinedOverlay, vertex[0], vertex[1], vertex[2], nx, ny, nz, uStart + sprite.getU(vertex[3] * uSize), vStart + sprite.getV(vertex[4] * vSize));
        }
    }

    public static void renderFace(PoseStack transform, VertexConsumer consumer, TextureAtlasSprite sprite, int combinedLight, int combinedOverlay, float[][] vertices, float nx, float ny, float nz, float uSize, float vSize) {
        for (float[] vertex : vertices) {
            renderVertex(transform, consumer, combinedLight, combinedOverlay, vertex[0], vertex[1], vertex[2], nx, ny, nz, sprite.getU(vertex[3] * uSize), sprite.getV(vertex[4] * vSize));
        }
    }

    public static void renderFace(PoseStack transform, VertexConsumer consumer, TextureAtlasSprite sprite, int color, int combinedLight, int combinedOverlay, float[][] vertices, float nx, float ny, float nz, float uSize, float vSize) {
        for (float[] vertex : vertices) {
            renderVertex(transform, consumer, color, combinedLight, combinedOverlay, vertex[0], vertex[1], vertex[2], nx, ny, nz, sprite.getU(vertex[3] * uSize), sprite.getV(vertex[4] * vSize));
        }
    }

    public static void renderVertex(PoseStack transform, VertexConsumer consumer, int combinedLight, int combinedOverlay, float x, float y, float z, float nx, float ny, float nz, float u, float v) {
        float shade = getShade(nx, ny, nz);
        consumer.vertex(transform.last().pose(), x, y, z).color(shade, shade, shade, 1.0f).uv(u, v).uv2(combinedLight).overlayCoords(combinedOverlay).normal(transform.last().normal(), nx, ny, nz).endVertex();
    }

    public static void renderVertex(PoseStack transform, VertexConsumer consumer, int color, int combinedLight, int combinedOverlay, float x, float y, float z, float nx, float ny, float nz, float u, float v) {
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

    public static float[][] buildPositiveXVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new float[][]{
                {minX, minY, minZ, 0, 1},
                {minX, minY, maxZ, 1, 1},
                {minX, maxY, maxZ, 1, 0},
                {minX, maxY, minZ, 0, 0}
        };
    }

    public static float[][] buildNegativeXVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new float[][]{
                {maxX, minY, maxZ, 1, 0},
                {maxX, minY, minZ, 0, 0},
                {maxX, maxY, minZ, 0, 1},
                {maxX, maxY, maxZ, 1, 1}
        };
    }

    public static float[][] buildPositiveYVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new float[][]{
                {minX, maxY, minZ, 0, 1},
                {minX, maxY, maxZ, 1, 1},
                {maxX, maxY, maxZ, 1, 0},
                {maxX, maxY, minZ, 0, 0}
        };
    }

    public static float[][] buildNegativeYVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new float[][]{
                {minX, minY, maxZ, 1, 0},
                {minX, minY, minZ, 0, 0},
                {maxX, minY, minZ, 0, 1},
                {maxX, minY, maxZ, 1, 1}
        };
    }

    public static float[][] buildPositiveZVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new float[][]{
                {maxX, minY, minZ, 0, 1},
                {minX, minY, minZ, 1, 1},
                {minX, maxY, minZ, 1, 0},
                {maxX, maxY, minZ, 0, 0}
        };
    }

    public static float[][] buildNegativeZVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new float[][]{
                {minX, minY, maxZ, 1, 0},
                {maxX, minY, maxZ, 0, 0},
                {maxX, maxY, maxZ, 0, 1},
                {minX, maxY, maxZ, 1, 1}
        };
    }
}