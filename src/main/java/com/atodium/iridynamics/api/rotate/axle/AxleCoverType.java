package com.atodium.iridynamics.api.rotate.axle;

public enum AxleCoverType {
    DECORATION(0, AxleCoverDirection.ALL, "decoration"), PARTITION(1, AxleCoverDirection.AXIAL, "partition"), REVERSING(2, AxleCoverDirection.BESIDE, "reversing"), LIMITER(3, AxleCoverDirection.AXIAL, "limiter"), SPEEDOMETER(4, AxleCoverDirection.AXIAL, "speedometer");

    private static final AxleCoverType[] TYPES = {DECORATION, PARTITION, REVERSING, LIMITER, SPEEDOMETER};

    private final int index;
    private final AxleCoverDirection direction;
    private final String id;

    AxleCoverType(int index, AxleCoverDirection direction, String id) {
        this.index = index;
        this.direction = direction;
        this.id = id;
    }

    public static AxleCoverType getTypeByIndex(int index) {
        if (index < 0) return null;
        return TYPES[index];
    }

    public int index() {
        return this.index;
    }

    public AxleCoverDirection direction() {
        return this.direction;
    }

    public String id() {
        return this.id;
    }

    public enum AxleCoverDirection {
        ALL, AXIAL, BESIDE
    }
}