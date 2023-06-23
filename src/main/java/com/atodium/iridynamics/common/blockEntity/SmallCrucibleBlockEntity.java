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
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import com.atodium.iridynamics.api.module.BlockHeatModule;
import com.atodium.iridynamics.common.module.SmallCrucibleModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class SmallCrucibleBlockEntity extends SyncedBlockEntity implements ITickable, IBlockEntityHolder<SmallCrucibleBlockEntity> {
    public static final BlockEntityCodec<SmallCrucibleBlockEntity> CODEC = BlockEntityCodec.createCodec(Iridynamics.rl("small_crucible_block_entity"));
    public static final Component TITLE = new TranslatableComponent("gui.iridynamics.small_crucible.title");
    public static final TextureArea BACKGROUND = TextureArea.createFullTexture(Iridynamics.rl("textures/gui/small_crucible_background.png"));

    private final Inventory inventory;
    private final LiquidContainerCapability container;
    private final HeatCapability heat;

    public SmallCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMALL_CRUCIBLE.get(), pos, state);
        this.inventory = new Inventory();
        this.container = new LiquidContainerCapability(SmallCrucibleModule.CAPACITY);
        this.heat = new HeatCapability(new SolidPhasePortrait(SmallCrucibleModule.HEAT_CAPACITY), SmallCrucibleModule.RESISTANCE);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            BlockHeatModule.blockHeatExchange(level, pos, state, this, false);
            SmallCrucibleModule.updateData(this.inventory, this.container, this.heat);
            this.markDirty();
            this.markForSync();
        }
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
            return SmallCrucibleModule.validateItem(stack);
        }
    }
}