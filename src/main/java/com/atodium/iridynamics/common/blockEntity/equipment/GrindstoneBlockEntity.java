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
import com.atodium.iridynamics.api.recipe.impl.GrindstoneRecipe;
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

public class GrindstoneBlockEntity extends SyncedBlockEntity implements ITickable, IBlockEntityGuiHolder<GrindstoneBlockEntity> {
    public static final BlockEntityCodec<GrindstoneBlockEntity> CODEC = BlockEntityCodec.createCodec(Iridynamics.rl("grindstone_block_entity"));
    public static final Component TITLE = new TranslatableComponent("gui.iridynamics.grindstone.title");
    public static final TextureArea BACKGROUND = TextureArea.createFullTexture(Iridynamics.rl("textures/gui/grindstone_background.png"));

    private boolean recipeUpdateFlag;
    private GrindstoneRecipe recipe;
    private final Inventory inventory;

    public GrindstoneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRINDSTONE.get(), pos, state);
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
        ToolInventoryContainer container = new ToolInventoryContainer(6, 4);
        for (int i = 0; i < 6; i++) container.setItem(i, this.inventory.getStackInSlot(i + 4));
        for (int i = 0; i < 4; i++) container.setTool(i, this.inventory.getStackInSlot(i));
        this.recipe = RecipeUtil.getRecipe(this.level, ModRecipeTypes.GRINDSTONE.get(), container);
        if (this.recipe != null) this.inventory.putOutputItem(this.recipe.assemble(container));
        else this.inventory.putOutputItem(ItemStack.EMPTY);
    }

    public void consumeRecipeItem() {
        if (this.recipe != null) {
            ToolInventoryContainer container = new ToolInventoryContainer(6, 4);
            for (int i = 0; i < 6; i++) container.setItem(i, this.inventory.getStackInSlot(i + 4));
            for (int i = 0; i < 4; i++) container.setTool(i, this.inventory.getStackInSlot(i));
            this.recipe.consume(container);
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public IGuiHolderCodec<IBlockEntityGuiHolder<GrindstoneBlockEntity>> getCodec() {
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
            super(11);
        }

        public void putOutputItem(ItemStack stack) {
            this.stacks.set(10, stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (slot == 10 && GrindstoneBlockEntity.this.recipe != null && this.getStackInSlot(10).isEmpty()) {
                GrindstoneBlockEntity.this.consumeRecipeItem();
                GrindstoneBlockEntity.this.recipe = null;
            }
            GrindstoneBlockEntity.this.markRecipeUpdate();
            GrindstoneBlockEntity.this.markDirty();
        }
    }
}