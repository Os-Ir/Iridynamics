package com.atodium.iridynamics.client;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.client.model.DynamicTextureLoader;
import com.atodium.iridynamics.client.model.material.MaterialModelLoader;
import com.atodium.iridynamics.client.model.tool.MaterialToolModelLoader;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.client.renderer.block.*;
import com.atodium.iridynamics.client.renderer.entity.BulletRenderer;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.atodium.iridynamics.common.blockEntity.PileBlockEntity;
import com.atodium.iridynamics.common.entity.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Iridynamics.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEventHandler {
    @SubscribeEvent
    public static void registerModelLoaders(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(Iridynamics.rl("material"), MaterialModelLoader.INSTANCE);
        ModelLoaderRegistry.registerLoader(Iridynamics.rl("material_tool"), MaterialToolModelLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PILE.get(), (context) -> PileRenderer.INSTANCE);
        event.registerBlockEntityRenderer(ModBlockEntities.HEAT_PROCESS.get(), (context) -> HeatProcessRenderer.INSTANCE);
        event.registerBlockEntityRenderer(ModBlockEntities.FORGE.get(), (context) -> ForgeRenderer.INSTANCE);
        event.registerBlockEntityRenderer(ModBlockEntities.ANVIL.get(), (context) -> AnvilRenderer.INSTANCE);
        event.registerBlockEntityRenderer(ModBlockEntities.SMALL_CRUCIBLE.get(), (context) -> SmallCrucibleRenderer.INSTANCE);
        event.registerBlockEntityRenderer(ModBlockEntities.MOLD.get(), (context) -> MoldRenderer.INSTANCE);
        event.registerEntityRenderer(ModEntities.BULLET.get(), BulletRenderer::new);
    }

    @SubscribeEvent
    public static void onTextureStitchPre(TextureStitchEvent.Pre event) {
        if (event.getAtlas().location().equals(RendererUtil.BLOCKS_ATLAS)) {
            PileBlockEntity.PILE_ITEM.values().forEach((info) -> event.addSprite(info.getTextureName()));
            event.addSprite(Iridynamics.rl("block/white"));
        }
    }

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        DynamicTextureLoader.INSTANCE.init(event);
    }
}