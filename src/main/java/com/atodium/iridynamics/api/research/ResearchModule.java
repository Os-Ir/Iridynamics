package com.atodium.iridynamics.api.research;

import com.atodium.iridynamics.api.capability.ResearchCapability;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class ResearchModule {
    public static final UnorderedRegistry<String, String> NETWORK_TYPES = new UnorderedRegistry<>();
    public static final UnorderedRegistry<String, ResearchNode> NODES = new UnorderedRegistry<>();
    public static final int MAX_LAYER = 100000;

    public static void init() {
        NETWORK_TYPES.register("agricultural_age", "agricultural_age_root");
    }

    public static void registerNetworkType(String name, String root) {
        NETWORK_TYPES.register(name, root);
    }

    public static void registerResearchNode(ResearchNode node) {
        NODES.register(node.name(), node);
    }

    public static ResearchNode getResearchNode(String name) {
        return NODES.get(name);
    }

    public static Map<String, ResearchNetwork> getPlayerAllResearchNetworks(Player player) {
        return player.getCapability(ResearchCapability.RESEARCH).orElseThrow(NullPointerException::new).allNetworks();
    }

    public static ResearchNetwork getPlayerResearchNetwork(Player player, String category) {
        return player.getCapability(ResearchCapability.RESEARCH).orElseThrow(NullPointerException::new).network(category);
    }
}