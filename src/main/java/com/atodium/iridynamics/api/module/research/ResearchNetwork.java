package com.atodium.iridynamics.api.module.research;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResearchNetwork {
    private final Object2FloatMap<ResearchNode> nodeProficiencies;
    private final Int2ObjectMap<List<ResearchNode>> layerNodes;
    private final Set<ResearchNode> unlockedNodes;
    private final Map<ResearchNode, Vector3f> position;
    private ResearchNode root;

    public ResearchNetwork(String root) {
        this.nodeProficiencies = new Object2FloatOpenHashMap<>();
        for (ResearchNode node : ResearchModule.NODES.values()) this.nodeProficiencies.put(node, 0.0f);
        this.layerNodes = new Int2ObjectOpenHashMap<>();
        this.unlockedNodes = Sets.newHashSet();
        this.position = Maps.newHashMap();
        this.root = ResearchModule.NODES.get(root);
        this.updateNode(this.root, 1.0f);
    }

    private void updateNodeRelation() {
        Object2BooleanMap<ResearchNode> valid = new Object2BooleanOpenHashMap<>();
        for (ResearchNode node : this.unlockedNodes) {
            node.setLayer(ResearchModule.MAX_LAYER);
            valid.put(node, true);
        }
        for (ResearchNode node : this.root.allCorrelationsTo().keySet()) node.setLayer(1);
        this.root.setLayer(0);
        valid.put(this.root, false);
        for (int i = 1; i < this.unlockedNodes.size(); i++) {
            int minLayer = ResearchModule.MAX_LAYER;
            ResearchNode u = this.root;
            for (ResearchNode node : this.unlockedNodes)
                if (valid.apply(node) && node.layer() < minLayer) {
                    u = node;
                    minLayer = node.layer();
                }
            valid.put(u, false);
            for (ResearchNode node : this.unlockedNodes)
                if (valid.apply(node) && u.allCorrelationsTo().containsKey(node) && u.layer() + 1 < node.layer())
                    node.setLayer(u.layer() + 1);
        }
        this.layerNodes.clear();
        for (ResearchNode node : this.unlockedNodes) {
            int layer = node.layer();
            if (!this.layerNodes.containsKey(layer)) this.layerNodes.put(layer, Lists.newArrayList());
            this.layerNodes.get(layer).add(node);
        }
    }

    private void updatePosition() {
        for (int layer : this.layerNodes.keySet()) {
            List<ResearchNode> nodes = this.layerNodes.get(layer);
            int count = nodes.size();
            for (int i = 0; i < count; i++)
                this.position.put(nodes.get(i), new Vector3f((float) (Math.sin(((double) i) / count) * layer), (float) (-Math.cos(((double) i) / count) * layer), 0.0f));
        }
    }

    public Map<ResearchNode, Vector3f> position() {
        return this.position;
    }

    public float proficiency(ResearchNode node) {
        return this.nodeProficiencies.apply(node);
    }

    private float unlockCoefficient(ResearchNode node) {
        float maxCoefficient = 0.0f;
        float coefficient = 0.0f;
        for (Object2FloatMap.Entry<ResearchNode> entry : node.allCorrelationsFrom().object2FloatEntrySet()) {
            float correlation = entry.getFloatValue();
            maxCoefficient += correlation;
            coefficient += correlation * this.nodeProficiencies.apply(entry.getKey());
        }
        return coefficient / maxCoefficient;
    }

    public void updateNode(ResearchNode node, float proficiency) {
        this.unlockedNodes.add(node);
        if (this.nodeProficiencies.containsKey(node) && this.nodeProficiencies.apply(node) >= proficiency) return;
        this.nodeProficiencies.put(node, proficiency);
        node.allCorrelationsTo().forEach((to, correlation) -> {
            float unlockCoefficient = this.unlockCoefficient(to);
            if (!this.unlockedNodes.contains(to) && unlockCoefficient >= to.minUnlockCoefficient())
                this.unlockedNodes.add(to);
        });
        this.updateNodeRelation();
        this.updatePosition();
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        ListTag proficienciesTag = new ListTag();
        for (Object2FloatMap.Entry<ResearchNode> entry : this.nodeProficiencies.object2FloatEntrySet()) {
            CompoundTag proficiencyTag = new CompoundTag();
            proficiencyTag.putString("name", entry.getKey().name());
            proficiencyTag.putFloat("proficiency", entry.getFloatValue());
            proficienciesTag.add(proficiencyTag);
        }
        tag.put("proficiencies", proficienciesTag);
        ListTag unlockedNodesTag = new ListTag();
        for (ResearchNode node : this.unlockedNodes) unlockedNodesTag.add(StringTag.valueOf(node.name()));
        tag.put("unlockedNodes", unlockedNodesTag);
        tag.putString("root", this.root.name());
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        ListTag proficienciesTag = tag.getList("proficiencies", Tag.TAG_COMPOUND);
        this.nodeProficiencies.clear();
        for (int i = 0; i < proficienciesTag.size(); i++) {
            CompoundTag proficiencyTag = proficienciesTag.getCompound(i);
            String name = proficiencyTag.getString("name");
            if (ResearchModule.NODES.containsKey(name))
                this.nodeProficiencies.put(ResearchModule.NODES.get(name), proficiencyTag.getFloat("proficiency"));
        }
        ListTag unlockedNodesTag = tag.getList("unlockedNodes", Tag.TAG_STRING);
        for (int i = 0; i < unlockedNodesTag.size(); i++) {
            String name = unlockedNodesTag.getString(i);
            if (ResearchModule.NODES.containsKey(name)) this.unlockedNodes.add(ResearchModule.NODES.get(name));
        }
        this.root = ResearchModule.NODES.get(tag.getString("root"));
        this.updateNodeRelation();
        this.updatePosition();
    }
}