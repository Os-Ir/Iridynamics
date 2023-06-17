package com.atodium.iridynamics.api.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class ItemBuilder extends AbstractBuilder<Item> {
    protected final Function<Item.Properties, Item> supplier;
    protected final Item.Properties properties;

    protected ItemBuilder(ModRegistry registry, String name, Function<Item.Properties, Item> supplier) {
        this(registry, name, supplier, new Item.Properties());
    }

    protected ItemBuilder(ModRegistry registry, String name, Function<Item.Properties, Item> supplier, Item.Properties properties) {
        super(registry, name);
        this.supplier = supplier;
        this.properties = properties;
    }

    public static ItemBuilder builder(ModRegistry registry, String name, Function<Item.Properties, Item> supplier) {
        return new ItemBuilder(registry, name, supplier);
    }

    public static ItemBuilder builder(ModRegistry registry, String name, Function<Item.Properties, Item> supplier, Item.Properties properties) {
        return new ItemBuilder(registry, name, supplier, properties);
    }

    public ItemBuilder tab(CreativeModeTab tab) {
        this.properties.tab(tab);
        return this;
    }

    public ItemBuilder stacksTo(int maxStackSize) {
        this.properties.stacksTo(maxStackSize);
        return this;
    }

    @Override
    public RegistryObject<Item> register() {
        super.register();
        return this.registry.getItemRegistry().register(this.name, () -> this.supplier.apply(this.properties));
    }
}