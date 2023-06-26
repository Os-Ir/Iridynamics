package com.atodium.iridynamics.api.recipe.container;

import com.atodium.iridynamics.api.module.ToolModule;
import com.atodium.iridynamics.api.tool.IToolInfo;
import com.atodium.iridynamics.api.tool.ToolItem;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class ToolInventoryContainer extends InventoryContainer {
    private final ItemStack[] tools;

    public ToolInventoryContainer(int size, int toolSize) {
        super(size);
        this.tools = new ItemStack[toolSize];
        Arrays.fill(this.tools, ItemStack.EMPTY);
    }

    public boolean validateToolIndex(int index) {
        return 0 <= index && index < this.tools.length;
    }

    public void setTool(int index, ItemStack tool) {
        if (this.validateToolIndex(index) && ToolModule.isTool(tool)) this.tools[index] = tool;
    }

    public int getToolCount() {
        return this.tools.length;
    }

    public ItemStack[] getAllToolItemStacks() {
        return this.tools;
    }

    public ItemStack getToolItemStack(int index) {
        if (this.validateToolIndex(index)) return this.tools[index];
        return ItemStack.EMPTY;
    }

    public ToolItem getToolItem(int index) {
        if (this.validateToolIndex(index) && this.tools[index].getItem() instanceof ToolItem toolItem) return toolItem;
        return null;
    }

    public IToolInfo getTool(int index) {
        return ToolModule.getItemToolInfo(this.tools[index]);
    }
}