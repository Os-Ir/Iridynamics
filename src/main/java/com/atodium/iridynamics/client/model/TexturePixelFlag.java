package com.atodium.iridynamics.client.model;

import com.atodium.iridynamics.api.util.math.MathUtil;

public class TexturePixelFlag {
    private final int width, height;
    private final boolean[][] flag;

    public TexturePixelFlag(int width, int height) {
        this.width = width;
        this.height = height;
        this.flag = new boolean[width][height];
    }

    public boolean get(int x, int y) {
        if (MathUtil.between(x, 0, this.width - 1) && MathUtil.between(y, 0, this.height - 1)) {
            return this.flag[x][y];
        }
        return false;
    }

    public void set(int x, int y) {
        if (MathUtil.between(x, 0, this.width - 1) && MathUtil.between(y, 0, this.height - 1)) {
            this.flag[x][y] = true;
        }
    }
}