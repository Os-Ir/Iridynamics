package com.atodium.iridynamics.common.item;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.ModCreativeTabs;
import com.atodium.iridynamics.api.heat.FuelInfo;
import com.atodium.iridynamics.api.material.MaterialPhysicalInfo;
import com.atodium.iridynamics.api.material.ModMaterials;
import com.atodium.iridynamics.api.material.ModSolidShapes;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.tool.MaterialToolBase;
import com.atodium.iridynamics.api.tool.MaterialToolItem;
import com.atodium.iridynamics.api.tool.ToolItem;
import com.atodium.iridynamics.common.blockEntity.PileBlockEntity;
import com.atodium.iridynamics.common.tool.ToolChisel;
import com.atodium.iridynamics.common.tool.ToolHammer;
import com.atodium.iridynamics.common.tool.ToolIgniter;
import com.atodium.iridynamics.common.tool.ToolKnife;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final RegistryObject<Item> GUN = Iridynamics.REGISTRY.item("gun", GunItem::new).tab(ModCreativeTabs.ITEM).register();
    public static final RegistryObject<Item> WOOD_BRICK = Iridynamics.REGISTRY.item("wood_brick", Item::new).tab(ModCreativeTabs.ITEM).register();
    public static final RegistryObject<Item> CRUSHED_STONE = Iridynamics.REGISTRY.item("crushed_stone", Item::new).tab(ModCreativeTabs.ITEM).register();
    public static final RegistryObject<Item> GRASS = Iridynamics.REGISTRY.item("grass", Item::new).tab(ModCreativeTabs.ITEM).register();
    public static final RegistryObject<Item> DRIED_GRASS = Iridynamics.REGISTRY.item("dried_grass", Item::new).tab(ModCreativeTabs.ITEM).register();
    public static final RegistryObject<Item> MOLD = Iridynamics.REGISTRY.item("mold", Item::new).stacksTo(1).tab(ModCreativeTabs.ITEM).register();
    public static final RegistryObject<Item> MOLD_TOOL = Iridynamics.REGISTRY.item("mold_tool", MoldToolItem::new).stacksTo(1).tab(ModCreativeTabs.ITEM).register();
    public static final RegistryObject<Item> SMALL_CRUCIBLE = Iridynamics.REGISTRY.item("small_crucible", Item::new).stacksTo(1).tab(ModCreativeTabs.ITEM).register();

    public static final RegistryObject<Item> IGNITER = Iridynamics.REGISTRY.item("igniter", (properties) -> new ToolItem(properties, ToolIgniter.INSTANCE)).stacksTo(1).tab(ModCreativeTabs.ITEM).register();
    public static final RegistryObject<Item> HAMMER = Iridynamics.REGISTRY.item("tool/hammer", (properties) -> new MaterialToolItem(properties, ToolHammer.INSTANCE)).stacksTo(1).tab(ModCreativeTabs.ITEM).register();
    public static final RegistryObject<Item> CHISEL = Iridynamics.REGISTRY.item("tool/chisel", (properties) -> new MaterialToolItem(properties, ToolChisel.INSTANCE)).stacksTo(1).tab(ModCreativeTabs.ITEM).register();
    public static final RegistryObject<Item> KNIFE = Iridynamics.REGISTRY.item("tool/knife", (properties) -> new MaterialToolItem(properties, ToolKnife.INSTANCE)).stacksTo(1).tab(ModCreativeTabs.ITEM).register();

    public static void init() {
        SolidShape.REGISTRY.values().forEach((shape) -> Iridynamics.REGISTRY.item("material_item/" + shape.getName(), (properties) -> new MaterialItem(properties, shape)).tab(ModCreativeTabs.MATERIAL).register());
        MaterialToolBase.register(ToolHammer.INSTANCE);
        MaterialToolBase.register(ToolChisel.INSTANCE);
        MaterialToolBase.register(ToolKnife.INSTANCE);
        MoldToolItem.GENERATED_MOLDS.add(ModSolidShapes.HAMMER_HEAD);
        MoldToolItem.GENERATED_MOLDS.add(ModSolidShapes.CHISEL_HEAD);
        MoldToolItem.GENERATED_MOLDS.add(ModSolidShapes.KNIFE_HEAD);
    }

    public static void onSetup() {
        MaterialPhysicalInfo wood = ModMaterials.WOOD.getPhysicalInfo();
        MaterialPhysicalInfo charcoal = ModMaterials.CHARCOAL.getPhysicalInfo();
        MaterialPhysicalInfo coal = ModMaterials.COAL.getPhysicalInfo();
        PileBlockEntity.PILE_ITEM.register(WOOD_BRICK.get(), new PileBlockEntity.PileItemInfo("wood_brick", wood.moleHeatCapacity(), wood.thermalConductivity()));
        PileBlockEntity.PILE_ITEM.register(Items.CHARCOAL, new PileBlockEntity.PileItemInfo("charcoal", charcoal.moleHeatCapacity(), charcoal.thermalConductivity()));
        PileBlockEntity.PILE_ITEM.register(Items.COAL, new PileBlockEntity.PileItemInfo("coal", coal.moleHeatCapacity(), coal.thermalConductivity()));
        FuelInfo.of("wood_brick", wood.moleCalorificValue(), 550.0, 1100.0).registerForItem(WOOD_BRICK.get());
        // TODO: 添加引火机制，将木炭、煤炭闪点改回900K
        FuelInfo.of("charcoal", charcoal.moleCalorificValue(), 550.0, 1400.0).registerForItem(Items.CHARCOAL);
        FuelInfo.of("coal", coal.moleCalorificValue(), 550.0, 1400.0).registerForItem(Items.COAL);
    }
}