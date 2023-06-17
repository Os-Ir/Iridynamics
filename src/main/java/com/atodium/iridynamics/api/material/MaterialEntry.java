package com.atodium.iridynamics.api.material;

import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import com.atodium.iridynamics.common.item.MaterialItem;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.function.Function;

public record MaterialEntry(SolidShape shape, MaterialBase material) {
    public static final UnorderedRegistry<MaterialEntry, Item> MATERIAL_ITEM = new UnorderedRegistry<>();
    public static final UnorderedRegistry<MaterialEntry, Function<Integer, ItemStack>> MATERIAL_ITEM_SUPPLIER = new UnorderedRegistry<>();

    public MaterialEntry {
        Validate.notNull(shape);
        Validate.notNull(material);
    }

    public static MaterialEntry of(SolidShape shape, MaterialBase material) {
        return new MaterialEntry(shape, material);
    }

    public static MaterialEntry fromJson(JsonObject json) {
        SolidShape shape = SolidShape.getShapeByName(json.get("shape").getAsString());
        MaterialBase material = MaterialBase.getMaterialByName(json.get("material").getAsString());
        return of(shape, material);
    }

    public static MaterialEntry fromByteBuf(FriendlyByteBuf buf) {
        SolidShape shape = SolidShape.getShapeByName(new String(buf.readByteArray()));
        MaterialBase material = MaterialBase.getMaterialByName(new String(buf.readByteArray()));
        return of(shape, material);
    }

    public static void register(SolidShape shape, MaterialBase material, Item item) {
        register(of(shape, material), item);
    }

    public static void register(MaterialEntry entry, Item item) {
        if (item instanceof MaterialItem materialItem) registerMaterialItem(entry, materialItem);
        else {
            MATERIAL_ITEM.register(entry, item);
            MATERIAL_ITEM_SUPPLIER.register(entry, (count) -> new ItemStack(item, count));
        }
    }

    public static void registerMaterialItem(SolidShape shape, MaterialBase material, MaterialItem materialItem) {
        MATERIAL_ITEM_SUPPLIER.register(of(shape, material), (count) -> materialItem.createItemStack(material, count));
    }

    public static void registerMaterialItem(MaterialEntry entry, MaterialItem materialItem) {
        MATERIAL_ITEM_SUPPLIER.register(entry, (count) -> materialItem.createItemStack(entry.material, count));
    }

    public static boolean containsMaterialEntry(ItemStack stack) {
        Item item = stack.getItem();
        return MaterialEntry.MATERIAL_ITEM.containsValue(item) || item instanceof MaterialItem;
    }

    public static MaterialEntry getItemMaterialEntry(ItemStack stack) {
        Item item = stack.getItem();
        MaterialEntry entry;
        if (MaterialEntry.MATERIAL_ITEM.containsValue(item)) entry = MaterialEntry.MATERIAL_ITEM.getKeyForValue(item);
        else entry = MaterialItem.getItemMaterialEntry(stack);
        return entry;
    }

    public static ItemStack getMaterialItemStack(SolidShape shape, MaterialBase material) {
        return getMaterialItemStack(of(shape, material), 1);
    }

    public static ItemStack getMaterialItemStack(SolidShape shape, MaterialBase material, int count) {
        return getMaterialItemStack(of(shape, material), count);
    }

    public static ItemStack getMaterialItemStack(MaterialEntry entry) {
        return getMaterialItemStack(entry, 1);
    }

    public static ItemStack getMaterialItemStack(MaterialEntry entry, int count) {
        if (MATERIAL_ITEM.containsKey(entry)) return new ItemStack(MATERIAL_ITEM.get(entry), count);
        if (MATERIAL_ITEM_SUPPLIER.containsKey(entry)) return MATERIAL_ITEM_SUPPLIER.get(entry).apply(count);
        return ItemStack.EMPTY;
    }

    public static void getAllMaterialItemStacks(List<ItemStack> list, MaterialEntry entry) {
        if (MATERIAL_ITEM.containsKey(entry)) list.add(new ItemStack(MATERIAL_ITEM.get(entry)));
        if (MATERIAL_ITEM_SUPPLIER.containsKey(entry)) list.add(MATERIAL_ITEM_SUPPLIER.get(entry).apply(1));
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("shape", this.shape.getName());
        json.addProperty("material", this.material.getName());
        return json;
    }

    public void toByteBuf(FriendlyByteBuf buf) {
        buf.writeByteArray(this.shape.getName().getBytes());
        buf.writeByteArray(this.material.getName().getBytes());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MaterialEntry entry)) return false;
        return this.shape == entry.shape && this.material == entry.material;
    }

    @Override
    public int hashCode() {
        return this.shape.hashCode() * 97 ^ this.material.hashCode();
    }

    @Override
    public String toString() {
        return this.shape.getName() + "/" + this.material.getName();
    }
}