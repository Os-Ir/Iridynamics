package com.atodium.iridynamics.api.material;

import com.atodium.iridynamics.Iridynamics;
import net.minecraft.client.resources.language.I18n;

public enum Phase {
    SOLID("solid"), LIQUID("liquid"), GAS("gas"), PLASMA("plasma"), SUPERCRITICAL("supercritical");

    private final String name;

    Phase(String name) {
        this.name = name;
    }

    public String getStateName() {
        return this.name;
    }

    public String getStateLocalizedName() {
        return I18n.get(Iridynamics.MODID + ".material.phase." + this.name);
    }
}