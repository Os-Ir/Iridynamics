package com.atodium.iridynamics.api.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class BlockBuilder extends AbstractBuilder<Block> {
    protected final Function<Block.Properties, Block> supplier;
    protected final Block.Properties properties;

    protected BlockBuilder(ModRegistry registry, String name, Function<Block.Properties, Block> supplier, Material material) {
        super(registry, name);
        this.supplier = supplier;
        this.properties = Block.Properties.of(material);
    }

    public static BlockBuilder builder(ModRegistry registry, String name, Function<Block.Properties, Block> supplier, Material material) {
        return new BlockBuilder(registry, name, supplier, material);
    }

    public BlockBuilder noCollission() {
        this.properties.noCollission();
        return this;
    }

    public BlockBuilder noOcclusion() {
        this.properties.noOcclusion();
        return this;
    }

    public BlockBuilder friction(float friction) {
        this.properties.friction(friction);
        return this;
    }

    public BlockBuilder speedFactor(float speedFactor) {
        this.properties.speedFactor(speedFactor);
        return this;
    }

    public BlockBuilder jumpFactor(float jumpFactor) {
        this.properties.jumpFactor(jumpFactor);
        return this;
    }

    public BlockBuilder sound(SoundType sound) {
        this.properties.sound(sound);
        return this;
    }

    public BlockBuilder lightLevel(ToIntFunction<BlockState> light) {
        this.properties.lightLevel(light);
        return this;
    }

    public BlockBuilder strength(float time, float resistance) {
        this.destroyTime(time);
        this.explosionResistance(resistance);
        return this;
    }

    public BlockBuilder instabreak() {
        this.properties.instabreak();
        return this;
    }

    public BlockBuilder strength(float value) {
        return this.strength(value, value);
    }

    public BlockBuilder randomTicks() {
        this.properties.randomTicks();
        return this;
    }

    public BlockBuilder dynamicShape() {
        this.properties.dynamicShape();
        return this;
    }

    public BlockBuilder noDrops() {
        this.properties.noDrops();
        return this;
    }

    public BlockBuilder lootFrom(Supplier<? extends Block> block) {
        this.properties.lootFrom(block);
        return this;
    }

    public BlockBuilder air() {
        this.properties.air();
        return this;
    }

    public BlockBuilder isValidSpawn(BlockBehaviour.StateArgumentPredicate<EntityType<?>> predicate) {
        this.properties.isValidSpawn(predicate);
        return this;
    }

    public BlockBuilder isRedstoneConductor(BlockBehaviour.StatePredicate predicate) {
        this.properties.isRedstoneConductor(predicate);
        return this;
    }

    public BlockBuilder isSuffocating(BlockBehaviour.StatePredicate predicate) {
        this.properties.isSuffocating(predicate);
        return this;
    }

    public BlockBuilder isViewBlocking(BlockBehaviour.StatePredicate predicate) {
        this.properties.isViewBlocking(predicate);
        return this;
    }

    public BlockBuilder hasPostProcess(BlockBehaviour.StatePredicate predicate) {
        this.properties.hasPostProcess(predicate);
        return this;
    }

    public BlockBuilder emissiveRendering(BlockBehaviour.StatePredicate predicate) {
        this.properties.emissiveRendering(predicate);
        return this;
    }

    public BlockBuilder requiresCorrectToolForDrops() {
        this.properties.requiresCorrectToolForDrops();
        return this;
    }

    public BlockBuilder color(MaterialColor color) {
        this.properties.color(color);
        return this;
    }

    public BlockBuilder destroyTime(float time) {
        this.properties.destroyTime(time);
        return this;
    }

    public BlockBuilder explosionResistance(float resistance) {
        this.properties.destroyTime(resistance);
        return this;
    }

    @Override
    public RegistryObject<Block> register() {
        super.register();
        return this.registry.getBlockRegistry().register(this.name, () -> this.supplier.apply(this.properties));
    }

    public RegistryObject<Block> registerWithItem(CreativeModeTab tab) {
        RegistryObject<Block> reg = this.register();
        this.registry.item(this.name + "_item", (properties) -> new BlockItem(reg.get(), properties)).tab(tab).register();
        return reg;
    }
}