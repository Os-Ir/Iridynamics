package com.atodium.iridynamics.api.blockEntity;

import com.atodium.iridynamics.api.rotate.IRotateNode;

public interface IRotateNodeHolder<T> {
    void receiveRotateNode(T node);

    @SuppressWarnings("unchecked")
    default void receiveRotateNodeRaw(IRotateNode node) {
        this.receiveRotateNode((T) node);
    }
}