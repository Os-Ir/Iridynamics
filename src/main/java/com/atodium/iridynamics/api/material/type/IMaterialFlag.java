package com.atodium.iridynamics.api.material.type;

public interface IMaterialFlag {
    int getId();

    default long getValue() {
        return 1L << this.getId();
    }

    default void setFlagForMaterial(MaterialBase... materials) {
        for (MaterialBase material : materials) {
            material.addFlag(this);
        }
    }
}