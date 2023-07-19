package com.atodium.iridynamics;

import com.atodium.iridynamics.api.gui.plan.PlanGuiCodec;
import com.atodium.iridynamics.api.material.ModMaterials;
import com.atodium.iridynamics.api.material.ModSolidShapes;
import com.atodium.iridynamics.api.recipe.ModOutputDecorators;
import com.atodium.iridynamics.api.recipe.ModRecipeSerializers;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.registry.ModRegistry;
import com.atodium.iridynamics.api.research.ResearchModule;
import com.atodium.iridynamics.api.rotate.RotateModule;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.atodium.iridynamics.common.entity.ModEntities;
import com.atodium.iridynamics.common.item.ModItems;
import com.atodium.iridynamics.common.level.levelgen.feature.ModFeatures;
import com.atodium.iridynamics.network.ModNetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Iridynamics.MODID)
public class Iridynamics {
    public static final String MODID = "iridynamics";
    public static final String MODNAME = "Iridynamics";
    public static final String VERSION = "1.1.1";

    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final ModRegistry REGISTRY = new ModRegistry(MODID);

    public Iridynamics() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        bus.addListener(this::loadComplete);
        REGISTRY.init(bus);
        ModMaterials.register();
        ModSolidShapes.register();
        ModBlocks.init();
        ModItems.init();
        ModEntities.init();
        ModBlockEntities.init();
        ModRecipeTypes.init();
        ModRecipeSerializers.init();
        ModOutputDecorators.init();
        ModFeatures.init();
        ModNetworkHandler.init();
        PlanGuiCodec.init();
        RotateModule.init();
        ResearchModule.init();
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }

    public void setup(FMLCommonSetupEvent event) {
        ModBlocks.setup();
        ModItems.setup();
        ModFeatures.setup();
    }

    public void loadComplete(FMLLoadCompleteEvent event) {

    }
}