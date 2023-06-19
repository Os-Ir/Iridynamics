package com.atodium.iridynamics.api.recipe;

import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientIndex {
    public static final IngredientIndex EMPTY = new IngredientIndex(Ingredient.EMPTY, 0);

    private final Ingredient ingredient;
    private final int count, type;

    private IngredientIndex(Ingredient ingredient, int count) {
        this(ingredient, count, ingredient instanceof MaterialItemIngredient ? 1 : 0);
    }

    private IngredientIndex(Ingredient ingredient, int count, int type) {
        this.ingredient = ingredient;
        this.count = Math.max(count, 0);
        this.type = type;
    }

    public static IngredientIndex from(Ingredient ingredient, int count) {
        IngredientIndex result = new IngredientIndex(ingredient, count);
        return result.isEmpty() ? EMPTY : result;
    }

    public static IngredientIndex from(Ingredient ingredient, int count, int type) {
        IngredientIndex result = new IngredientIndex(ingredient, count, type);
        return result.isEmpty() ? EMPTY : result;
    }

    public static IngredientIndex from(ItemStack stack) {
        IngredientIndex result = new IngredientIndex(Ingredient.of(stack), stack.getCount(), 0);
        return result.isEmpty() ? EMPTY : result;
    }

    public static IngredientIndex from(ItemStack stack, int count) {
        IngredientIndex result = new IngredientIndex(Ingredient.of(stack), count, 0);
        return result.isEmpty() ? EMPTY : result;
    }

    public static IngredientIndex from(SolidShape shape, MaterialBase material) {
        return from(shape, material, 1);
    }

    public static IngredientIndex from(SolidShape shape, MaterialBase material, int count) {
        IngredientIndex result = new IngredientIndex(MaterialItemIngredient.of(shape, material), count, 1);
        return result.isEmpty() ? EMPTY : result;
    }

    public static IngredientIndex from(MaterialEntry entry) {
        return from(entry, 1);
    }

    public static IngredientIndex from(MaterialEntry entry, int count) {
        IngredientIndex result = new IngredientIndex(MaterialItemIngredient.of(entry), count, 1);
        return result.isEmpty() ? EMPTY : result;
    }

    public static IngredientIndex fromNetwork(FriendlyByteBuf buf) {
        int type = buf.readInt();
        if (type == 1) return from(MaterialItemIngredient.SERIALIZER.parse(buf), buf.readInt());
        return from(Ingredient.fromNetwork(buf), buf.readInt());
    }

    public static IngredientIndex fromJson(JsonObject json) {
        int type = json.get("type").getAsInt();
        int count = json.has("count") ? json.get("count").getAsInt() : 1;
        if (type == 1)
            return from(MaterialItemIngredient.SERIALIZER.parse(json.get("ingredient").getAsJsonObject()), count);
        return from(Ingredient.fromJson(json.get("ingredient")), count);
    }

    public boolean isEmpty() {
        return this == EMPTY || this.count == 0 || this.ingredient.isEmpty();
    }

    public boolean testItem(ItemStack stack) {
        return this.ingredient.test(stack);
    }

    public boolean test(ItemStack stack) {
        return this.ingredient.test(stack) && stack.getCount() >= this.count;
    }

    public boolean testEqual(ItemStack stack) {
        return this.ingredient.test(stack) && stack.getCount() == this.count;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public int getCount() {
        return this.count;
    }

    public int getType() {
        return this.type;
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeInt(this.type);
        this.ingredient.toNetwork(buf);
        buf.writeInt(this.count);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", this.type);
        json.add("ingredient", this.ingredient.toJson());
        json.addProperty("count", this.count);
        return json;
    }
}