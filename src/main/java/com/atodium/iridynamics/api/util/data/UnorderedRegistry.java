package com.atodium.iridynamics.api.util.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class UnorderedRegistry<K, T> {
    private final BiMap<K, T> registry;
    private final BiMap<T, K> inversedRegistry;
    private boolean frozen;

    public UnorderedRegistry() {
        this.registry = HashBiMap.create();
        this.inversedRegistry = this.registry.inverse();
    }

    public boolean isFrozen() {
        return this.frozen;
    }

    public void freeze() {
        if (this.frozen) {
            throw new IllegalStateException("This registry has already frozen");
        }
        this.frozen = true;
    }

    public boolean containsKey(K key) {
        return this.registry.containsKey(key);
    }

    public boolean containsValue(T value) {
        return this.inversedRegistry.containsKey(value);
    }

    public K getKeyForValue(T value) {
        return this.inversedRegistry.get(value);
    }

    public T get(K key) {
        if (this.registry.containsKey(key)) return this.registry.get(key);
        return null;
    }

    public Set<K> keySet() {
        return this.registry.keySet();
    }

    public Set<Map.Entry<K, T>> entrySet() {
        return this.registry.entrySet();
    }

    public Collection<T> values() {
        return this.registry.values();
    }

    public void register(K key, T value) {
        if (this.frozen) {
            throw new IllegalStateException("This registry has already frozen");
        }
        if (this.registry.containsKey(key)) {
            throw new IllegalStateException("This key has been registered");
        }
        this.registry.put(key, value);
    }

    public void delete(K key) {
        this.registry.remove(key);
    }
}