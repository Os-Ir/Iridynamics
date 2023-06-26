package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.PotteryCapability;
import com.atodium.iridynamics.api.gui.IGuiHolderCodec;
import com.atodium.iridynamics.api.gui.ModularContainer;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import com.atodium.iridynamics.api.gui.TextureArea;
import com.atodium.iridynamics.api.gui.impl.BlockEntityCodec;
import com.atodium.iridynamics.api.gui.impl.IBlockEntityHolder;
import com.atodium.iridynamics.api.gui.plan.IPlanBlockEntity;
import com.atodium.iridynamics.api.gui.plan.PlanGuiHolder;
import com.atodium.iridynamics.api.gui.widget.ButtonWidget;
import com.atodium.iridynamics.api.gui.widget.IWidgetRenderer;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.RecipeUtil;
import com.atodium.iridynamics.api.recipe.container.ItemStackContainer;
import com.atodium.iridynamics.api.recipe.impl.PotteryRecipe;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class PotteryWorkTableBlockEntity extends SyncedBlockEntity implements ITickable, IBlockEntityHolder<PotteryWorkTableBlockEntity>, IPlanBlockEntity {
    public static final BlockEntityCodec<PotteryWorkTableBlockEntity> CODEC = BlockEntityCodec.createCodec(Iridynamics.rl("pottery_work_table_block_entity"));
    public static final Component TITLE = new TranslatableComponent("gui.iridynamics.pottery_work_table.title");
    public static final TextureArea BACKGROUND = TextureArea.createFullTexture(Iridynamics.rl("textures/gui/pottery_work_table_background.png"));

    private PotteryRecipe[] recipes;
    private int plan;
    private boolean recipeUpdateFlag;
    private final Inventory inventory;

    public PotteryWorkTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POTTERY_WORK_TABLE.get(), pos, state);
        this.inventory = new Inventory();
        this.recipes = new PotteryRecipe[0];
        this.plan = -1;
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (this.recipeUpdateFlag) {
            this.updateRecipe();
            this.recipeUpdateFlag = false;
        }
    }

    private void onPotteryWork(ButtonWidget.ButtonClickData clickData, ModularContainer container, long clickTime) {
        ItemStack stack = this.inventory.getStackInSlot(0);
        stack.getCapability(PotteryCapability.POTTERY).ifPresent((pottery) -> {
            int h = clickData.y() / 10;
            pottery.carve(h, Math.max(1, (int) (clickTime / 100)));
            ItemStackContainer c = RecipeUtil.container(this.inventory.getStackInSlot(0));
            if (this.checkRecipe(c))
                this.inventory.setStackInSlot(0, this.recipes[this.plan].assemble(c));
            this.markDirty();
            this.markForSync();
        });
    }

    private boolean checkRecipe(ItemStackContainer container) {
        if (this.plan >= 0 && this.recipes.length > this.plan)
            return this.recipes[this.plan].matchesCarving(container, this.level);
        return false;
    }

    private void markRecipeUpdate() {
        this.recipeUpdateFlag = true;
    }

    private void markForItemChange() {
        this.markRecipeUpdate();
        this.markDirty();
        this.markForSync();
    }

    private void updateRecipe() {
        if (!this.level.isClientSide) this.plan = -1;
        this.recipes = RecipeUtil.getAllValidRecipes(this.level, ModRecipeTypes.POTTERY.get(), RecipeUtil.container(this.inventory.getStackInSlot(0))).toArray(new PotteryRecipe[0]);
        this.markForSync();
        this.markDirty();
    }

    private void renderWorkBoard(ModularContainer container, PoseStack transform, int x, int y, int width, int height) {
        ItemStack stack = this.inventory.getStackInSlot(0);
        stack.getCapability(PotteryCapability.POTTERY).ifPresent((pottery) -> {
            for (int i = 0; i < 12; i++) {
                int carved = pottery.getCarved(i);
                RendererUtil.fill(transform, x + carved, y + i * 10, 50 - carved, 10, 0xff788c96);
                RendererUtil.fill(transform, x + 55, y + i * 10, 50 - carved, 10, 0xff788c96);
            }
        });
        if (this.plan >= 0 && this.recipes.length > this.plan) {
            for (int i = 0; i < 12; i++) {
                int carved = Mth.clamp(this.recipes[this.plan].carved()[i], 1, 49);
                int color = 0x0000ff00 + (((int) (MathUtil.getTriangularWave(2000, 0.25, 0.5) * 0xff)) << 24);
                RendererUtil.fill(transform, x + carved - 1, y + i * 10, 2, 10, color);
                RendererUtil.fill(transform, x + 104 - carved, y + i * 10, 2, 10, color);
            }
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public IGuiHolderCodec<IBlockEntityHolder<PotteryWorkTableBlockEntity>> getCodec() {
        return CODEC;
    }

    @Override
    public Component getTitle(Player player) {
        return TITLE;
    }

    @Override
    public ModularGuiInfo createGuiInfo(Player player) {
        return ModularGuiInfo.builder(176, 216).background(BACKGROUND).playerInventory(player.getInventory(), 8, 134)
                .slot(0, 153, 7, this.inventory, 0)
                .widget(1, new ButtonWidget(0, 6, 6, 16, 120).setReleaseCallback(this::onPotteryWork))
                .renderer(2, 25, 6, 105, 120, this::renderWorkBoard)
                .widget(4, new ButtonWidget(1, 133, 7, 16, 16, this::openPlanGui).setRenderer((transform, x, y, width, height) -> {
                    if (this.recipes.length > this.plan && this.plan >= 0)
                        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(this.recipes[this.plan].output().withoutDecorate(), x, y);
                })).build(player);
    }

    public void openPlanGui(ButtonWidget.ButtonClickData clickData, ModularContainer container) {
        ModularGuiInfo.openModularGui(new PlanGuiHolder(this), (ServerPlayer) container.getGuiInfo().getPlayer(), container.getParentGuiHolders());
    }

    @Override
    public int getPlanCount() {
        return this.recipes.length;
    }

    @Override
    public IWidgetRenderer getOverlayRenderer(int count) {
        return (transform, x, y, width, height) -> {
            if (this.recipes.length > count)
                Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(this.recipes[count].output().withoutDecorate(), x + 1, y + 1);
        };
    }

    @Override
    public void callback(int count) {
        if (this.recipes.length <= count) return;
        this.plan = count;
        this.markForSync();
        this.markDirty();
        ItemStackContainer c = RecipeUtil.container(this.inventory.getStackInSlot(0));
        if (this.checkRecipe(c)) this.inventory.setStackInSlot(0, this.recipes[this.plan].assemble(c));
    }

    @Override
    protected CompoundTag writeSyncData(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
        tag.putInt("plan", this.plan);
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.plan = tag.getInt("plan");
        this.markRecipeUpdate();
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
        tag.putInt("plan", this.plan);
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.plan = tag.getInt("plan");
        this.markRecipeUpdate();
    }

    public class Inventory extends ItemStackHandler {
        public Inventory() {
            super(1);
        }

        public ItemStack take(int slot) {
            this.validateSlotIndex(slot);
            ItemStack stack = this.getStackInSlot(slot);
            this.setStackInSlot(slot, ItemStack.EMPTY);
            return stack;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (!PotteryWorkTableBlockEntity.this.level.isClientSide)
                PotteryWorkTableBlockEntity.this.markForItemChange();
        }
    }
}