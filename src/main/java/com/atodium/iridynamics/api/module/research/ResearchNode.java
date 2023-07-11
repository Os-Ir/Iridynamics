package com.atodium.iridynamics.api.module.research;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.Mth;

public class ResearchNode {
    private final ResearchNodeType type;
    private final String name;
    private final Object2FloatMap<ResearchNode> correlationsTo;
    private final Object2FloatMap<ResearchNode> correlationsFrom;
    private final float minUnlockCoefficient;
    private int layer;

    public ResearchNode(ResearchNodeType type, String name, float minUnlockCoefficient) {
        this.type = type;
        this.name = name;
        this.correlationsTo = new Object2FloatOpenHashMap<>();
        this.correlationsFrom = new Object2FloatOpenHashMap<>();
        this.minUnlockCoefficient = Mth.clamp(minUnlockCoefficient, 0.0f, 1.0f);
        this.layer = ResearchModule.MAX_LAYER;
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
        this.layer = Mth.clamp(layer, 0, ResearchModule.MAX_LAYER);
    }

    public void putCorrelationTo(ResearchNode to, float correlation) {
        if (!this.correlationsTo.containsKey(to)) this.correlationsTo.put(to, correlation);
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