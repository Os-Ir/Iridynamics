package com.atodium.iridynamics.api.module.research;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.Mth;

public class ResearchNode {
    private final ResearchNetwork network;
    private final ResearchNodeType type;
    private final String name;
    private final Object2FloatMap<ResearchNode> correlationsTo;
    private final Object2FloatMap<ResearchNode> correlationsFrom;
    private final float minUnlockCoefficient;
    private int layer;

    public ResearchNode(ResearchNetwork network, ResearchNodeType type, String name, Object2FloatMap<ResearchNode> correlations, float minUnlockCoefficient) {
        this.network = network;
        this.type = type;
        this.name = name;
        this.correlationsTo = correlations;
        this.correlationsFrom = new Object2FloatOpenHashMap<>();
        this.minUnlockCoefficient = Mth.clamp(minUnlockCoefficient, 0.0f, 1.0f);
        this.layer = 1;
    }

    public ResearchNetwork network() {
        return this.network;
    }

    public ResearchNodeType type() {
        return this.type;
    }

    public String name() {
        return this.name;
    }

    public int layer() {
        return this.layer;
    }

    public float minUnlockCoefficient() {
        return this.minUnlockCoefficient;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public float correlationTo(ResearchNode to) {
        return this.correlationsTo.apply(to);
    }

    public Object2FloatMap<ResearchNode> allCorrelationsTo() {
        return this.correlationsTo;
    }

    public void putCorrelationFrom(ResearchNode from, float correlation) {
        if (!this.correlationsFrom.containsKey(from)) this.correlationsFrom.put(from, correlation);
    }

    public float correlationFrom(ResearchNode from) {
        return this.correlationsFrom.apply(from);
    }

    public Object2FloatMap<ResearchNode> allCorrelationsFrom() {
        return this.correlationsFrom;
    }
}