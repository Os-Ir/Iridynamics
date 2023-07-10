package com.atodium.iridynamics.api.module.research;

public enum ResearchNodeType {
    THEORY("theory"), IDEA("idea"), SUMMARY("summary"), APPLICATION("application"), COMBINATION("combination");

    private final String typeName;

    ResearchNodeType(String typeName) {
        this.typeName = typeName;
    }

    public static ResearchNodeType getTypeByName(String name) {
        for (ResearchNodeType type : ResearchNodeType.values()) if (type.typeName.equals(name)) return type;
        return null;
    }

    public String typeName() {
        return this.typeName;
    }
}