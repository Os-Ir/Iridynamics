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

    private final SolidShape shape;
    private final MaterialBase material;

    private MaterialItemIngredient(SolidShape shape, MaterialBase material) {
        super(Stream.empty());
        if (shape == null && material == null)
            throw new IllegalArgumentException("Shape and material of MaterialItemIngredient can not be null at the same time");
        this.shape = shape;
        this.material = material;
    }

    public static MaterialItemIngredient of(SolidShape shape, MaterialBase material) {
        return new MaterialItemIngredient(shape, material);
    }

    public static MaterialItemIngredient of(MaterialEntry entry) {
        return of(entry.shape(), entry.material());
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
        MaterialEntry.getAllMaterialItemStacks(list, this.shape, this.material);
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
        if (!MaterialEntry.containsMaterialEntry(stack)) return false;
        MaterialEntry entry = MaterialEntry.getItemMaterialEntry(stack);
        return (this.shape == null || this.shape == entry.shape()) && (this.material == null || this.material == entry.material());
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        if (this.shape != null) json.addProperty("shape", this.shape.getName());
        if (this.material != null) json.addProperty("material", this.material.getName());
        return json;
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
        public MaterialItemIngredient parse(FriendlyByteBuf buf) {
            SolidShape shape = null;
            MaterialBase material = null;
            if (buf.readBoolean()) shape = SolidShape.getShapeByName(buf.readUtf());
            if (buf.readBoolean()) material = MaterialBase.getMaterialByName(buf.readUtf());
            return new MaterialItemIngredient(shape, material);
        }

        @Override
        public MaterialItemIngredient parse(JsonObject json) {
            SolidShape shape = null;
            MaterialBase material = null;
            if (json.has("shape")) shape = SolidShape.getShapeByName(json.get("shape").getAsString());
            if (json.has("material")) material = MaterialBase.getMaterialByName(json.get("material").getAsString());
            return new MaterialItemIngredient(shape, material);
        }

        @Override
        public void write(FriendlyByteBuf buf, MaterialItemIngredient ingredient) {
            buf.writeBoolean(ingredient.shape != null);
            if (ingredient.shape != null) buf.writeUtf(ingredient.shape.getName());
            buf.writeBoolean(ingredient.material != null);
            if (ingredient.material != null) buf.writeUtf(ingredient.material.getName());
        }
    }
}