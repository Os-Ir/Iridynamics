package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.api.module.research.ResearchNetwork;

import java.util.Map;

public interface IResearch {
    Map<String, ResearchNetwork> allNetworks();

    ResearchNetwork network(String category);
}