package com.atodium.iridynamics.api.recipe;

import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.commons.compress.utils.Lists;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

public class MaterialItemIngredient extends Ingredient {
    public static final Serializer SERIALIZER = new Serializer();

    private final MaterialEntry entry;

    private MaterialItemIngredient(MaterialEntry entry) {
        super(Stream.empty());
        this.entry = entry;
    }

    public static MaterialItemIngredient of(SolidShape shape, MaterialBase material) {
        return of(MaterialEntry.of(shape, material));
    }

    public static MaterialItemIngredient of(MaterialEntry entry) {
        return new MaterialItemIngredient(entry);
    }

    private Field getItemStacksField() {
        return ObfuscationReflectionHelper.findField(Ingredient.class, "f_43903_");
    }

    private ItemStack[] getItemStacks(Field field) {
        try {
            return (ItemStack[]) field.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ItemStack[] getItems() {
        Field field = this.getItemStacksField();
        List<ItemStack> list = Lists.newArrayList();
        MaterialEntry.getAllMaterialItemStacks(list, this.entry);
        try {
            field.set(this, list.toArray());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return this.getItemStacks(field);
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack == null) return false;
        return this.entry.equals(MaterialEntry.getItemMaterialEntry(stack));
    }

    @Override
    public JsonElement toJson() {
        return this.entry.toJson();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<MaterialItemIngredient> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer implements IIngredientSerializer<MaterialItemIngredient> {
        @Override
        public MaterialItemIngredient parse(FriendlyByteBuf buffer) {
            return new MaterialItemIngredient(MaterialEntry.fromByteBuf(buffer));
        }

        @Override
        public MaterialItemIngredient parse(JsonObject json) {
            return new MaterialItemIngredient(MaterialEntry.fromJson(json));
        }

        @Override
        public void write(FriendlyByteBuf buffer, MaterialItemIngredient ingredient) {
            ingredient.entry.toByteBuf(buffer);
        }
    }
}