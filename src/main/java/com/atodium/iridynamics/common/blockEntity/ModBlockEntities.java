package com.atodium.iridynamics.common.blockEntity;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.blockEntity.equipment.*;
import com.atodium.iridynamics.common.blockEntity.rotate.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final RegistryObject<BlockEntityType<FuelBlockEntity>> FUEL = Iridynamics.REGISTRY.blockEntity("fuel", () -> BlockEntityType.Builder.of(FuelBlockEntity::new, ModBlocks.FUEL.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<HeatProcessBlockEntity>> HEAT_PROCESS = Iridynamics.REGISTRY.blockEntity("heat_process", () -> BlockEntityType.Builder.of(HeatProcessBlockEntity::new, ModBlocks.HEAT_PROCESS.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<PileBlockEntity>> PILE = Iridynamics.REGISTRY.blockEntity("pile", () -> BlockEntityType.Builder.of(PileBlockEntity::new, ModBlocks.PILE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<PlacedStoneBlockEntity>> PLACED_STONE = Iridynamics.REGISTRY.blockEntity("placed_stone", () -> BlockEntityType.Builder.of(PlacedStoneBlockEntity::new, ModBlocks.PLACED_STONE.get()).build(null)).register();

    public static final RegistryObject<BlockEntityType<ChuteBlockEntity>> CHUTE = Iridynamics.REGISTRY.blockEntity("chute", () -> BlockEntityType.Builder.of(ChuteBlockEntity::new, ModBlocks.CHUTE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<ForgeBlockEntity>> FORGE = Iridynamics.REGISTRY.blockEntity("forge", () -> BlockEntityType.Builder.of(ForgeBlockEntity::new, ModBlocks.FORGE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<AnvilBlockEntity>> ANVIL = Iridynamics.REGISTRY.blockEntity("anvil", () -> BlockEntityType.Builder.of(AnvilBlockEntity::new, ModBlocks.ANVIL.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<MoldBlockEntity>> MOLD = Iridynamics.REGISTRY.blockEntity("mold", () -> BlockEntityType.Builder.of(MoldBlockEntity::new, ModBlocks.MOLD.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<MoldToolBlockEntity>> MOLD_TOOL = Iridynamics.REGISTRY.blockEntity("mold_tool", () -> BlockEntityType.Builder.of(MoldToolBlockEntity::new, ModBlocks.MOLD_TOOL.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<SmallCrucibleBlockEntity>> SMALL_CRUCIBLE = Iridynamics.REGISTRY.blockEntity("small_crucible", () -> BlockEntityType.Builder.of(SmallCrucibleBlockEntity::new, ModBlocks.SMALL_CRUCIBLE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE = Iridynamics.REGISTRY.blockEntity("crucible", () -> BlockEntityType.Builder.of(CrucibleBlockEntity::new, ModBlocks.CRUCIBLE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<BasinBlockEntity>> BASIN = Iridynamics.REGISTRY.blockEntity("basin", () -> BlockEntityType.Builder.of(BasinBlockEntity::new, ModBlocks.BASIN.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<GrindstoneBlockEntity>> GRINDSTONE = Iridynamics.REGISTRY.blockEntity("grindstone", () -> BlockEntityType.Builder.of(GrindstoneBlockEntity::new, ModBlocks.GRINDSTONE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<BlowerBlockEntity>> BLOWER = Iridynamics.REGISTRY.blockEntity("blower", () -> BlockEntityType.Builder.of(BlowerBlockEntity::new, ModBlocks.BLOWER.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<ArtisanCraftingTableBlockEntity>> ARTISAN_CRAFTING_TABLE = Iridynamics.REGISTRY.blockEntity("artisan_crafting_table", () -> BlockEntityType.Builder.of(ArtisanCraftingTableBlockEntity::new, ModBlocks.ARTISAN_CRAFTING_TABLE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<BonfireBlockEntity>> BONFIRE = Iridynamics.REGISTRY.blockEntity("bonfire", () -> BlockEntityType.Builder.of(BonfireBlockEntity::new, ModBlocks.BONFIRE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<CarvingTableBlockEntity>> CARVING_TABLE = Iridynamics.REGISTRY.blockEntity("carving_table", () -> BlockEntityType.Builder.of(CarvingTableBlockEntity::new, ModBlocks.CARVING_TABLE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<CrushingBoardBlockEntity>> CRUSHING_BOARD = Iridynamics.REGISTRY.blockEntity("crushing_board", () -> BlockEntityType.Builder.of(CrushingBoardBlockEntity::new, ModBlocks.CRUSHING_BOARD.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<PotteryWorkTableBlockEntity>> POTTERY_WORK_TABLE = Iridynamics.REGISTRY.blockEntity("pottery_work_table", () -> BlockEntityType.Builder.of(PotteryWorkTableBlockEntity::new, ModBlocks.POTTERY_WORK_TABLE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<ResearchTableBlockEntity>> RESEARCH_TABLE = Iridynamics.REGISTRY.blockEntity("research_table", () -> BlockEntityType.Builder.of(ResearchTableBlockEntity::new, ModBlocks.RESEARCH_TABLE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<FurnaceBlockEntity>> FURNACE = Iridynamics.REGISTRY.blockEntity("furnace", () -> BlockEntityType.Builder.of(FurnaceBlockEntity::new, ModBlocks.FURNACE.get()).build(null)).register();

    public static final RegistryObject<BlockEntityType<AxleBlockEntity>> AXLE = Iridynamics.REGISTRY.blockEntity("axle", () -> BlockEntityType.Builder.of(AxleBlockEntity::new, ModBlocks.AXLE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<GearboxBlockEntity>> GEARBOX = Iridynamics.REGISTRY.blockEntity("gearbox", () -> BlockEntityType.Builder.of(GearboxBlockEntity::new, ModBlocks.GEARBOX.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<EscapementBlockEntity>> ESCAPEMENT = Iridynamics.REGISTRY.blockEntity("escapement", () -> BlockEntityType.Builder.of(EscapementBlockEntity::new, ModBlocks.ESCAPEMENT.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<FlywheelBlockEntity>> FLYWHEEL = Iridynamics.REGISTRY.blockEntity("flywheel", () -> BlockEntityType.Builder.of(FlywheelBlockEntity::new, ModBlocks.FLYWHEEL.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<HandleBlockEntity>> HANDLE = Iridynamics.REGISTRY.blockEntity("handle", () -> BlockEntityType.Builder.of(HandleBlockEntity::new, ModBlocks.HANDLE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<CentrifugeBlockEntity>> CENTRIFUGE = Iridynamics.REGISTRY.blockEntity("centrifuge", () -> BlockEntityType.Builder.of(CentrifugeBlockEntity::new, ModBlocks.CENTRIFUGE.get()).build(null)).register();

    public static void init() {

    }
}