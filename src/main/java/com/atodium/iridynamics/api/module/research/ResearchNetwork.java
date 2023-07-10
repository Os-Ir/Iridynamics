package com.atodium.iridynamics.api.module.research;

import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResearchNetwork {
    public static final UnorderedRegistry<String, ResearchNode> NODES = new UnorderedRegistry<>();

    private final Object2FloatMap<ResearchNode> nodeProficiencies;
    private final Int2ObjectMap<List<ResearchNode>> layerNodes;
    private final Set<ResearchNode> unlockedNodes;
    private final Map<ResearchNode, Vector3f> position;

    public ResearchNetwork() {
        this.nodeProficiencies = new Object2FloatOpenHashMap<>();
        for (ResearchNode node : NODES.values()) this.nodeProficiencies.put(node, 0.0f);
        this.layerNodes = new Int2ObjectOpenHashMap<>();
        this.unlockedNodes = Sets.newHashSet();
        this.position = Maps.newHashMap();
    }

    public static void registerResearchNode(ResearchNode node) {
        NODES.register(node.name(), node);
    }

    public void updateNodeRelation() {
        for (ResearchNode node : NODES.values())
            for (Object2FloatMap.Entry<ResearchNode> entry : node.allCorrelationsTo().object2FloatEntrySet()) {
                ResearchNode to = entry.getKey();
                to.putCorrelationFrom(node, entry.getFloatValue());
                to.setLayer(Math.min(to.layer(), node.layer() + 1));
            }
        this.layerNodes.clear();
        for (ResearchNode node : NODES.values()) {
            int layer = node.layer();
            if (!this.layerNodes.containsKey(layer)) this.layerNodes.put(layer, Lists.newArrayList());
            this.layerNodes.get(layer).add(node);
        }
    }

    public void updatePosition() {
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

    public float unlockCoefficient(ResearchNode node) {
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
        if (this.nodeProficiencies.apply(node) >= proficiency) return;
        this.nodeProficiencies.put(node, proficiency);
        node.allCorrelationsTo().forEach((to, correlation) -> {
            float unlockCoefficient = this.unlockCoefficient(to);
            boolean flag = false;
            if (!this.unlockedNodes.contains(to) && unlockCoefficient >= to.minUnlockCoefficient()) {
                this.unlockedNodes.add(to);
                flag = true;
            }
            if (flag) {
                this.updateNodeRelation();
                this.updatePosition();
            }
        });
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
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        ListTag proficienciesTag = tag.getList("proficiencies", Tag.TAG_COMPOUND);
        this.nodeProficiencies.clear();
        for (int i = 0; i < proficienciesTag.size(); i++) {
            CompoundTag proficiencyTag = proficienciesTag.getCompound(i);
            String name = proficiencyTag.getString("name");
            if (NODES.containsKey(name))
                this.nodeProficiencies.put(NODES.get(name), proficiencyTag.getFloat("proficiency"));
        }
        ListTag unlockedNodesTag = tag.getList("unlockedNodes", Tag.TAG_STRING);
        for (int i = 0; i < unlockedNodesTag.size(); i++) {
            String name = unlockedNodesTag.getString(i);
            if (NODES.containsKey(name)) this.unlockedNodes.add(NODES.get(name));
        }
        this.updateNodeRelation();
        this.updatePosition();
    }
}