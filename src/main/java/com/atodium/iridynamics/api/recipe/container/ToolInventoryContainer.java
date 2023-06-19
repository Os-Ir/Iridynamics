package com.atodium.iridynamics.api.recipe.container;

import com.atodium.iridynamics.api.tool.IToolInfo;

public class ToolInventoryContainer extends InventoryContainer {
    private final IToolInfo[] tools;

    public ToolInventoryContainer(int size, int toolSize) {
        super(size);
        this.tools = new IToolInfo[toolSize];
    }

    public boolean validateToolIndex(int index) {
        return 0 <= index && index < this.tools.length;
    }

    public void setTool(int index, IToolInfo info) {
        if (this.validateToolIndex(index)) this.tools[index] = info;
    }

    public int getToolCount() {
        return this.tools.length;
    }

    public IToolInfo[] getAllTools() {
        return this.tools;
    }

    public IToolInfo getTool(int index) {
        if (this.validateToolIndex(index)) return this.tools[index];
        return null;
    }
}