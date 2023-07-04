package com.atodium.iridynamics.api.util.data;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class ItemDelegate {
    public abstract ResourceLocation uniqueName();

    public abstract ItemStack createStack(int count);

    public ItemStack createStack() {
        return this.createStack(1);
    }

    public boolean is(ItemStack stack) {
        return this.uniqueName().equals(of(stack).uniqueName());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemDelegate delegate)) return false;
        return this.uniqueName().equals(delegate.uniqueName());
    }

    @Override
    public int hashCode() {
        return this.uniqueName().hashCode();
    }

    public static ItemDelegate of(Item item) {
        return MaterialEntry.MATERIAL_ITEM.containsValue(item) ? new MaterialItemDelegate(MaterialEntry.MATERIAL_ITEM.getKeyForValue(item)) : new SimpleItemDelegate(item);
    }

    public static ItemDelegate of(ItemStack stack) {
        return MaterialEntry.containsMaterialEntry(stack) ? new MaterialItemDelegate(MaterialEntry.getItemMaterialEntry(stack)) : new SimpleItemDelegate(stack.getItem());
    }

    public static ItemDelegate of(SolidShape shape, MaterialBase material) {
        return new MaterialItemDelegate(shape, material);
    }

    private static class SimpleItemDelegate extends ItemDelegate {
        private final Item item;

        private SimpleItemDelegate(Item item) {
            this.item = item;
        }

        @Override
        public ResourceLocation uniqueName() {
            return this.item.getRegistryName();
        }

        @Override
        public ItemStack createStack(int count) {
            return new ItemStack(this.item, count);
        }
    }

    private static class MaterialItemDelegate extends ItemDelegate {
        private final MaterialEntry entry;
        private final ResourceLocation name;

        private MaterialItemDelegate(SolidShape shape, MaterialBase material) {
            this.entry = MaterialEntry.of(shape, material);
            this.name = Iridynamics.rl("material_item/" + shape.getName() + "/" + material.getName());
        }

        public MaterialItemDelegate(MaterialEntry entry) {
            this.entry = entry;
            this.name = Iridynamics.rl("material_item/" + entry.shape().getName() + "/" + entry.material().getName());
        }

        @Override
        public ResourceLocation uniqueName() {
            return this.name;
        }

        @Override
        public ItemStack createStack(int count) {
            return MaterialEntry.getMaterialItemStack(this.entry, count);
        }
    }
}