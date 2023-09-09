package com.atodium.iridynamics.client.model;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.model.SpecialJsonModel;

public class ModSpecialModels {
    public static void init() {
        new SpecialJsonModel(Iridynamics.rl("item/axle_item"));
        new SpecialJsonModel(Iridynamics.rl("entity/bullet"));
    }
}