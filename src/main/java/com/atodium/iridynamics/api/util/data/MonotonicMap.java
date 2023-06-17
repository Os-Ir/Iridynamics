package com.atodium.iridynamics.api.util.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class MonotonicMap<T> {
    private static final Comparator<Pair<Double, ?>> COMPARATOR = Comparator.comparing(Pair::getLeft);

    private final List<Pair<Double, T>> map;

    public MonotonicMap(List<Pair<Double, T>> map) {
        this.map = ImmutableList.copyOf(map);
    }

    public List<Pair<Double, T>> getPointsMap() {
        return this.map;
    }

    public double getCriticalPoint(T value) {
        for (Pair<Double, T> pair : this.map) if (pair.getRight().equals(value)) return pair.getLeft();
        return 0.0;
    }

    public T getData(double num) {
        int size = this.map.size();
        for (int i = 0; i < size; i++)
            if (this.map.get(i).getLeft() > num) {
                if (i == 0) return null;
                return this.map.get(i - 1).getRight();
            }
        return this.map.get(size - 1).getRight();
    }

    @Override
    public String toString() {
        return this.map.toString();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private final Set<Double> criticalPoints;
        private final List<Pair<Double, T>> map;

        public Builder() {
            this.criticalPoints = Sets.newHashSet();
            this.map = Lists.newArrayList();
        }

        public Builder<T> addCriticalPoint(double num, T value) {
            if (this.criticalPoints.contains(num))
                throw new IllegalArgumentException("Critical Points of MonotonicMap can not be repetitive");
            this.criticalPoints.add(num);
            this.map.add(Pair.of(num, value));
            return this;
        }

        public MonotonicMap<T> build() {
            this.map.sort(COMPARATOR);
            return new MonotonicMap<>(this.map);
        }
    }
}