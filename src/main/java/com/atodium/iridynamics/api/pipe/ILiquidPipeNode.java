package com.atodium.iridynamics.api.pipe;

import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.api.util.math.MathUtil;
import net.minecraft.core.Direction;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public interface ILiquidPipeNode {
    boolean contains(Direction direction);

    boolean connected(Direction direction);

    int capacity();

    int fluidAmount();

    int addFluidAmount(int amount);

    boolean canInput();

    boolean canOutput();

    int maxFlowRate(Direction direction);

    default List<Direction> containedDirections() {
        List<Direction> directions = Lists.newArrayList();
        for (Direction direction : DataUtil.DIRECTIONS) if (this.contains(direction)) directions.add(direction);
        return directions;
    }

    default List<Direction> connectedDirections() {
        List<Direction> directions = Lists.newArrayList();
        for (Direction direction : DataUtil.DIRECTIONS) if (this.connected(direction)) directions.add(direction);
        return directions;
    }

    default void setFluidAmount(int amount) {
        this.addFluidAmount(amount - this.fluidAmount());
    }

    default int remainCapacity() {
        return this.capacity() - this.fluidAmount();
    }

    default boolean empty() {
        return this.fluidAmount() == 0;
    }

    default boolean full() {
        return MathUtil.between(this.fluidAmount(), this.capacity() * 0.9, this.capacity());
    }

    default double scaledPressure() {
        return this.full() ? 1.0 : ((double) this.fluidAmount()) / this.capacity();
    }
}