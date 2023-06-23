package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.api.util.math.MathUtil;

public interface ICarving {
    boolean processed();

    int getOriginalThickness();

    boolean carve(int x, int y);

    int getThickness(int x, int y);

    default boolean validatePos(int x, int y) {
        return MathUtil.between(x, 0, 11) && MathUtil.between(y, 0, 11);
    }
}