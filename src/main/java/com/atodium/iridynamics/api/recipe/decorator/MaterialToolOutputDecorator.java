package com.atodium.iridynamics.api.recipe.decorator;

import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.tool.MaterialToolItem;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class MaterialToolOutputDecorator implements OutputDecorator {
    public static final Serializer SERIALIZER = new Serializer();

    private final Map<Integer, Pair<String, Integer>> function;

    public MaterialToolOutputDecorator(Map<Integer, Pair<String, Integer>> function) {
        this.function = function;
    }

    @Override
    public ItemStack apply(ItemStack stack, ItemStack[] input) {
        if (stack.getItem() instanceof MaterialToolItem) {
            for (Map.Entry<Integer, Pair<String, Integer>> entry : this.function.entrySet()) {
                ItemStack part = input[entry.getValue().getRight()];
                if (!MaterialEntry.containsMaterialEntry(part)) continue;
                MaterialToolItem.setToolMaterial(stack, entry.getKey(), entry.getValue().getLeft(), MaterialEntry.getItemMaterialEntry(part).material());
            }
        }
        return stack;
    }

    @Override
    public Serializer serializer() {
        return SERIALIZER;
    }

    public static class Serializer implements OutputDecorator.Serializer {
        @Override
        public OutputDecorator fromJson(JsonObject json) {
            JsonArray map = json.getAsJsonArray("map");
            int size = map.size();
            Map<Integer, Pair<String, Integer>> function = Maps.newHashMap();
            for (int i = 0; i < size; i++) {
                JsonObject entry = map.get(i).getAsJsonObject();
                function.put(entry.get("tool_part_index").getAsInt(), Pair.of(entry.get("tool_part_name").getAsString(), entry.get("source_index").getAsInt()));
            }
            return new MaterialToolOutputDecorator(function);
        }

        @Override
        public OutputDecorator fromNetwork(FriendlyByteBuf buf) {
            int size = buf.readInt();
            Map<Integer, Pair<String, Integer>> function = Maps.newHashMap();
            for (int i = 0; i < size; i++) function.put(buf.readInt(), Pair.of(buf.readUtf(), buf.readInt()));
            return new MaterialToolOutputDecorator(function);
        }

        @Override
        public void toNetwork(OutputDecorator decorator, FriendlyByteBuf buf) {
            if (decorator instanceof MaterialToolOutputDecorator tool) {
                buf.writeInt(tool.function.size());
                for (Map.Entry<Integer, Pair<String, Integer>> entry : tool.function.entrySet()) {
                    buf.writeInt(entry.getKey());
                    buf.writeUtf(entry.getValue().getLeft());
                    buf.writeInt(entry.getValue().getRight());
                }
            }
        }
    }
}