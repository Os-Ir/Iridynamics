package com.atodium.iridynamics.common.block;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.ModCreativeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final RegistryObject<Block> PILE = Iridynamics.REGISTRY.block("pile", PileBlock::new, Material.WOOD).strength(5.0f, 3.0f).sound(SoundType.WOOD).noOcclusion().register();
    public static final RegistryObject<Block> HEAT_PROCESS = Iridynamics.REGISTRY.block("heat_process", HeatProcessBlock::new, Material.WOOD).strength(5.0f, 3.0f).sound(SoundType.WOOD).noOcclusion().register();
    public static final RegistryObject<Block> FUEL = Iridynamics.REGISTRY.block("fuel", FuelBlock::new, Material.WOOD).strength(5.0f, 3.0f).sound(SoundType.WOOD).noOcclusion().noDrops().register();
    public static final RegistryObject<Block> CHUTE = Iridynamics.REGISTRY.block("chute", ChuteBlock::new, Material.STONE).strength(4.0f, 2.0f).sound(SoundType.STONE).noOcclusion().registerWithItem(ModCreativeTabs.BLOCK);
    public static final RegistryObject<Block> FORGE = Iridynamics.REGISTRY.block("forge", ForgeBlock::new, Material.STONE).strength(2.0f, 1.0f).sound(SoundType.STONE).noOcclusion().registerWithItem(ModCreativeTabs.BLOCK);
    public static final RegistryObject<Block> ANVIL = Iridynamics.REGISTRY.block("anvil", AnvilBlock::new, Material.METAL).strength(6.0f, 3.0f).sound(SoundType.METAL).noOcclusion().registerWithItem(ModCreativeTabs.BLOCK);
    public static final RegistryObject<Block> MOLD = Iridynamics.REGISTRY.block("mold", MoldBlock::new, Material.STONE).strength(2.0f, 1.0f).sound(SoundType.STONE).noOcclusion().register();
    public static final RegistryObject<Block> MOLD_TOOL = Iridynamics.REGISTRY.block("mold_tool", MoldToolBlock::new, Material.STONE).strength(2.0f, 1.0f).sound(SoundType.STONE).noOcclusion().register();
    public static final RegistryObject<Block> SMALL_CRUCIBLE = Iridynamics.REGISTRY.block("small_crucible", SmallCrucibleBlock::new, Material.STONE).strength(2.0f, 1.0f).sound(SoundType.STONE).noOcclusion().register();
    public static final RegistryObject<Block> BASIN = Iridynamics.REGISTRY.block("basin", BasinBlock::new, Material.WOOD).strength(2.0f, 1.0f).sound(SoundType.WOOD).noOcclusion().registerWithItem(ModCreativeTabs.BLOCK);
    public static final RegistryObject<Block> BENDING_ANVIL = Iridynamics.REGISTRY.block("bending_anvil", BendingAnvil::new, Material.METAL).strength(6.0f, 3.0f).sound(SoundType.METAL).noOcclusion().registerWithItem(ModCreativeTabs.BLOCK);

    public static void init() {

    }
}