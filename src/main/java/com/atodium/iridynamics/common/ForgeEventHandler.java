package com.atodium.iridynamics.common;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.IIgnitable;
import com.atodium.iridynamics.api.capability.ForgingCapability;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.InventoryCapability;
import com.atodium.iridynamics.api.capability.LiquidContainerCapability;
import com.atodium.iridynamics.api.heat.HeatUtil;
import com.atodium.iridynamics.api.heat.impl.MaterialPhasePortrait;
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.MaterialInfoLoader;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.recipe.JsonRecipeLoader;
import com.atodium.iridynamics.api.recipe.impl.*;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.blockEntity.*;
import com.atodium.iridynamics.common.item.ModItems;
import com.atodium.iridynamics.common.tool.ToolIgniter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Iridynamics.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        PileHeatRecipe.resetCache();
        DryingRecipe.resetCache();
        WashingRecipe.resetCache();
        ToolCraftingRecipe.resetCache();
        GrindstoneRecipe.resetCache();
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        JsonRecipeLoader.INSTANCE.onDatapackSync(event);
        MaterialInfoLoader.INSTANCE.onDatapackSync(event);
    }

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(JsonRecipeLoader.INSTANCE);
        event.addListener(MaterialInfoLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void attachItemCapability(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        Item item = stack.getItem();
        if (item == ModItems.IGNITER.get())
            event.addCapability(HeatCapability.KEY, new HeatCapability(new SolidPhasePortrait(800.0), 0.4));
        else if (item == ModItems.SMALL_CRUCIBLE.get()) {
            event.addCapability(LiquidContainerCapability.KEY, new LiquidContainerCapability(SmallCrucibleBlockEntity.CAPACITY));
            event.addCapability(HeatCapability.KEY, new HeatCapability(new SolidPhasePortrait(16000.0), 0.2));
            event.addCapability(InventoryCapability.KEY, new InventoryCapability(4));
        } else if (item == ModItems.MOLD.get()) {
            event.addCapability(LiquidContainerCapability.KEY, new LiquidContainerCapability(MoldBlockEntity.CAPACITY));
        } else if (item == ModItems.MOLD_TOOL.get()) {
            event.addCapability(LiquidContainerCapability.KEY, new LiquidContainerCapability(MoldBlockEntity.CAPACITY));
        } else if (MaterialEntry.containsMaterialEntry(stack)) {
            MaterialEntry entry = MaterialEntry.getItemMaterialEntry(stack);
            MaterialBase material = entry.material();
            SolidShape shape = entry.shape();
            if (material.hasHeatInfo()) {
                event.addCapability(HeatCapability.KEY, new HeatCapability(new MaterialPhasePortrait(material.getHeatInfo(), shape.getUnit() / 144.0)));
                if (shape.hasForgeShape()) event.addCapability(ForgingCapability.KEY, new ForgingCapability(shape));
            }
        }
    }

    @SubscribeEvent
    public static void updateItemTemperature(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player player) {
            Inventory inventory = player.getInventory();
            inventory.items.forEach((stack) -> {
                stack.getCapability(HeatCapability.HEAT).ifPresent((heat) -> HeatUtil.heatExchange(heat, HeatUtil.AMBIENT_TEMPERATURE, heat.getResistance(Direction.UP) + HeatUtil.RESISTANCE_AIR_FLOW));
                Item item = stack.getItem();
                if (item == ModItems.SMALL_CRUCIBLE.get())
                    SmallCrucibleBlockEntity.updateSmallCrucible(stack.getCapability(InventoryCapability.INVENTORY).orElseThrow(NullPointerException::new), (LiquidContainerCapability) stack.getCapability(LiquidContainerCapability.LIQUID_CONTAINER).orElseThrow(NullPointerException::new), (HeatCapability) stack.getCapability(HeatCapability.HEAT).orElseThrow(NullPointerException::new));
                else if (item == ModItems.MOLD.get())
                    MoldBlockEntity.updateMold((LiquidContainerCapability) stack.getCapability(LiquidContainerCapability.LIQUID_CONTAINER).orElseThrow(NullPointerException::new));
                else if (item == ModItems.MOLD_TOOL.get())
                    MoldToolBlockEntity.updateMold((LiquidContainerCapability) stack.getCapability(LiquidContainerCapability.LIQUID_CONTAINER).orElseThrow(NullPointerException::new));
            });
            inventory.offhand.forEach((stack) -> {
                stack.getCapability(HeatCapability.HEAT).ifPresent((heat) -> HeatUtil.heatExchange(heat, HeatUtil.AMBIENT_TEMPERATURE, heat.getResistance(Direction.UP) + HeatUtil.RESISTANCE_AIR_FLOW));
                Item item = stack.getItem();
                if (item == ModItems.SMALL_CRUCIBLE.get())
                    SmallCrucibleBlockEntity.updateSmallCrucible(stack.getCapability(InventoryCapability.INVENTORY).orElseThrow(NullPointerException::new), (LiquidContainerCapability) stack.getCapability(LiquidContainerCapability.LIQUID_CONTAINER).orElseThrow(NullPointerException::new), (HeatCapability) stack.getCapability(HeatCapability.HEAT).orElseThrow(NullPointerException::new));
                else if (item == ModItems.MOLD.get())
                    MoldBlockEntity.updateMold((LiquidContainerCapability) stack.getCapability(LiquidContainerCapability.LIQUID_CONTAINER).orElseThrow(NullPointerException::new));
                else if (item == ModItems.MOLD_TOOL.get())
                    MoldToolBlockEntity.updateMold((LiquidContainerCapability) stack.getCapability(LiquidContainerCapability.LIQUID_CONTAINER).orElseThrow(NullPointerException::new));
            });
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
                    if (crucible.setup(stack)) stack.shrink(1);
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