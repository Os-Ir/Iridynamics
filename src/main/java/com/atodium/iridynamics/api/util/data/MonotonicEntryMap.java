package com.atodium.iridynamics.api.util.data;

import com.google.common.collect.ImmutableList;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class MonotonicEntryMap {
    private final List<Pair<Double, Double>> map;

    public MonotonicEntryMap(List<Pair<Double, Double>> map) {
        this.map = ImmutableList.copyOf(map);
    }

    public List<Pair<Double, Double>> getPointsMap() {
        return this.map;
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public double keyFloor(double key) {
        if (this.isEmpty()) return 0.0;
        int size = this.map.size();
        for (int i = 0; i < size; i++)
            if (this.map.get(i).getLeft() > key) {
                if (i == 0) return 0.0;
                return this.map.get(i - 1).getLeft();
            }
        return this.map.get(size - 1).getLeft();
    }

    public double keyCeil(double key) {
        if (this.isEmpty()) return 0.0;
        for (Pair<Double, Double> pair : this.map)
            if (pair.getLeft() > key) return pair.getLeft();
        return 0.0;
    }

    public double valueFloor(double value) {
        if (this.isEmpty()) return 0.0;
        int size = this.map.size();
        for (int i = 0; i < size; i++)
            if (this.map.get(i).getRight() > value) {
                if (i == 0) return 0.0;
                return this.map.get(i - 1).getRight();
            }
        return this.map.get(size - 1).getRight();
    }

    public double valueCeil(double value) {
        if (this.isEmpty()) return 0.0;
        for (Pair<Double, Double> pair : this.map)
            if (pair.getRight() > value) return pair.getRight();
        return 0.0;
    }

    public double getKey(double value) {
        if (this.isEmpty()) return 0.0;
        int size = this.map.size();
        for (int i = 0; i < size; i++)
            if (this.map.get(i).getRight() > value) {
                if (i == 0) return 0.0;
                return this.map.get(i - 1).getLeft();
            }
        return this.map.get(size - 1).getLeft();
    }

    public double getValue(double key) {
        if (this.isEmpty()) return 0.0;
        int size = this.map.size();
        for (int i = 0; i < size; i++)
            if (this.map.get(i).getLeft() > key) {
                if (i == 0) return 0.0;
                return this.map.get(i - 1).getRight();
            }
        return this.map.get(size - 1).getRight();
    }

    @Override
    public String toString() {
        return this.map.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Pair<Double, Double>> map;
        private double lastKey, lastValue;

        public Builder() {
            this.map = Lists.newArrayList();
        }

        public Builder addData(double key, double value) {
            if (!this.map.isEmpty() && (key < this.lastKey || value < this.lastValue))
                throw new IllegalArgumentException("Keys or values of MonotonicEntryMap should be monotonic");
            this.lastKey = key;
            this.lastValue = value;
            this.map.add(Pair.of(key, value));
            return this;
        }

        public MonotonicEntryMap build() {
            return new MonotonicEntryMap(this.map);
        }
    }
}