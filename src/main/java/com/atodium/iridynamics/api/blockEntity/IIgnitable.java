package com.atodium.iridynamics.api.blockEntity;

import net.minecraft.core.Direction;

public interface IIgnitable {
    boolean ignite(Direction direction, double temperature);

    void blow(Direction direction, int volume);
}