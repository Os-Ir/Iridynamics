package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.api.util.math.MathUtil;

public interface IForging {
    boolean processed();

    boolean hit(int x, int y, double thickness, int range);

    boolean carve(int x, int y);

    double getThickness(int x, int y);

    double getMaxThickness();

    default boolean validatePos(int x, int y) {
        return MathUtil.between(x, 0, 6) && MathUtil.between(y, 0, 6);
    }
}