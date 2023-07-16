package com.atodium.iridynamics.api.module.research;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.gui.TextureArea;

public enum ResearchNodeType {
    ROOT("root", 0), MATH("math", 1), THEORY("theory", 2), IDEA("idea", 3), SUMMARY("summary", 4), APPLICATION("application", 5), COMBINATION("combination", 6);

    private final String typeName;
    private final TextureArea texture;

    ResearchNodeType(String typeName, int index) {
        this.typeName = typeName;
        this.texture = TextureArea.createTexture(Iridynamics.rl("textures/gui/research/research_table_icons.png"), index / 7.0f, 0.0f, 1.0f / 7.0f, 1.0f);
    }

    public static ResearchNodeType getTypeByName(String name) {
        for (ResearchNodeType type : ResearchNodeType.values()) if (type.typeName.equals(name)) return type;
        return null;
    }

    public String typeName() {
        return this.typeName;
    }

    public TextureArea texture() {
        return this.texture;
    }
}