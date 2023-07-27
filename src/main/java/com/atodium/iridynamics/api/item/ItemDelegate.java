package com.atodium.iridynamics.api.item;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.util.data.DataUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public abstract class ItemDelegate {
    public static final ItemDelegate EMPTY = of(Items.AIR);

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

    public static ItemDelegate of(String id) {
        if (id.startsWith(SimpleItemDelegate.PREFIX))
            return SimpleItemDelegate.fromString(id.substring(SimpleItemDelegate.PREFIX.length()));
        if (id.startsWith(MaterialItemDelegate.PREFIX))
            return MaterialItemDelegate.fromString(id.substring(SimpleItemDelegate.PREFIX.length()));
        return EMPTY;
    }

    private static class SimpleItemDelegate extends ItemDelegate {
        public static final String PREFIX = "simple_";

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

        public static ItemDelegate fromString(String id) {
            return new SimpleItemDelegate(DataUtil.readItemFromString(id));
        }

        @Override
        public String toString() {
            return PREFIX + this.uniqueName();
        }
    }

    private static class MaterialItemDelegate extends ItemDelegate {
        public static final String PREFIX = "material_";

        private final MaterialEntry entry;
        private final ResourceLocation name;

        private MaterialItemDelegate(SolidShape shape, MaterialBase material) {
            this.entry = MaterialEntry.of(shape, material);
            this.name = Iridynamics.rl("material_item/" + this.entry);
        }

        public MaterialItemDelegate(MaterialEntry entry) {
            this.entry = entry;
            this.name = Iridynamics.rl("material_item/" + this.entry);
        }

        @Override
        public ResourceLocation uniqueName() {
            return this.name;
        }

        @Override
        public ItemStack createStack(int count) {
            return MaterialEntry.getMaterialItemStack(this.entry, count);
        }

        public static ItemDelegate fromString(String id) {
            return new MaterialItemDelegate(MaterialEntry.fromString(new ResourceLocation(id).getPath().substring(14)));
        }

        @Override
        public String toString() {
            return PREFIX + this.uniqueName();
        }
    }
}