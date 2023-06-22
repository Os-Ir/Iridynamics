package com.atodium.iridynamics.api.recipe;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.recipe.impl.MaterialToolOutputDecorator;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ModOutputDecorators {
    public static final UnorderedRegistry<ResourceLocation, OutputDecorator.Serializer> SERIALIZERS = new UnorderedRegistry<>();

    public static void init() {
        SERIALIZERS.register(Iridynamics.rl("material_tool"), MaterialToolOutputDecorator.SERIALIZER);
    }

    public static void register(ResourceLocation id, OutputDecorator.Serializer serializer) {
        SERIALIZERS.register(id, serializer);
    }

    public static ResourceLocation getId(OutputDecorator.Serializer serializer) {
        if (SERIALIZERS.containsValue(serializer)) return SERIALIZERS.getKeyForValue(serializer);
        throw new IllegalArgumentException("This OutputDecorator Serializer is unregistered");
    }

    public static OutputDecorator.Serializer getSerializer(ResourceLocation id) {
        if (SERIALIZERS.containsKey(id)) return SERIALIZERS.get(id);
        throw new IllegalArgumentException("OutputDecorator Serializer id [ " + id + " ] is unregistered");
    }

    public static OutputDecorator fromNetwork(FriendlyByteBuf buf) {
        return getSerializer(buf.readResourceLocation()).fromNetwork(buf);
    }

    public static OutputDecorator fromJson(JsonObject json) {
        return getSerializer(new ResourceLocation(json.get("serializer").getAsString())).fromJson(json);
    }
}