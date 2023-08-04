package com.atodium.iridynamics.api.material;

import com.atodium.iridynamics.api.heat.MaterialHeatInfo;
import com.atodium.iridynamics.api.heat.SubMaterialHeatInfo;
import com.atodium.iridynamics.api.material.type.*;
import com.atodium.iridynamics.api.util.data.MonotonicMap;

// TODO: 将所有的材料信息改为通过json文件加载
public class ModMaterials {
    public static final GasMaterial HYDROGEN = new GasMaterial("hydrogen").color(0x00ffff).cast();
    public static final GasMaterial HELIUM = new GasMaterial("helium").color(0xdddd00).cast();

    public static final PlasticityMaterial LITHIUM = new PlasticityMaterial("lithium").color(0xc8c8c8).cast();
    public static final PlasticityMaterial BERYLLIUM = new PlasticityMaterial("beryllium").color(0x64b464).cast();
    public static final PlasticityMaterial BORON = new PlasticityMaterial("boron").color(0xaad2d2).cast();
    public static final DustMaterial CARBON = new DustMaterial("carbon").color(0x333333).cast();
    public static final GasMaterial NITROGEN = new GasMaterial("nitrogen").color(0x50b4a0).cast();
    public static final GasMaterial OXYGEN = new GasMaterial("oxygen").color(0xa0c8fa).cast();
    public static final GasMaterial FLUORINE = new GasMaterial("fluorine").color(0xb4ffaa).cast();
    public static final GasMaterial NEON = new GasMaterial("neon").color(0xdd0000).cast();

    public static final PlasticityMaterial SODIUM = new PlasticityMaterial("sodium").color(0x000096).cast();
    public static final PlasticityMaterial MAGNESIUM = new PlasticityMaterial("magnesium").color(0xf09696).cast();
    public static final PlasticityMaterial ALUMINIUM = new PlasticityMaterial("aluminium").color(0x78c8f0).cast();
    public static final PlasticityMaterial SILICON = new PlasticityMaterial("silicon").color(0x3c3c50).cast();
    public static final DustMaterial PHOSPHOR = new DustMaterial("phosphor").color(0x821e1e).cast();
    public static final DustMaterial SULFUR = new DustMaterial("sulfur").color(0xc8c800).cast();
    public static final GasMaterial CHLORINE = new GasMaterial("chlorine").color(0xffffaa).cast();
    public static final GasMaterial ARGON = new GasMaterial("argon").color(0x32e600).cast();

    public static final PlasticityMaterial POTASSIUM = new PlasticityMaterial("potassium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial CALCIUM = new PlasticityMaterial("calcium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial SCANDIUM = new PlasticityMaterial("scandium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial TITANIUM = new PlasticityMaterial("titanium").color(0xb450c8).cast();
    public static final PlasticityMaterial VANADIUM = new PlasticityMaterial("vanadium").color(0x9696e6).cast();
    public static final PlasticityMaterial CHROME = new PlasticityMaterial("chrome").color(0xfaaac8).cast();
    public static final PlasticityMaterial MANGANESE = new PlasticityMaterial("manganese").color(0x646464).cast();
    public static final PlasticityMaterial IRON = new PlasticityMaterial("iron");
    public static final PlasticityMaterial COBALT = new PlasticityMaterial("cobalt").color(0x3232c8).cast();
    public static final PlasticityMaterial NICKEL = new PlasticityMaterial("nickel").color(0x9696e6).cast();
    public static final PlasticityMaterial COPPER = new PlasticityMaterial("copper");
    public static final PlasticityMaterial ZINC = new PlasticityMaterial("zinc").color(0xfadcdc).cast();
    public static final PlasticityMaterial GALLIUM = new PlasticityMaterial("gallium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial GERMANIUM = new PlasticityMaterial("germanium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial ARSENIC = new PlasticityMaterial("arsenic").color(0xaaaaaa).cast();
    public static final PlasticityMaterial SELENIUM = new PlasticityMaterial("selenium").color(0xaaaaaa).cast();
    public static final GasMaterial BROMINE = new GasMaterial("bromine").color(0x82501e).cast();
    public static final GasMaterial KRYPTON = new GasMaterial("krypton").color(0x145ad2).cast();

    public static final PlasticityMaterial RUBIDIUM = new PlasticityMaterial("rubidium").color(0xb4463c).cast();
    public static final PlasticityMaterial STRONTIUM = new PlasticityMaterial("strontium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial YTTRIUM = new PlasticityMaterial("yttrium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial ZIRCONIUM = new PlasticityMaterial("zirconium").color(0x3cc8aa).cast();
    public static final PlasticityMaterial NIOBIUM = new PlasticityMaterial("niobium").color(0x8c82aa).cast();
    public static final PlasticityMaterial MOLYBDENUM = new PlasticityMaterial("molybdenum").color(0xaaaae6).cast();
    public static final PlasticityMaterial TECHNETIUM = new PlasticityMaterial("technetium").color(0x82dc50).cast();
    public static final PlasticityMaterial RUTHENIUM = new PlasticityMaterial("ruthenium").color(0x6ec8d2).cast();
    public static final PlasticityMaterial RHODIUM = new PlasticityMaterial("rhodium").color(0xc8b45a).cast();
    public static final PlasticityMaterial PALLADIUM = new PlasticityMaterial("palladium").color(0x82826e).cast();
    public static final PlasticityMaterial SILVER = new PlasticityMaterial("silver").color(0xdcdcf0).cast();
    public static final PlasticityMaterial CADMIUM = new PlasticityMaterial("cadmium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial INDIUM = new PlasticityMaterial("indium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial TIN = new PlasticityMaterial("tin");
    public static final PlasticityMaterial ANTIMONY = new PlasticityMaterial("antimony").color(0xdcdcc8).cast();
    public static final PlasticityMaterial TELLURIUM = new PlasticityMaterial("tellurium").color(0xaaaaaa).cast();
    public static final DustMaterial IODINE = new DustMaterial("iodine").color(0x8232a0).cast();
    public static final GasMaterial XENON = new GasMaterial("xenon").color(0x3ca0aa).cast();

    public static final PlasticityMaterial CAESIUM = new PlasticityMaterial("caesium").color(0xa0505a).cast();
    public static final PlasticityMaterial BARIUM = new PlasticityMaterial("barium").color(0x78d2a0).cast();
    public static final PlasticityMaterial LANTHANUM = new PlasticityMaterial("lanthanum").color(0xaaaaaa).cast();
    public static final PlasticityMaterial CERIUM = new PlasticityMaterial("cerium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial PRASEODYMIUM = new PlasticityMaterial("praseodymium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial NEODYMIUM = new PlasticityMaterial("neodymium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial PROMETHIUM = new PlasticityMaterial("promethium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial SAMARIUM = new PlasticityMaterial("samarium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial EUROPIUM = new PlasticityMaterial("europium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial GADOLINIUM = new PlasticityMaterial("gadolinium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial TERBIUM = new PlasticityMaterial("terbium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial DYSPROSIUM = new PlasticityMaterial("dysprosium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial HOLMIUM = new PlasticityMaterial("holmium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial ERBIUM = new PlasticityMaterial("erbium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial THULIUM = new PlasticityMaterial("thulium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial YTTERBIUM = new PlasticityMaterial("ytterbium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial LUTETIUM = new PlasticityMaterial("lutetium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial HAFNIUM = new PlasticityMaterial("hafnium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial TANTALUM = new PlasticityMaterial("tantalum").color(0xaaaaaa).cast();
    public static final PlasticityMaterial TUNGSTEN = new PlasticityMaterial("tungsten").color(0x323232).cast();
    public static final PlasticityMaterial RHENIUM = new PlasticityMaterial("rhenium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial OSMIUM = new PlasticityMaterial("osmium").color(0x5050f0).cast();
    public static final PlasticityMaterial IRIDIUM = new PlasticityMaterial("iridium").color(0xf0f0f0).cast();
    public static final PlasticityMaterial PLATINUM = new PlasticityMaterial("platinum").color(0xf0f096).cast();
    public static final PlasticityMaterial GOLD = new PlasticityMaterial("gold").color(0xf0f000).cast();
    public static final LiquidMaterial MERCURY = new LiquidMaterial("mercury").color(0xa0a0a0).cast();
    public static final PlasticityMaterial THALLIUM = new PlasticityMaterial("thallium").color(0xaaaaaa).cast();
    public static final PlasticityMaterial LEAD = new PlasticityMaterial("lead").color(0x8c648c).cast();
    public static final PlasticityMaterial BISMUTH = new PlasticityMaterial("bismuth").color(0x64a0a0).cast();
    public static final PlasticityMaterial POLONIUM = new PlasticityMaterial("polonium").color(0xaaaaaa).cast();
    public static final DustMaterial ASTATINE = new DustMaterial("astatine").color(0xaaaaaa).cast();
    public static final GasMaterial RADON = new GasMaterial("radon").color(0xf000f0).cast();

    public static final LiquidMaterial WATER = new LiquidMaterial("water");
    public static final LiquidMaterial LAVA = new LiquidMaterial("lava");
    public static final LiquidMaterial MILK = new LiquidMaterial("milk");

    public static final PlasticityMaterial NETHERITE = new PlasticityMaterial("netherite").color(0x46322d).cast();
    public static final PlasticityMaterial WROUGHT_IRON = new PlasticityMaterial("wrought_iron").color(0xc8b4b4).setToolProperty(204800, 2, 6.0f).cast();
    public static final PlasticityMaterial PIG_IRON = new PlasticityMaterial("pig_iron").color(0xc8b4b4).setToolProperty(204800, 2, 6.0f).cast();
    public static final PlasticityMaterial STEEL = new PlasticityMaterial("steel").color(0x5a5a5a).setToolProperty(409600, 2, 8.0f).cast();
    public static final PlasticityMaterial BRONZE = new PlasticityMaterial("bronze");
    public static final PlasticityMaterial BRASS = new PlasticityMaterial("brass").color(0xf0b400).cast();
    public static final PlasticityMaterial CUPRONICKEL = new PlasticityMaterial("cupronickel").color(0xf0b400).cast();
    public static final PlasticityMaterial BLACK_BRONZE = new PlasticityMaterial("black_bronze").color(0x461e5a).setToolProperty(102400, 2, 6.0f).cast();
    public static final PlasticityMaterial BISMUTH_BRONZE = new PlasticityMaterial("bismuth_bronze").color(0x5a8282).setToolProperty(102400, 2, 6.0f).cast();
    public static final PlasticityMaterial RED_ALLOY = new PlasticityMaterial("red_alloy").color(0xc83232).cast();
    public static final PlasticityMaterial BLUE_ALLOY = new PlasticityMaterial("blue_alloy").color(0x3232c8).cast();
    public static final PlasticityMaterial STERLING_SILVER = new PlasticityMaterial("sterling_silver").color(0xe6c8dc).cast();
    public static final PlasticityMaterial STERLING_GOLD = new PlasticityMaterial("sterling_gold").color(0xfadc14).cast();
    public static final PlasticityMaterial INVAR = new PlasticityMaterial("invar").color(0xd2d28c).cast();

    public static final DustMaterial FLINT = new DustMaterial("flint").color(0x002040).setToolProperty(6400, 1, 4.0f).cast();
    public static final DustMaterial STONE = new DustMaterial("stone").color(0xc8c8c8).setToolProperty(3200, 1, 4.0f).cast();
    public static final DustMaterial WOOD = new DustMaterial("wood").color(0x8c5a28).cast();

    public static final GemMaterial DIAMOND = new GemMaterial("diamond").color(0xb4f0f0).cast();
    public static final GemMaterial EMERALD = new GemMaterial("emerald").color(0x64f064).cast();
    public static final GemMaterial RUBY = new GemMaterial("ruby");
    public static final GemMaterial SAPPHIRE = new GemMaterial("sapphire");

    public static final OreMaterial REDSTONE = new OreMaterial("redstone").color(0xc80000).cast();
    public static final OreMaterial BLUESTONE = new OreMaterial("bluestone").color(0x005ac8).cast();
    public static final OreMaterial GLOWSTONE = new OreMaterial("glowstone").color(0xffffbe).cast();
    public static final OreMaterial COAL = new OreMaterial("coal").color(0x323232).cast();
    public static final OreMaterial CHARCOAL = new OreMaterial("charcoal").color(0x463232).cast();
    public static final OreMaterial LAPIS_LAZULI = new OreMaterial("lapis_lazuli");
    public static final OreMaterial CINNABAR = new OreMaterial("cinnabar");
    public static final OreMaterial APATITE = new OreMaterial("apatite");
    public static final OreMaterial SALT = new OreMaterial("salt");
    public static final OreMaterial HEMATITE = new OreMaterial("hematite");
    public static final OreMaterial MAGNETITE = new OreMaterial("magnetite");
    public static final OreMaterial PYRITE = new OreMaterial("pyrite");
    public static final OreMaterial BAUXITE = new OreMaterial("bauxite");
    public static final OreMaterial CHALCOPYRITE = new OreMaterial("chalcopyrite");
    public static final OreMaterial TETRAHEDRITE = new OreMaterial("tetrahedrite");
    public static final OreMaterial CHALCOCITE = new OreMaterial("chalcocite");
    public static final OreMaterial CASSITERITE = new OreMaterial("cassiterite");
    public static final OreMaterial PYROLUSITE = new OreMaterial("pyrolusite");
    public static final OreMaterial ANTIMONITE = new OreMaterial("antimonite");
    public static final OreMaterial SPHALERITE = new OreMaterial("sphalerite");
    public static final OreMaterial GARNIERITE = new OreMaterial("garnierite");
    public static final OreMaterial PENTLANDITE = new OreMaterial("pentlandite");
    public static final OreMaterial MAGNESITE = new OreMaterial("magnesite");
    public static final OreMaterial GALENA = new OreMaterial("galena");
    public static final OreMaterial ARGENTITE = new OreMaterial("argentite");
    public static final OreMaterial CHLORARGYRITE = new OreMaterial("chlorargyrite");

    public static final GasMaterial AIR = new GasMaterial("air");
    public static final GasMaterial STEAM = new GasMaterial("steam");
    public static final GasMaterial METHANE = new GasMaterial("methane");
    public static final GasMaterial ETHANE = new GasMaterial("ethane");
    public static final GasMaterial VINYL = new GasMaterial("vinyl");
    public static final GasMaterial ACETYLENE = new GasMaterial("acetylene");
    public static final GasMaterial PROPANE = new GasMaterial("propane");
    public static final GasMaterial PROPYLENE = new GasMaterial("propylene");
    public static final GasMaterial CARBON_MONOXIDE = new GasMaterial("carbon_monoxide");
    public static final GasMaterial CARBON_DIOXIDE = new GasMaterial("carbon_dioxide");
    public static final GasMaterial NITRIC_OXIDE = new GasMaterial("nitric_oxide");
    public static final GasMaterial NITROGEN_DIOXIDE = new GasMaterial("nitrogen_dioxide");
    public static final GasMaterial SULFUR_DIOXIDE = new GasMaterial("sulfur_dioxide");
    public static final GasMaterial SULFUR_TRIOXIDE = new GasMaterial("sulfur_trioxide");
    public static final GasMaterial HYDROGEN_CHLORIDE = new GasMaterial("hydrogen_chloride");

    public static void register() {
        MaterialBase.GENERATE_ORE.setFlagForMaterial(GOLD, SILVER, NETHERITE);
        MaterialBase.GENERATE_TOOL.setFlagForMaterial(IRON, COPPER, WROUGHT_IRON, PIG_IRON, STEEL, BRONZE, BLACK_BRONZE, BISMUTH_BRONZE, FLINT, STONE);
        MaterialBase.GENERATE_ROD.setFlagForMaterial(WOOD);
        MaterialBase.GENERATE_GEAR.setFlagForMaterial(WOOD);
        MaterialBase.GENERATE_SCREW.setFlagForMaterial(WOOD);
        registerSolidInfo(GOLD, 19300.0, 318.0, 129.0, 1337.0);
        registerSolidInfo(SILVER, 10490.0, 420.0, 235.0, 1235.0);
        registerSolidInfo(BISMUTH, 9780.0, 8.0, 122.0, 545.0);
        registerSolidInfo(WROUGHT_IRON, 7870.0, 80.0, 450.0, 1750.0);
        registerSolidInfo(PIG_IRON, 7870.0, 80.0, 450.0, 1400.0);
        registerSolidInfo(STEEL, 7870.0, 80.0, 450.0, 1600.0);
        registerSolidInfo(BLACK_BRONZE, 8960.0, 401.0, 385.0, 1300.0);
        registerSolidInfo(BISMUTH_BRONZE, 8960.0, 401.0, 385.0, 1150.0);
        registerSolidInfo(COAL, 1350.0, 9950.0, 120.0, 3000.0, 3350000.0);
        registerSolidInfo(CHARCOAL, 1350.0, 9950.0, 120.0, 3000.0, 3350000.0);
        registerSolidInfo(WOOD, 750.0, 4500.0, 240.0, 3000.0, 150000.0);
    }

    public static void registerSolidInfo(MaterialBase material, double density, double thermalConductivity, double heatCapacity, double meltingPoint) {
        registerSolidInfo(material, density, thermalConductivity, heatCapacity, meltingPoint, 0.0);
    }

    public static void registerSolidInfo(MaterialBase material, double density, double thermalConductivity, double heatCapacity, double meltingPoint, double calorificValue) {
        material.setPhysicalInfo(new MaterialPhysicalInfo(density, thermalConductivity, heatCapacity, calorificValue));
        double capacity = heatCapacity * density / 9.0;
        material.setHeatInfo(MaterialHeatInfo.getSimplified(SubMaterialHeatInfo.builder().putCapacity(Phase.SOLID, capacity).putCapacity(Phase.LIQUID, capacity).setCriticalPoints(MonotonicMap.<Phase>builder().addData(0.0, Phase.SOLID).addData(meltingPoint, Phase.LIQUID).build()).build()));
    }
}