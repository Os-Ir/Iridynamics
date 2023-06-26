package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.api.util.math.MathUtil;

public interface IPottery {
    boolean processed();

    boolean carve(int h, int carve);

    int getCarved(int h);

    default boolean validateHeight(int h) {
        return MathUtil.between(h, 0, 11);
    }
}