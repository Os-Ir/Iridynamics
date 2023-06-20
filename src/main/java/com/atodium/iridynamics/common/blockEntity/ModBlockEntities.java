package com.atodium.iridynamics.common.blockEntity;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.common.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final RegistryObject<BlockEntityType<PileBlockEntity>> PILE = Iridynamics.REGISTRY.blockEntity("pile", () -> BlockEntityType.Builder.of(PileBlockEntity::new, ModBlocks.PILE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<HeatProcessBlockEntity>> HEAT_PROCESS = Iridynamics.REGISTRY.blockEntity("heat_process", () -> BlockEntityType.Builder.of(HeatProcessBlockEntity::new, ModBlocks.HEAT_PROCESS.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<FuelBlockEntity>> FUEL = Iridynamics.REGISTRY.blockEntity("fuel", () -> BlockEntityType.Builder.of(FuelBlockEntity::new, ModBlocks.FUEL.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<ChuteBlockEntity>> CHUTE = Iridynamics.REGISTRY.blockEntity("chute", () -> BlockEntityType.Builder.of(ChuteBlockEntity::new, ModBlocks.CHUTE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<ForgeBlockEntity>> FORGE = Iridynamics.REGISTRY.blockEntity("forge", () -> BlockEntityType.Builder.of(ForgeBlockEntity::new, ModBlocks.FORGE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<AnvilBlockEntity>> ANVIL = Iridynamics.REGISTRY.blockEntity("anvil", () -> BlockEntityType.Builder.of(AnvilBlockEntity::new, ModBlocks.ANVIL.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<MoldBlockEntity>> MOLD = Iridynamics.REGISTRY.blockEntity("mold", () -> BlockEntityType.Builder.of(MoldBlockEntity::new, ModBlocks.MOLD.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<MoldToolBlockEntity>> MOLD_TOOL = Iridynamics.REGISTRY.blockEntity("mold_tool", () -> BlockEntityType.Builder.of(MoldToolBlockEntity::new, ModBlocks.MOLD_TOOL.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<SmallCrucibleBlockEntity>> SMALL_CRUCIBLE = Iridynamics.REGISTRY.blockEntity("small_crucible", () -> BlockEntityType.Builder.of(SmallCrucibleBlockEntity::new, ModBlocks.SMALL_CRUCIBLE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<BasinBlockEntity>> BASIN = Iridynamics.REGISTRY.blockEntity("basin", () -> BlockEntityType.Builder.of(BasinBlockEntity::new, ModBlocks.BASIN.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<GrindstoneBlockEntity>> GRINDSTONE = Iridynamics.REGISTRY.blockEntity("grindstone", () -> BlockEntityType.Builder.of(GrindstoneBlockEntity::new, ModBlocks.GRINDSTONE.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<BlowerBlockEntity>> BLOWER = Iridynamics.REGISTRY.blockEntity("blower", () -> BlockEntityType.Builder.of(BlowerBlockEntity::new, ModBlocks.BLOWER.get()).build(null)).register();
    public static final RegistryObject<BlockEntityType<ArtisanCraftingTableBlockEntity>> ARTISAN_CRAFTING_TABLE = Iridynamics.REGISTRY.blockEntity("artisan_crafting_table", () -> BlockEntityType.Builder.of(ArtisanCraftingTableBlockEntity::new, ModBlocks.ARTISAN_CRAFTING_TABLE.get()).build(null)).register();

    public static void init() {

    }
}