package com.atodium.iridynamics.api.util.data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeImpl<T> implements ParameterizedType {
    private final Class<T> raw;
    private final Type[] actual;

    public ParameterizedTypeImpl(Class<T> raw, Type[] sub) {
        this.raw = raw;
        this.actual = sub == null ? new Type[0] : sub;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return this.actual;
    }

    @Override
    public Type getRawType() {
        return this.raw;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}