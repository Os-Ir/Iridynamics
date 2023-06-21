package com.atodium.iridynamics.common.blockEntity;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.gui.IGuiHolderCodec;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import com.atodium.iridynamics.api.gui.TextureArea;
import com.atodium.iridynamics.api.gui.impl.BlockEntityCodec;
import com.atodium.iridynamics.api.gui.impl.IBlockEntityHolder;
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class BonfireBlockEntity extends SyncedBlockEntity implements ITickable, IBlockEntityHolder<BonfireBlockEntity> {
    public static final BlockEntityCodec<BonfireBlockEntity> CODEC = BlockEntityCodec.createCodec(new ResourceLocation(Iridynamics.MODID, "bonfire_block_entity"));
    public static final Component TITLE = new TranslatableComponent("gui.iridynamics.bonfire.title");
    public static final TextureArea BACKGROUND = TextureArea.createFullTexture(new ResourceLocation(Iridynamics.MODID, "textures/gui/bonfire_background.png"));

    private final Inventory inventory;
    private final HeatCapability heat;

    public BonfireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BONFIRE.get(), pos, state);
        this.inventory = new Inventory();
        this.heat = new HeatCapability(new SolidPhasePortrait(16000.0), new double[]{0.1, 0.4, 0.2, 0.2, 0.2, 0.2});
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {

    }

    @Override
    public IGuiHolderCodec<IBlockEntityHolder<BonfireBlockEntity>> getCodec() {
        return CODEC;
    }

    @Override
    public Component getTitle(Player player) {
        return TITLE;
    }

    @Override
    public ModularGuiInfo createGuiInfo(Player player) {
        return ModularGuiInfo.builder().background(BACKGROUND).playerInventory(player.getInventory())
                .build(player);
    }

    @Override
    protected CompoundTag writeSyncData(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
        tag.put("heat", this.heat.serializeNBT());
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.heat.deserializeNBT(tag.getCompound("heat"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
        tag.put("heat", this.heat.serializeNBT());
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.heat.deserializeNBT(tag.getCompound("heat"));
    }

    public static class Inventory extends ItemStackHandler {
        public Inventory() {
            super(2);
        }

        public ItemStack get(int slot) {
            return this.getStackInSlot(slot);
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
            return stack.getCapability(HeatCapability.HEAT).isPresent();
        }
    }
}