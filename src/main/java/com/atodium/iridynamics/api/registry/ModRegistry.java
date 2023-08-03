package com.atodium.iridynamics.api.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ModRegistry {
    private final String modid;
    private final DeferredRegister<Item> itemRegistry;
    private final DeferredRegister<Block> blockRegistry;
    private final DeferredRegister<Fluid> fluidRegistry;
    private final DeferredRegister<EntityType<?>> entityRegistry;
    private final DeferredRegister<BlockEntityType<?>> blockEntityRegistry;
    private final DeferredRegister<RecipeType<?>> recipeTypeRegistry;
    private final DeferredRegister<RecipeSerializer<?>> recipeSerializerRegistry;
    private final DeferredRegister<Feature<?>> featureRegistry;
    private final DeferredRegister<ConfiguredFeature<?, ?>> configuredFeatureRegistry;
    private final DeferredRegister<PlacementModifierType<?>> placementModifierRegister;

    public ModRegistry(String modid) {
        this.modid = modid;
        this.itemRegistry = DeferredRegister.create(ForgeRegistries.ITEMS, modid);
        this.blockRegistry = DeferredRegister.create(ForgeRegistries.BLOCKS, modid);
        this.fluidRegistry = DeferredRegister.create(ForgeRegistries.FLUIDS, modid);
        this.entityRegistry = DeferredRegister.create(ForgeRegistries.ENTITIES, modid);
        this.blockEntityRegistry = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, modid);
        this.recipeTypeRegistry = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, modid);
        this.recipeSerializerRegistry = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, modid);
        this.featureRegistry = DeferredRegister.create(ForgeRegistries.FEATURES, modid);
        this.configuredFeatureRegistry = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, modid);
        this.placementModifierRegister = DeferredRegister.create(Registry.PLACEMENT_MODIFIER_REGISTRY, modid);
    }

    public void init(IEventBus bus) {
        this.itemRegistry.register(bus);
        this.blockRegistry.register(bus);
        this.fluidRegistry.register(bus);
        this.entityRegistry.register(bus);
        this.blockEntityRegistry.register(bus);
        this.recipeTypeRegistry.register(bus);
        this.recipeSerializerRegistry.register(bus);
        this.featureRegistry.register(bus);
        this.configuredFeatureRegistry.register(bus);
        this.placementModifierRegister.register(bus);
    }

    public String getModid() {
        return this.modid;
    }

    public DeferredRegister<Item> getItemRegistry() {
        return this.itemRegistry;
    }

    public DeferredRegister<Block> getBlockRegistry() {
        return this.blockRegistry;
    }

    public DeferredRegister<Fluid> getFluidRegistry() {
        return this.fluidRegistry;
    }

    public DeferredRegister<EntityType<?>> getEntityRegistry() {
        return this.entityRegistry;
    }

    public DeferredRegister<BlockEntityType<?>> getBlockEntityRegistry() {
        return this.blockEntityRegistry;
    }

    public DeferredRegister<RecipeType<?>> getRecipeTypeRegistry() {
        return this.recipeTypeRegistry;
    }

    public DeferredRegister<RecipeSerializer<?>> getRecipeSerializerRegistry() {
        return this.recipeSerializerRegistry;
    }

    public DeferredRegister<Feature<?>> getFeatureRegistry() {
        return this.featureRegistry;
    }

    public DeferredRegister<ConfiguredFeature<?, ?>> getConfiguredFeatureRegistry() {
        return this.configuredFeatureRegistry;
    }

    public DeferredRegister<PlacementModifierType<?>> getPlacementModifierRegister() {
        return this.placementModifierRegister;
    }

    public ItemBuilder item(String name, Function<Item.Properties, Item> supplier) {
        return ItemBuilder.builder(this, name, supplier);
    }

    public ItemBuilder item(String name, Function<Item.Properties, Item> supplier, Item.Properties properties) {
        return ItemBuilder.builder(this, name, supplier, properties);
    }

    public BlockBuilder block(String name, Function<BlockBehaviour.Properties, Block> supplier, Material material) {
        return BlockBuilder.builder(this, name, supplier, material);
    }

    public FluidBuilder fluid(String name, Supplier<Fluid> supplier) {
        return FluidBuilder.builder(this, name, supplier);
    }

    public <H extends Entity> EntityBuilder<H> entity(String name, Supplier<EntityType<H>> supplier) {
        return EntityBuilder.builder(this, name, supplier);
    }

    public <H extends BlockEntity> BlockEntityBuilder<H> blockEntity(String name, Supplier<BlockEntityType<H>> supplier) {
        return BlockEntityBuilder.builder(this, name, supplier);
    }

    public <C extends Container, R extends Recipe<C>> RecipeTypeBuilder<C, R> recipeType(String name, Supplier<RecipeType<R>> supplier) {
        return RecipeTypeBuilder.builder(this, name, supplier);
    }

    public <C extends Container, R extends Recipe<C>> RecipeSerializerBuilder<C, R> recipeSerializer(String name, Supplier<RecipeSerializer<R>> supplier) {
        return RecipeSerializerBuilder.builder(this, name, supplier);
    }

    public <H extends FeatureConfiguration> FeatureBuilder<H> feature(String name, Function<Codec<H>, Feature<H>> supplier, Codec<H> codec) {
        return FeatureBuilder.builder(this, name, supplier, codec);
    }

    public <H extends FeatureConfiguration, F extends Feature<H>> ConfiguredFeatureBuilder<H, F> configuredFeature(String name, Supplier<ConfiguredFeature<H, F>> supplier) {
        return ConfiguredFeatureBuilder.builder(this, name, supplier);
    }

    public <T extends PlacementModifier> PlacementModifierBuilder<T> placementModifier(String name, Supplier<PlacementModifierType<T>> supplier) {
        return PlacementModifierBuilder.builder(this, name, supplier);
    }
}