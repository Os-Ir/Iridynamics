package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.gui.IGuiHolderCodec;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import com.atodium.iridynamics.api.gui.TextureArea;
import com.atodium.iridynamics.api.gui.impl.BlockEntityCodec;
import com.atodium.iridynamics.api.gui.impl.IBlockEntityGuiHolder;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.RecipeUtil;
import com.atodium.iridynamics.api.recipe.container.ToolInventoryContainer;
import com.atodium.iridynamics.api.recipe.impl.ToolCraftingRecipe;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class ArtisanCraftingTableBlockEntity extends SyncedBlockEntity implements ITickable, IBlockEntityGuiHolder<ArtisanCraftingTableBlockEntity> {
    public static final BlockEntityCodec<ArtisanCraftingTableBlockEntity> CODEC = BlockEntityCodec.createCodec(Iridynamics.rl("artisan_crafting_table_block_entity"));
    public static final Component TITLE = new TranslatableComponent("gui.iridynamics.artisan_crafting_table.title");
    public static final TextureArea BACKGROUND = TextureArea.createFullTexture(Iridynamics.rl("textures/gui/artisan_crafting_table_background.png"));

    private boolean recipeUpdateFlag;
    private ToolCraftingRecipe recipe;
    private final Inventory inventory;

    public ArtisanCraftingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ARTISAN_CRAFTING_TABLE.get(), pos, state);
        this.inventory = new Inventory();
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (this.recipeUpdateFlag) {
            this.updateRecipe();
            this.recipeUpdateFlag = false;
        }
    }

    public void markRecipeUpdate() {
        this.recipeUpdateFlag = true;
    }

    public void updateRecipe() {
        ToolInventoryContainer container = new ToolInventoryContainer(25, 5);
        for (int i = 0; i < 25; i++) container.setItem(i, this.inventory.getStackInSlot(i + 5));
        for (int i = 0; i < 5; i++) container.setTool(i, this.inventory.getStackInSlot(i));
        this.recipe = RecipeUtil.getRecipe(this.level, ModRecipeTypes.TOOL_CRAFTING.get(), container);
        if (this.recipe != null) this.inventory.putOutputItem(this.recipe.assemble(container));
        else this.inventory.putOutputItem(ItemStack.EMPTY);
    }

    public void consumeRecipeItem() {
        if (this.recipe != null) {
            ToolInventoryContainer container = new ToolInventoryContainer(25, 5);
            for (int i = 0; i < 25; i++) container.setItem(i, this.inventory.getStackInSlot(i + 5));
            for (int i = 0; i < 5; i++) container.setTool(i, this.inventory.getStackInSlot(i));
            this.recipe.consume(container);
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public IGuiHolderCodec<IBlockEntityGuiHolder<ArtisanCraftingTableBlockEntity>> getCodec() {
        return CODEC;
    }

    @Override
    public Component getTitle(Player player) {
        return TITLE;
    }

    @Override
    public ModularGuiInfo createGuiInfo(Player player) {
        return ModularGuiInfo.builder(208, 200).background(BACKGROUND).playerInventory(player.getInventory(), 24, 118)
                .slot(0, 15, 16, this.inventory, 0)
                .slot(1, 15, 34, this.inventory, 1)
                .slot(2, 15, 52, this.inventory, 2)
                .slot(3, 15, 70, this.inventory, 3)
                .slot(4, 15, 88, this.inventory, 4)
                .slot(5, 43, 16, this.inventory, 5)
                .slot(6, 61, 16, this.inventory, 6)
                .slot(7, 79, 16, this.inventory, 7)
                .slot(8, 97, 16, this.inventory, 8)
                .slot(9, 115, 16, this.inventory, 9)
                .slot(10, 43, 34, this.inventory, 10)
                .slot(11, 61, 34, this.inventory, 11)
                .slot(12, 79, 34, this.inventory, 12)
                .slot(13, 97, 34, this.inventory, 13)
                .slot(14, 115, 34, this.inventory, 14)
                .slot(15, 43, 52, this.inventory, 15)
                .slot(16, 61, 52, this.inventory, 16)
                .slot(17, 79, 52, this.inventory, 17)
                .slot(18, 97, 52, this.inventory, 18)
                .slot(19, 115, 52, this.inventory, 19)
                .slot(20, 43, 70, this.inventory, 20)
                .slot(21, 61, 70, this.inventory, 21)
                .slot(22, 79, 70, this.inventory, 22)
                .slot(23, 97, 70, this.inventory, 23)
                .slot(24, 115, 70, this.inventory, 24)
                .slot(25, 43, 88, this.inventory, 25)
                .slot(26, 61, 88, this.inventory, 26)
                .slot(27, 79, 88, this.inventory, 27)
                .slot(28, 97, 88, this.inventory, 28)
                .slot(29, 115, 88, this.inventory, 29)
                .slot(30, 173, 52, this.inventory, 30, false, true)
                .build(player);
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.markRecipeUpdate();
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.markRecipeUpdate();
    }

    public class Inventory extends ItemStackHandler {
        public Inventory() {
            super(31);
        }

        public void putOutputItem(ItemStack stack) {
            this.stacks.set(30, stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (slot == 30 && ArtisanCraftingTableBlockEntity.this.recipe != null && this.getStackInSlot(30).isEmpty()) {
                ArtisanCraftingTableBlockEntity.this.consumeRecipeItem();
                ArtisanCraftingTableBlockEntity.this.recipe = null;
            }
            ArtisanCraftingTableBlockEntity.this.markRecipeUpdate();
            ArtisanCraftingTableBlockEntity.this.markDirty();
        }
    }
}