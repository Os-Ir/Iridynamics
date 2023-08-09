package com.atodium.iridynamics.api.pipe;

import com.atodium.iridynamics.api.util.data.DataUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public interface ILiquidPipeNode {
    boolean isConnectable(Direction direction);

    int capacity();

    int fluidAmount();

    int addFluidAmount(int amount);

    Fluid fluid();

    boolean canInput();

    boolean canOutput();

    int maxFlowRate(Direction direction);

    default List<Direction> connectableDirections() {
        List<Direction> directions = Lists.newArrayList();
        for (Direction direction : DataUtil.DIRECTIONS) if (this.isConnectable(direction)) directions.add(direction);
        return directions;
    }

    default void setFluidAmount(int amount) {
        this.addFluidAmount(amount - this.fluidAmount());
    }

    default int remainCapacity() {
        return this.capacity() - this.fluidAmount();
    }

    default boolean full() {
        return this.fluidAmount() == this.capacity();
    }

    default double scaledPressure() {
        return this.full() ? 1.0 : ((double) this.fluidAmount()) / this.capacity();
    }
}