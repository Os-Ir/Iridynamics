package com.atodium.iridynamics.common.block;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.ModCreativeTabs;
import com.atodium.iridynamics.api.multiblock.MultiblockModule;
import com.atodium.iridynamics.common.block.equipment.*;
import com.atodium.iridynamics.common.block.rotate.*;
import com.atodium.iridynamics.common.multiblock.Smelter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final RegistryObject<Block> FUEL = Iridynamics.REGISTRY.block("fuel", FuelBlock::new, Material.WOOD).strength(5.0f, 3.0f).sound(SoundType.WOOD).noOcclusion().noDrops().register();
    public static final RegistryObject<Block> HEAT_PROCESS = Iridynamics.REGISTRY.block("heat_process", HeatProcessBlock::new, Material.WOOD).strength(5.0f, 3.0f).sound(SoundType.WOOD).noOcclusion().register();
    public static final RegistryObject<Block> PILE = Iridynamics.REGISTRY.block("pile", PileBlock::new, Material.WOOD).strength(5.0f, 3.0f).sound(SoundType.WOOD).noOcclusion().register();
    public static final RegistryObject<Block> PLACED_STICK = Iridynamics.REGISTRY.block("placed_stick", PlacedStickBlock::new, Material.WOOD).strength(0.0f, 0.0f).sound(SoundType.WOOD).noOcclusion().register();
    public static final RegistryObject<Block> PLACED_STONE = Iridynamics.REGISTRY.block("placed_stone", PlacedStoneBlock::new, Material.STONE).strength(0.0f, 0.0f).sound(SoundType.STONE).noOcclusion().register();

    public static final RegistryObject<Block> SMELTER_WALL = Iridynamics.REGISTRY.block("smelter_wall", Block::new, Material.STONE).strength(6.0f, 4.0f).sound(SoundType.STONE).registerWithItem(ModCreativeTabs.BLOCK);

    public static final RegistryObject<Block> CHUTE = Iridynamics.REGISTRY.block("chute", ChuteBlock::new, Material.STONE).strength(4.0f, 2.0f).sound(SoundType.STONE).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> FORGE = Iridynamics.REGISTRY.block("forge", ForgeBlock::new, Material.STONE).strength(2.0f, 1.0f).sound(SoundType.STONE).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> ANVIL = Iridynamics.REGISTRY.block("anvil", AnvilBlock::new, Material.METAL).strength(6.0f, 3.0f).sound(SoundType.ANVIL).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> MOLD = Iridynamics.REGISTRY.block("mold", MoldBlock::new, Material.STONE).strength(2.0f, 1.0f).sound(SoundType.STONE).noOcclusion().register();
    public static final RegistryObject<Block> MOLD_TOOL = Iridynamics.REGISTRY.block("mold_tool", MoldToolBlock::new, Material.STONE).strength(2.0f, 1.0f).sound(SoundType.STONE).noOcclusion().register();
    public static final RegistryObject<Block> SMALL_CRUCIBLE = Iridynamics.REGISTRY.block("small_crucible", SmallCrucibleBlock::new, Material.STONE).strength(2.0f, 1.0f).sound(SoundType.STONE).noOcclusion().register();
    public static final RegistryObject<Block> CRUCIBLE = Iridynamics.REGISTRY.block("crucible", CrucibleBlock::new, Material.STONE).strength(4.0f, 2.0f).sound(SoundType.STONE).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> BASIN = Iridynamics.REGISTRY.block("basin", BasinBlock::new, Material.WOOD).strength(2.0f, 1.0f).sound(SoundType.WOOD).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> BENDING_ANVIL = Iridynamics.REGISTRY.block("bending_anvil", BendingAnvilBlock::new, Material.METAL).strength(6.0f, 3.0f).sound(SoundType.ANVIL).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> GRINDSTONE = Iridynamics.REGISTRY.block("grindstone", GrindstoneBlock::new, Material.STONE).strength(2.0f, 2.0f).sound(SoundType.STONE).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> BLOWER = Iridynamics.REGISTRY.block("blower", BlowerBlock::new, Material.WOOD).strength(3.0f, 2.0f).sound(SoundType.WOOD).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> ARTISAN_CRAFTING_TABLE = Iridynamics.REGISTRY.block("artisan_crafting_table", ArtisanCraftingTableBlock::new, Material.WOOD).strength(3.0f, 2.0f).sound(SoundType.WOOD).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> BONFIRE = Iridynamics.REGISTRY.block("bonfire", BonfireBlock::new, Material.WOOD).strength(2.0f, 1.0f).sound(SoundType.WOOD).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> CARVING_TABLE = Iridynamics.REGISTRY.block("carving_table", CarvingTableBlock::new, Material.WOOD).strength(2.0f, 1.0f).sound(SoundType.WOOD).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> CRUSHING_BOARD = Iridynamics.REGISTRY.block("crushing_board", CrushingBoardBlock::new, Material.STONE).strength(3.0f, 2.0f).sound(SoundType.STONE).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> POTTERY_WORK_TABLE = Iridynamics.REGISTRY.block("pottery_work_table", PotteryWorkTableBlock::new, Material.STONE).strength(3.0f, 2.0f).sound(SoundType.STONE).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> RESEARCH_TABLE = Iridynamics.REGISTRY.block("research_table", ResearchTableBlock::new, Material.WOOD).strength(3.0f, 2.0f).sound(SoundType.WOOD).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> FURNACE = Iridynamics.REGISTRY.block("furnace", FurnaceBlock::new, Material.STONE).strength(4.0f, 2.0f).sound(SoundType.STONE).registerWithItem(ModCreativeTabs.EQUIPMENT);

    public static final RegistryObject<Block> AXLE = Iridynamics.REGISTRY.block("axle", AxleBlock::new, Material.WOOD).strength(2.0f, 1.0f).sound(SoundType.WOOD).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> GEARBOX = Iridynamics.REGISTRY.block("gearbox", GearboxBlock::new, Material.WOOD).strength(3.0f, 2.0f).sound(SoundType.WOOD).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> ESCAPEMENT = Iridynamics.REGISTRY.block("escapement", EscapementBlock::new, Material.WOOD).strength(3.0f, 2.0f).sound(SoundType.WOOD).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> FLYWHEEL = Iridynamics.REGISTRY.block("flywheel", FlywheelBlock::new, Material.WOOD).strength(3.0f, 2.0f).sound(SoundType.WOOD).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> HANDLE = Iridynamics.REGISTRY.block("handle", HandleBlock::new, Material.WOOD).strength(3.0f, 2.0f).sound(SoundType.WOOD).noOcclusion().registerWithItem(ModCreativeTabs.EQUIPMENT);
    public static final RegistryObject<Block> CENTRIFUGE = Iridynamics.REGISTRY.block("centrifuge", CentrifugeBlock::new, Material.METAL).strength(6.0f, 4.0f).sound(SoundType.METAL).registerWithItem(ModCreativeTabs.EQUIPMENT);

    public static void init() {

    }

    public static void setup() {
        MultiblockModule.registerBlock(Iridynamics.rl("smelter_wall"), SMELTER_WALL.get());
        MultiblockModule.registerStructure(Smelter.INSTANCE);
    }
}