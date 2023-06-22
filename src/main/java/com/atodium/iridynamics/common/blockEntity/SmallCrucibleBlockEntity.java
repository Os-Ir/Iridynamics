package com.atodium.iridynamics.common.blockEntity;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.InventoryCapability;
import com.atodium.iridynamics.api.capability.LiquidContainerCapability;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import com.atodium.iridynamics.api.gui.TextureArea;
import com.atodium.iridynamics.api.gui.impl.BlockEntityCodec;
import com.atodium.iridynamics.api.gui.impl.IBlockEntityHolder;
import com.atodium.iridynamics.api.heat.HeatUtil;
import com.atodium.iridynamics.api.heat.MaterialHeatInfo;
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.common.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class SmallCrucibleBlockEntity extends SyncedBlockEntity implements ITickable, IBlockEntityHolder<SmallCrucibleBlockEntity> {
    public static final BlockEntityCodec<SmallCrucibleBlockEntity> CODEC = BlockEntityCodec.createCodec(Iridynamics.rl("small_crucible_block_entity"));
    public static final Component TITLE = new TranslatableComponent("gui.iridynamics.small_crucible.title");
    public static final TextureArea BACKGROUND = TextureArea.createFullTexture(Iridynamics.rl("textures/gui/small_crucible_background.png"));
    public static final double INVENTORY_RESISTANCE = 0.02;
    public static final int CAPACITY = 576;

    private final Inventory inventory;
    private final LiquidContainerCapability container;
    private final HeatCapability heat;

    public SmallCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMALL_CRUCIBLE.get(), pos, state);
        this.inventory = new Inventory();
        this.container = new LiquidContainerCapability(CAPACITY);
        this.heat = new HeatCapability(new SolidPhasePortrait(16000.0), new double[]{0.03, 0.3, 0.2, 0.2, 0.2, 0.2});
    }

    public static boolean validateItem(ItemStack stack) {
        if (!stack.getCapability(HeatCapability.HEAT).isPresent() || !MaterialEntry.containsMaterialEntry(stack))
            return false;
        return MaterialEntry.getItemMaterialEntry(stack).material().hasHeatInfo();
    }

    public static void updateSmallCrucible(IItemHandlerModifiable inventory, LiquidContainerCapability container, HeatCapability heat) {
        if (!container.isEmpty()) HeatUtil.heatExchange(heat, container, INVENTORY_RESISTANCE);
        for (int i = 0; i < 4; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!validateItem(stack)) continue;
            MaterialEntry entry = MaterialEntry.getItemMaterialEntry(stack);
            MaterialHeatInfo info = MaterialEntry.getItemMaterialEntry(stack).material().getHeatInfo();
            HeatCapability itemHeat = (HeatCapability) stack.getCapability(HeatCapability.HEAT).orElseThrow(NullPointerException::new);
            HeatUtil.heatExchange(heat, itemHeat, INVENTORY_RESISTANCE + itemHeat.getResistance());
            if (itemHeat.getTemperature() > info.getMeltingPoint()) {
                container.addMaterial(entry.material(), entry.shape().getUnit());
                container.increaseEnergy(itemHeat.getEnergy());
                inventory.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            HeatUtil.blockHeatExchange(level, pos, state, this, false);
            updateSmallCrucible(this.inventory, this.container, this.heat);
            this.markDirty();
            this.markForSync();
        }
    }

    public boolean setup(ItemStack stack) {
        if (stack.getItem() == ModItems.SMALL_CRUCIBLE.get()) {
            this.inventory.deserializeNBT(((InventoryCapability) stack.getCapability(InventoryCapability.INVENTORY).orElseThrow(NullPointerException::new)).serializeNBT());
            this.container.deserializeNBT(((LiquidContainerCapability) stack.getCapability(LiquidContainerCapability.LIQUID_CONTAINER).orElseThrow(NullPointerException::new)).serializeNBT());
            this.heat.setEnergy(stack.getCapability(HeatCapability.HEAT).orElseThrow(NullPointerException::new).getEnergy());
            return true;
        }
        return false;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public LiquidContainerCapability getLiquidContainer() {
        return this.container;
    }

    public HeatCapability getHeat() {
        return this.heat;
    }

    @Override
    public BlockEntityCodec<SmallCrucibleBlockEntity> getCodec() {
        return CODEC;
    }

    @Override
    public Component getTitle(Player player) {
        return TITLE;
    }

    @Override
    public ModularGuiInfo createGuiInfo(Player player) {
        return ModularGuiInfo.builder().background(BACKGROUND).playerInventory(player.getInventory())
                .slot(0, 53, 35, this.inventory, 0)
                .slot(1, 71, 35, this.inventory, 1)
                .slot(2, 89, 35, this.inventory, 2)
                .slot(3, 107, 35, this.inventory, 3)
                .build(player);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction direction) {
        if (capability == LiquidContainerCapability.LIQUID_CONTAINER)
            return LazyOptional.of(() -> this.container).cast();
        if (capability == HeatCapability.HEAT) return LazyOptional.of(() -> this.heat).cast();
        if (capability == InventoryCapability.INVENTORY) return LazyOptional.of(() -> this.inventory).cast();
        return super.getCapability(capability, direction);
    }

    @Override
    protected CompoundTag writeSyncData(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
        tag.put("container", this.container.serializeNBT());
        tag.put("heat", this.heat.serializeNBT());
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.container.deserializeNBT(tag.getCompound("container"));
        this.heat.deserializeNBT(tag.getCompound("heat"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
        tag.put("container", this.container.serializeNBT());
        tag.put("heat", this.heat.serializeNBT());
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.container.deserializeNBT(tag.getCompound("container"));
        this.heat.deserializeNBT(tag.getCompound("heat"));
    }

    public static class Inventory extends ItemStackHandler {
        public Inventory() {
            super(4);
        }

        public ItemStack take(int slot) {
            this.validateSlotIndex(slot);
            ItemStack stack = this.getStackInSlot(slot);
            this.setStackInSlot(slot, ItemStack.EMPTY);
            return stack;
        }

        public ItemStack put(int slot, ItemStack stack) {
            return this.insertItem(slot, stack, false);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return validateItem(stack);
        }
    }
}