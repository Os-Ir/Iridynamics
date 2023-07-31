package com.atodium.iridynamics.api.rotate;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class RotateModule {
    public static final UnorderedRegistry<ResourceLocation, IRotateNode.Serializer<?>> SERIALIZERS = new UnorderedRegistry<>();

    public static void init() {
        SERIALIZERS.register(Iridynamics.rl("axle"), Axle.SERIALIZER);
        SERIALIZERS.register(Iridynamics.rl("gearbox"), Gearbox.SERIALIZER);
        SERIALIZERS.register(Iridynamics.rl("escapement"), Escapement.SERIALIZER);
        SERIALIZERS.register(Iridynamics.rl("flywheel"), Flywheel.SERIALIZER);
        SERIALIZERS.register(Iridynamics.rl("handle"), Handle.SERIALIZER);
        SERIALIZERS.register(Iridynamics.rl("machine"), Machine.SERIALIZER);
        SERIALIZERS.register(Iridynamics.rl("clockwork"), Clockwork.SERIALIZER);
    }

    public static CompoundTag writeRotateNode(IRotateNode node) {
        CompoundTag tag = new CompoundTag();
        IRotateNode.Serializer<?> serializer = node.serializer();
        tag.putString("id", RotateModule.SERIALIZERS.getKeyForValue(serializer).toString());
        tag.put("node", serializer.serializeRaw(node));
        return tag;
    }

    public static IRotateNode readRotateNode(CompoundTag tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("id"));
        IRotateNode.Serializer<?> serializer = SERIALIZERS.get(id);
        return serializer.deserialize(tag.getCompound("node"));
    }

    public static CompoundTag writeSyncTag(IRotateNode node) {
        return node.serializer().writeSyncTagRaw(node);
    }

    public static void readSyncTag(IRotateNode node, CompoundTag tag) {
        node.serializer().readSyncTagRaw(node, tag);
    }

    public static void tick(ServerLevel level) {
        RotateSavedData.get(level).tryTick(level, level.getGameTime());
    }

    public static void addRotateBlock(ServerLevel level, BlockPos pos, IRotateNode node) {
        RotateSavedData.get(level).addNode(pos, node);
    }

    public static void removeRotateBlock(ServerLevel level, BlockPos pos) {
        RotateSavedData.get(level).removeNode(pos);
    }

    public static void updateRotateBlock(ServerLevel level, BlockPos pos, IRotateNode node) {
        RotateSavedData data = RotateSavedData.get(level);
        data.removeNode(pos);
        data.addNode(pos, node);
    }

    public static Axle axle(Direction direction, double inertia, double friction) {
        return new Axle(direction, inertia, friction);
    }

    public static Gearbox gearbox(Direction directionA, Direction directionB, int gearA, int gearB) {
        return new Gearbox(directionA, directionB, gearA, gearB);
    }

    public static Escapement escapement(Direction direction) {
        return new Escapement(direction);
    }

    public static Flywheel flywheel(Direction direction) {
        return new Flywheel(direction);
    }

    public static Handle handle(Direction direction) {
        return new Handle(direction);
    }

    public static Clockwork clockwork(Direction direction) {
        return new Clockwork(direction);
    }

    public static Machine machine(Direction direction) {
        return new Machine(direction);
    }
}