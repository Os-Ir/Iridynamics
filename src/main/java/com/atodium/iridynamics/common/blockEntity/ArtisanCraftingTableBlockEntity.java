package com.atodium.iridynamics.common.blockEntity;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.gui.IGuiHolderCodec;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import com.atodium.iridynamics.api.gui.TextureArea;
import com.atodium.iridynamics.api.gui.impl.BlockEntityCodec;
import com.atodium.iridynamics.api.gui.impl.IBlockEntityHolder;
import com.atodium.iridynamics.api.recipe.impl.ToolCraftingRecipe;
import com.atodium.iridynamics.api.tool.ToolItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class ArtisanCraftingTableBlockEntity extends SyncedBlockEntity implements IBlockEntityHolder<GrindstoneBlockEntity> {
    public static final BlockEntityCodec<GrindstoneBlockEntity> CODEC = BlockEntityCodec.createCodec(new ResourceLocation(Iridynamics.MODID, "artisan_crafting_table_block_entity"));
    public static final Component TITLE = new TranslatableComponent("gui.iridynamics.artisan_crafting_table.title");
    public static final TextureArea BACKGROUND = TextureArea.createFullTexture(new ResourceLocation(Iridynamics.MODID, "textures/gui/artisan_crafting_table_background.png"));

    private ToolCraftingRecipe recipe;
    private final Inventory inventory;

    public ArtisanCraftingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ARTISAN_CRAFTING_TABLE.get(), pos, state);
        this.inventory = new Inventory();
    }

    public void updateRecipe() {

    }

    @Override
    public IGuiHolderCodec<IBlockEntityHolder<GrindstoneBlockEntity>> getCodec() {
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
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.updateRecipe();
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.updateRecipe();
    }

    public class Inventory extends ItemStackHandler {
        public Inventory() {
            super(14);
        }

        public void putOutputItem(ItemStack stack) {
            this.stacks.set(13, stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (slot == 13 && ArtisanCraftingTableBlockEntity.this.recipe != null && this.getStackInSlot(13).isEmpty()) {
                for (int i = 4; i <= 12; i++) this.stacks.set(i, ItemStack.EMPTY);
                for (int i = 0; i <= 3; i++) {
                    ItemStack stack = this.getStackInSlot(i);
                    if (stack.getItem() instanceof ToolItem toolItem)
                        toolItem.damageItem(stack, toolItem.getToolInfo().getContainerCraftDamage());
                }
                ArtisanCraftingTableBlockEntity.this.recipe = null;
            }
            ArtisanCraftingTableBlockEntity.this.updateRecipe();
        }
    }
}