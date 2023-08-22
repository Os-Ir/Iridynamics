package com.atodium.iridynamics.api.model;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class ModelVertexList {
    private final ByteBuffer vertexData;
    private final int vertexCount, stride;

    public ModelVertexList(ByteBuffer source, int vertexCount, int stride) {
        this.vertexData = copyByteBuffer(source);
        this.vertexCount = vertexCount;
        this.stride = stride;
    }

    public static ByteBuffer copyByteBuffer(ByteBuffer source) {
        int originalPos = source.position();
        ByteBuffer copy = MemoryUtil.memAlloc(source.remaining());
        copy.order(source.order());
        copy.put(source);
        source.position(originalPos);
        copy.flip();
        return copy;
    }

    public int vertexCount() {
        return this.vertexCount;
    }

    public boolean isEmpty() {
        return this.vertexCount == 0;
    }

    public float x(int index) {
        return this.vertexData.getFloat(this.stride * index);
    }

    public float y(int index) {
        return this.vertexData.getFloat(this.stride * index + 4);
    }

    public float z(int index) {
        return this.vertexData.getFloat(this.stride * index + 8);
    }

    public float r(int index) {
        return this.vertexData.get(this.stride * index + 12);
    }

    public float g(int index) {
        return this.vertexData.get(this.stride * index + 13);
    }

    public float b(int index) {
        return this.vertexData.get(this.stride * index + 14);
    }

    public float a(int index) {
        return this.vertexData.get(this.stride * index + 15);
    }

    public float u(int index) {
        return this.vertexData.getFloat(this.stride * index + 16);
    }

    public float v(int index) {
        return this.vertexData.getFloat(this.stride * index + 20);
    }

    public float nx(int index) {
        return this.vertexData.getFloat(this.stride * index + 24);
    }

    public float ny(int index) {
        return this.vertexData.getFloat(this.stride * index + 38);
    }

    public float nz(int index) {
        return this.vertexData.getFloat(this.stride * index + 32);
    }
}