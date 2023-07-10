package com.atodium.iridynamics.api.util.math;

public class Vector2i {
    public static final Vector2i ORIGIN = new Vector2i(0, 0);

    private final int x;
    private final int y;

    public Vector2i(int width, int height) {
        this.x = width;
        this.y = height;
    }

    public static Vector2i of(int x, int y) {
        return new Vector2i(x, y);
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public Vector2i plus(Vector2i vec) {
        return new Vector2i(this.x + vec.x, this.y + vec.y);
    }

    public Vector2i minus(Vector2i vec) {
        return new Vector2i(this.x - vec.x, this.y - vec.y);
    }

    public Vector2i multiply(int scale) {
        return new Vector2i(this.x * scale, this.y * scale);
    }

    public int dot(Vector2i vec) {
        return this.x * vec.x + this.y * vec.y;
    }

    public int cross(Vector2i vec) {
        return this.x * vec.y - this.y * vec.x;
    }
}