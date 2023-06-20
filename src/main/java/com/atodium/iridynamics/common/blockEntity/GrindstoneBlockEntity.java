package com.atodium.iridynamics.common.blockEntity;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.gui.IGuiHolderCodec;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import com.atodium.iridynamics.api.gui.TextureArea;
import com.atodium.iridynamics.api.gui.impl.BlockEntityCodec;
import com.atodium.iridynamics.api.gui.impl.IBlockEntityHolder;
import com.atodium.iridynamics.api.recipe.container.ToolInventoryContainer;
import com.atodium.iridynamics.api.recipe.impl.GrindstoneRecipe;
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

public class GrindstoneBlockEntity extends SyncedBlockEntity implements IBlockEntityHolder<GrindstoneBlockEntity> {
    public static final BlockEntityCodec<GrindstoneBlockEntity> CODEC = BlockEntityCodec.createCodec(new ResourceLocation(Iridynamics.MODID, "grindstone_block_entity"));
    public static final Component TITLE = new TranslatableComponent("gui.iridynamics.grindstone.title");
    public static final TextureArea BACKGROUND = TextureArea.createFullTexture(new ResourceLocation(Iridynamics.MODID, "textures/gui/grindstone_background.png"));

    private GrindstoneRecipe recipe;
    private final Inventory inventory;

    public GrindstoneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRINDSTONE.get(), pos, state);
        this.inventory = new Inventory();
    }

    public void updateRecipe() {
        ToolInventoryContainer container = new ToolInventoryContainer(6, 4);
        for (int i = 0; i < 6; i++) container.setItem(i, this.inventory.getStackInSlot(i + 4));
        for (int i = 0; i < 4; i++)
            container.setTool(i, this.inventory.getStackInSlot(i).getItem() instanceof ToolItem toolItem ? toolItem.getToolInfo() : null);
        this.recipe = GrindstoneRecipe.getRecipe(container, this.level);
        if (this.recipe != null) this.inventory.putOutputItem(this.recipe.assemble(container));
        else this.inventory.putOutputItem(ItemStack.EMPTY);
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
                .slot(0, 8, 8, this.inventory, 0)
                .slot(1, 26, 8, this.inventory, 1)
                .slot(2, 44, 8, this.inventory, 2)
                .slot(3, 62, 8, this.inventory, 3)
                .slot(4, 30, 37, this.inventory, 4)
                .slot(5, 48, 37, this.inventory, 5)
                .slot(6, 66, 37, this.inventory, 6)
                .slot(7, 30, 55, this.inventory, 7)
                .slot(8, 48, 55, this.inventory, 8)
                .slot(9, 66, 55, this.inventory, 9)
                .slot(10, 124, 46, this.inventory, 10, false, true)
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
            super(11);
        }

        public void putOutputItem(ItemStack stack) {
            this.stacks.set(10, stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (slot == 10 && GrindstoneBlockEntity.this.recipe != null && this.getStackInSlot(10).isEmpty()) {
                for (int i = 4; i <= 9; i++) this.stacks.set(i, ItemStack.EMPTY);
                for (int i = 0; i <= 3; i++) {
                    ItemStack stack = this.getStackInSlot(i);
                    if (stack.getItem() instanceof ToolItem toolItem)
                        toolItem.damageItem(stack, toolItem.getToolInfo().getContainerCraftDamage());
                }
                GrindstoneBlockEntity.this.recipe = null;
            }
            GrindstoneBlockEntity.this.updateRecipe();
        }
    }
}