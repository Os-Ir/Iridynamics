package com.atodium.iridynamics.api.material;

import com.atodium.iridynamics.api.material.type.IMaterialFlag;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import net.minecraft.world.item.Items;

import java.util.function.Predicate;

public class ModSolidShapes {
    public static final SolidShape DUST = new SolidShape("dust", 144, flag(MaterialBase.GENERATE_DUST));
    public static final SolidShape DOUBLE_PLATE = new SolidShape("double_plate", 288, flag(MaterialBase.GENERATE_PLATE));
    public static final SolidShape PLATE = new SolidShape("plate", new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, 144, flag(MaterialBase.GENERATE_PLATE), DOUBLE_PLATE);
    public static final SolidShape CURVED_PLATE = new SolidShape("curved_plate", 144, flag(MaterialBase.GENERATE_PLATE));
    public static final SolidShape ROD = new SolidShape("rod", 72, flag(MaterialBase.GENERATE_ROD));
    public static final SolidShape SHORT_ROD = new SolidShape("short_rod", 36, flag(MaterialBase.GENERATE_ROD));
    public static final SolidShape LONG_ROD = new SolidShape("long_rod", 144, flag(MaterialBase.GENERATE_ROD));
    public static final SolidShape GEAR = new SolidShape("gear", 576, flag(MaterialBase.GENERATE_GEAR));
    public static final SolidShape SMALL_GEAR = new SolidShape("small_gear", 144, flag(MaterialBase.GENERATE_GEAR));
    public static final SolidShape CRYSTAL = new SolidShape("crystal", 144, flag(MaterialBase.GENERATE_CRYSTAL));
    public static final SolidShape DOUBLE_INGOT = new SolidShape("double_ingot", new int[]{0, 6, 6, 6, 6, 6, 0, 0, 6, 6, 6, 6, 6, 0, 0, 6, 6, 6, 6, 6, 0, 0, 6, 6, 6, 6, 6, 0, 0, 6, 6, 6, 6, 6, 0, 0, 6, 6, 6, 6, 6, 0, 0, 6, 6, 6, 6, 6, 0}, 288, flag(MaterialBase.GENERATE_INGOT));
    public static final SolidShape INGOT = new SolidShape("ingot", new int[]{0, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 0}, 144, flag(MaterialBase.GENERATE_INGOT), DOUBLE_INGOT);
    public static final SolidShape FOIL = new SolidShape("foil", 36, flag(MaterialBase.GENERATE_FOIL));
    public static final SolidShape SCREW = new SolidShape("screw", 18, flag(MaterialBase.GENERATE_SCREW));
    public static final SolidShape SPRING = new SolidShape("spring", 144, flag(MaterialBase.GENERATE_SPRING));
    public static final SolidShape RING = new SolidShape("ring", 36, flag(MaterialBase.GENERATE_RING));
    public static final SolidShape WIRE = new SolidShape("wire", 18, flag(MaterialBase.GENERATE_WIRE));
    public static final SolidShape NUGGET = new SolidShape("nugget", new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 16, flag(MaterialBase.GENERATE_NUGGET));

    public static final SolidShape KNIFE_HEAD = new SolidShape("knife_head", 144, flag(MaterialBase.GENERATE_TOOL));
    public static final SolidShape HAMMER_HEAD = new SolidShape("hammer_head", 144, flag(MaterialBase.GENERATE_TOOL));
    public static final SolidShape CHISEL_HEAD = new SolidShape("chisel_head", 144, flag(MaterialBase.GENERATE_TOOL));

    public static final SolidShape ORE_NUGGET = new SolidShape("ore_nugget", null, 16, flag(MaterialBase.GENERATE_ORE));

    public static Predicate<MaterialBase> flag(IMaterialFlag flag) {
        return (material) -> material.hasFlag(flag);
    }

    public static void register() {
        DUST.addIgnoredMaterial(ModMaterials.REDSTONE, ModMaterials.GLOWSTONE);
        INGOT.addIgnoredMaterial(ModMaterials.IRON, ModMaterials.GOLD, ModMaterials.COPPER, ModMaterials.NETHERITE);
        NUGGET.addIgnoredMaterial(ModMaterials.IRON, ModMaterials.GOLD);
        ROD.addIgnoredMaterial(ModMaterials.WOOD);
        CRYSTAL.addIgnoredMaterial(ModMaterials.DIAMOND, ModMaterials.EMERALD);
        MaterialEntry.register(DUST, ModMaterials.REDSTONE, Items.REDSTONE);
        MaterialEntry.register(DUST, ModMaterials.GLOWSTONE, Items.GLOWSTONE_DUST);
        MaterialEntry.register(INGOT, ModMaterials.IRON, Items.IRON_INGOT);
        MaterialEntry.register(INGOT, ModMaterials.GOLD, Items.GOLD_INGOT);
        MaterialEntry.register(INGOT, ModMaterials.COPPER, Items.COPPER_INGOT);
        MaterialEntry.register(INGOT, ModMaterials.NETHERITE, Items.NETHERITE_INGOT);
        MaterialEntry.register(NUGGET, ModMaterials.IRON, Items.IRON_NUGGET);
        MaterialEntry.register(NUGGET, ModMaterials.GOLD, Items.GOLD_NUGGET);
        MaterialEntry.register(CRYSTAL, ModMaterials.DIAMOND, Items.DIAMOND);
        MaterialEntry.register(ROD, ModMaterials.WOOD, Items.STICK);
        MaterialEntry.register(CRYSTAL, ModMaterials.EMERALD, Items.EMERALD);
    }
}