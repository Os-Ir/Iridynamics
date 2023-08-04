package com.atodium.iridynamics.api.fluid;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.material.ModMaterials;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final RegistryObject<Fluid> WATER = Iridynamics.REGISTRY.fluid("water", () -> new MaterialLiquidFluid(ModMaterials.WATER)).register();
    public static final RegistryObject<Fluid> LAVA = Iridynamics.REGISTRY.fluid("lava", () -> new MaterialLiquidFluid(ModMaterials.LAVA)).register();
    public static final RegistryObject<Fluid> MILK = Iridynamics.REGISTRY.fluid("milk", () -> new MaterialLiquidFluid(ModMaterials.MILK)).register();
    public static final RegistryObject<Fluid> MERCURY = Iridynamics.REGISTRY.fluid("mercury", () -> new MaterialLiquidFluid(ModMaterials.MERCURY)).register();

    public static final RegistryObject<Fluid> NITROGEN = Iridynamics.REGISTRY.fluid("nitrogen", () -> new MaterialGasFluid(ModMaterials.NITROGEN)).register();
    public static final RegistryObject<Fluid> OXYGEN = Iridynamics.REGISTRY.fluid("oxygen", () -> new MaterialGasFluid(ModMaterials.OXYGEN)).register();
    public static final RegistryObject<Fluid> FLUORINE = Iridynamics.REGISTRY.fluid("fluorine", () -> new MaterialGasFluid(ModMaterials.FLUORINE)).register();
    public static final RegistryObject<Fluid> NEON = Iridynamics.REGISTRY.fluid("neon", () -> new MaterialGasFluid(ModMaterials.NEON)).register();
    public static final RegistryObject<Fluid> CHLORINE = Iridynamics.REGISTRY.fluid("chlorine", () -> new MaterialGasFluid(ModMaterials.CHLORINE)).register();
    public static final RegistryObject<Fluid> ARGON = Iridynamics.REGISTRY.fluid("argon", () -> new MaterialGasFluid(ModMaterials.ARGON)).register();
    public static final RegistryObject<Fluid> BROMINE = Iridynamics.REGISTRY.fluid("bromine", () -> new MaterialGasFluid(ModMaterials.BROMINE)).register();
    public static final RegistryObject<Fluid> KRYPTON = Iridynamics.REGISTRY.fluid("krypton", () -> new MaterialGasFluid(ModMaterials.KRYPTON)).register();
    public static final RegistryObject<Fluid> XENON = Iridynamics.REGISTRY.fluid("xenon", () -> new MaterialGasFluid(ModMaterials.XENON)).register();
    public static final RegistryObject<Fluid> RADON = Iridynamics.REGISTRY.fluid("radon", () -> new MaterialGasFluid(ModMaterials.RADON)).register();

    public static final RegistryObject<Fluid> AIR = Iridynamics.REGISTRY.fluid("air", () -> new MaterialGasFluid(ModMaterials.AIR)).register();
    public static final RegistryObject<Fluid> STEAM = Iridynamics.REGISTRY.fluid("steam", () -> new MaterialGasFluid(ModMaterials.RADON)).register();
    public static final RegistryObject<Fluid> METHANE = Iridynamics.REGISTRY.fluid("methane", () -> new MaterialGasFluid(ModMaterials.METHANE)).register();
    public static final RegistryObject<Fluid> VINYL = Iridynamics.REGISTRY.fluid("vinyl", () -> new MaterialGasFluid(ModMaterials.VINYL)).register();
    public static final RegistryObject<Fluid> ETHANE = Iridynamics.REGISTRY.fluid("ethane", () -> new MaterialGasFluid(ModMaterials.ETHANE)).register();
    public static final RegistryObject<Fluid> ACETYLENE = Iridynamics.REGISTRY.fluid("acetylene", () -> new MaterialGasFluid(ModMaterials.ACETYLENE)).register();
    public static final RegistryObject<Fluid> PROPANE = Iridynamics.REGISTRY.fluid("propane", () -> new MaterialGasFluid(ModMaterials.PROPANE)).register();
    public static final RegistryObject<Fluid> PROPYLENE = Iridynamics.REGISTRY.fluid("propylene", () -> new MaterialGasFluid(ModMaterials.PROPYLENE)).register();

    public static final RegistryObject<Fluid> CARBON_MONOXIDE = Iridynamics.REGISTRY.fluid("carbon_monoxide", () -> new MaterialGasFluid(ModMaterials.CARBON_MONOXIDE)).register();
    public static final RegistryObject<Fluid> CARBON_DIOXIDE = Iridynamics.REGISTRY.fluid("carbon_dioxide", () -> new MaterialGasFluid(ModMaterials.CARBON_DIOXIDE)).register();
    public static final RegistryObject<Fluid> NITRIC_OXIDE = Iridynamics.REGISTRY.fluid("nitric_oxide", () -> new MaterialGasFluid(ModMaterials.NITRIC_OXIDE)).register();
    public static final RegistryObject<Fluid> NITROGEN_DIOXIDE = Iridynamics.REGISTRY.fluid("nitrogen_dioxide", () -> new MaterialGasFluid(ModMaterials.NITROGEN_DIOXIDE)).register();
    public static final RegistryObject<Fluid> SULFUR_DIOXIDE = Iridynamics.REGISTRY.fluid("sulfur_dioxide", () -> new MaterialGasFluid(ModMaterials.SULFUR_DIOXIDE)).register();
    public static final RegistryObject<Fluid> SULFUR_TRIOXIDE = Iridynamics.REGISTRY.fluid("sulfur_trioxide", () -> new MaterialGasFluid(ModMaterials.SULFUR_TRIOXIDE)).register();
    public static final RegistryObject<Fluid> HYDROGEN_CHLORIDE = Iridynamics.REGISTRY.fluid("hydrogen_chloride", () -> new MaterialGasFluid(ModMaterials.HYDROGEN_CHLORIDE)).register();

    public static void init() {

    }
}