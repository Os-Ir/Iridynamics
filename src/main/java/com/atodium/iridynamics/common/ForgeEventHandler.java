package com.atodium.iridynamics.common;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.IIgnitable;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.LiquidContainerCapability;
import com.atodium.iridynamics.api.capability.PotteryCapability;
import com.atodium.iridynamics.api.capability.ResearchCapability;
import com.atodium.iridynamics.api.heat.HeatUtil;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.MaterialInfoLoader;
import com.atodium.iridynamics.api.module.CarvingModule;
import com.atodium.iridynamics.api.module.ItemHeatModule;
import com.atodium.iridynamics.api.module.LiquidContainerModule;
import com.atodium.iridynamics.api.research.ResearchNodeLoader;
import com.atodium.iridynamics.api.recipe.JsonRecipeLoader;
import com.atodium.iridynamics.api.recipe.RecipeUtil;
import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.atodium.iridynamics.common.blockEntity.PileBlockEntity;
import com.atodium.iridynamics.common.blockEntity.equipment.MoldBlockEntity;
import com.atodium.iridynamics.common.blockEntity.equipment.MoldToolBlockEntity;
import com.atodium.iridynamics.common.item.ModItems;
import com.atodium.iridynamics.common.level.levelgen.feature.ModFeatures;
import com.atodium.iridynamics.common.module.SmallCrucibleModule;
import com.atodium.iridynamics.common.tool.ToolIgniter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Map;

@Mod.EventBusSubscriber(modid = Iridynamics.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        BiomeGenerationSettingsBuilder generation = event.getGeneration();
        for (Map.Entry<ResourceLocation, Holder<PlacedFeature>> entry : ModFeatures.PLACED_FEATURE.entrySet())
            generation.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, entry.getValue());
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        RecipeUtil.clearCache();
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        JsonRecipeLoader.INSTANCE.onDatapackSync(event);
        MaterialInfoLoader.INSTANCE.onDatapackSync(event);
        ResearchNodeLoader.INSTANCE.onDatapackSync(event);
    }

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(JsonRecipeLoader.INSTANCE);
        event.addListener(MaterialInfoLoader.INSTANCE);
        event.addListener(ResearchNodeLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void attachItemCapability(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        Item item = stack.getItem();
        if (item == ModItems.IGNITER.get()) ItemHeatModule.addItemHeat(event, stack, 800.0, 0.4);
        else if (item == ModItems.MOLD.get())
            LiquidContainerModule.addItemLiquidContainer(event, MoldBlockEntity.CAPACITY);
        else if (item == ModItems.MOLD_TOOL.get())
            LiquidContainerModule.addItemLiquidContainer(event, MoldBlockEntity.CAPACITY);
        else if (item == Items.CHICKEN) ItemHeatModule.addItemHeat(event, stack, 16000.0, 0.2);
        else if (item == ModItems.UNFIRED_SMALL_CRUCIBLE.get()) ItemHeatModule.addItemHeat(event, stack, 16000.0, 0.2);
        else if (item == ModItems.SMALL_CRUCIBLE.get()) SmallCrucibleModule.initItem(event, stack);
        else if (item == ModItems.MOLD_CLAY_ADOBE.get()) CarvingModule.addItemCarving(event, stack, 3, 0x788c96);
        else if (item == ModItems.POT_CLAY_ADOBE.get())
            event.addCapability(PotteryCapability.KEY, new PotteryCapability());
        else if (MaterialEntry.containsMaterialEntry(stack))
            HeatUtil.addMaterialItemCapability(event, MaterialEntry.getItemMaterialEntry(stack));
    }

    @SubscribeEvent
    public static void attachPlayerCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player)
            event.addCapability(ResearchCapability.KEY, new ResearchCapability());
    }

    @SubscribeEvent
    public static void updateItemTemperature(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player player) {
            DataUtil.updateAllItems(player, (stack) -> {
                stack.getCapability(HeatCapability.HEAT).ifPresent((heat) -> ItemHeatModule.heatExchange(heat, ItemHeatModule.AMBIENT_TEMPERATURE, heat.getResistance(Direction.UP) + ItemHeatModule.RESISTANCE_AIR_FLOW));
                Item item = stack.getItem();
                if (item == ModItems.SMALL_CRUCIBLE.get()) SmallCrucibleModule.updateData(stack);
                else if (item == ModItems.MOLD.get())
                    MoldBlockEntity.updateMold((LiquidContainerCapability) stack.getCapability(LiquidContainerCapability.LIQUID_CONTAINER).orElseThrow(NullPointerException::new));
                else if (item == ModItems.MOLD_TOOL.get())
                    MoldToolBlockEntity.updateMold((LiquidContainerCapability) stack.getCapability(LiquidContainerCapability.LIQUID_CONTAINER).orElseThrow(NullPointerException::new));
            });
        }
    }

    @SubscribeEvent
    public static void flintWork(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getWorld();
        if (level.isClientSide) return;
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Player player = event.getPlayer();
        if (!player.isShiftKeyDown()) return;
        ItemStack stack = player.getItemInHand(event.getHand());
        Item item = stack.getItem();
        if (event.getFace() == Direction.UP && state.getMaterial() == Material.STONE && state.isFaceSturdy(level, pos, Direction.UP) && item == Items.FLINT) {
            stack.shrink(1);
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModItems.POLISHED_FLINT.get()));
        }
    }

    @SubscribeEvent
    public static void putItemBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getWorld();
        if (level.isClientSide) return;
        BlockPos pos = event.getPos();
        Player player = event.getPlayer();
        if (!player.isShiftKeyDown()) return;
        Direction direction = event.getFace();
        if (direction == null) direction = Direction.UP;
        BlockPos posAbove = pos.relative(direction);
        BlockState state = level.getBlockState(pos);
        ItemStack stack = player.getItemInHand(event.getHand());
        Item item = stack.getItem();
        if (state.getBlock() != ModBlocks.PILE.get() && PileBlockEntity.PILE_ITEM.containsKey(item) && state.isFaceSturdy(level, pos, Direction.UP)) {
            if (level.setBlockAndUpdate(posAbove, ModBlocks.PILE.get().defaultBlockState()))
                level.getBlockEntity(posAbove, ModBlockEntities.PILE.get()).ifPresent((pile) -> {
                    if (pile.setup(item) && !player.isCreative()) stack.shrink(1);
                });
        } else if (item == ModItems.SMALL_CRUCIBLE.get() && state.isFaceSturdy(level, pos, Direction.UP)) {
            if (level.setBlockAndUpdate(posAbove, ModBlocks.SMALL_CRUCIBLE.get().defaultBlockState()))
                level.getBlockEntity(posAbove, ModBlockEntities.SMALL_CRUCIBLE.get()).ifPresent((crucible) -> {
                    SmallCrucibleModule.setupBlock(crucible, stack);
                    stack.shrink(1);
                });
        } else if (item == ModItems.MOLD.get() && state.isFaceSturdy(level, pos, Direction.UP)) {
            if (level.setBlockAndUpdate(posAbove, ModBlocks.MOLD.get().defaultBlockState()))
                level.getBlockEntity(posAbove, ModBlockEntities.MOLD.get()).ifPresent((mold) -> {
                    if (mold.setup(stack)) stack.shrink(1);
                });
        } else if (item == ModItems.MOLD_TOOL.get() && state.isFaceSturdy(level, pos, Direction.UP)) {
            if (level.setBlockAndUpdate(posAbove, ModBlocks.MOLD_TOOL.get().defaultBlockState()))
                level.getBlockEntity(posAbove, ModBlockEntities.MOLD_TOOL.get()).ifPresent((mold) -> {
                    if (mold.setup(stack)) stack.shrink(1);
                });
        }
    }

    @SubscribeEvent
    public static void igniteBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getWorld();
        if (level.isClientSide) return;
        BlockPos pos = event.getPos();
        Player player = event.getPlayer();
        Direction direction = event.getFace();
        if (direction == null) direction = Direction.UP;
        BlockState state = level.getBlockState(pos);
        BlockEntity entity = level.getBlockEntity(pos);
        ItemStack stack = player.getItemInHand(event.getHand());
        if (entity instanceof IIgnitable ignitable && stack.getItem() == ModItems.IGNITER.get())
            ToolIgniter.igniteBlock(stack, ignitable, direction);
    }
}