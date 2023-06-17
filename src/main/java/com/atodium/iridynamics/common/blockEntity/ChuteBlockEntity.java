package com.atodium.iridynamics.common.blockEntity;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.gui.IGuiHolderCodec;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import com.atodium.iridynamics.api.gui.TextureArea;
import com.atodium.iridynamics.api.gui.impl.BlockEntityCodec;
import com.atodium.iridynamics.api.gui.impl.IBlockEntityHolder;
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

public class ChuteBlockEntity extends SyncedBlockEntity implements ITickable, IBlockEntityHolder<ChuteBlockEntity> {
    public static final BlockEntityCodec<ChuteBlockEntity> CODEC = BlockEntityCodec.createCodec(new ResourceLocation(Iridynamics.MODID, "chute_block_entity"));
    public static final Component TITLE = new TranslatableComponent("gui.iridynamics.chute.title");
    public static final TextureArea BACKGROUND = TextureArea.createFullTexture(new ResourceLocation(Iridynamics.MODID, "textures/gui/chute_background.png"));
    private final Inventory inventory;

    public ChuteBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHUTE.get(), pos, state);
        this.inventory = new Inventory();
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            this.markDirty();
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public IGuiHolderCodec<IBlockEntityHolder<ChuteBlockEntity>> getCodec() {
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
    protected CompoundTag writeSyncData(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
    }

    public static class Inventory extends ItemStackHandler {
        public Inventory() {
            super(4);
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
        public boolean isItemValid(int slot, ItemStack stack) {
            return PileBlockEntity.PILE_ITEM.containsKey(stack.getItem());
        }
    }
}