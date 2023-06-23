package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.IIgnitable;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.gui.IGuiHolderCodec;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import com.atodium.iridynamics.api.gui.TextureArea;
import com.atodium.iridynamics.api.gui.impl.BlockEntityCodec;
import com.atodium.iridynamics.api.gui.impl.IBlockEntityHolder;
import com.atodium.iridynamics.api.gui.widget.MoveType;
import com.atodium.iridynamics.api.heat.FuelInfo;
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import com.atodium.iridynamics.api.module.BlockHeatModule;
import com.atodium.iridynamics.api.module.ItemHeatModule;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.FuelBlock;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
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

public class BonfireBlockEntity extends SyncedBlockEntity implements ITickable, IIgnitable, IBlockEntityHolder<BonfireBlockEntity> {
    public static final BlockEntityCodec<BonfireBlockEntity> CODEC = BlockEntityCodec.createCodec(Iridynamics.rl("bonfire_block_entity"));
    public static final Component TITLE = new TranslatableComponent("gui.iridynamics.bonfire.title");
    public static final TextureArea BACKGROUND = TextureArea.createFullTexture(Iridynamics.rl("textures/gui/bonfire_background.png"));
    public static final TextureArea PROGRESS = TextureArea.createFullTexture(Iridynamics.rl("textures/gui/overlay/fire.png"));
    public static final double INVENTORY_RESISTANCE = 0.02;
    public static final double POWER = 5000.0;
    public static final int MAX_BLOW_VOLUME = 4000;

    private final Inventory inventory;
    private final HeatCapability heat;
    private FuelInfo fuelInfo;
    private double remainEnergy;
    private boolean ignite;
    private int blowVolume;

    public BonfireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BONFIRE.get(), pos, state);
        this.inventory = new Inventory();
        this.heat = new HeatCapability(new SolidPhasePortrait(16000.0), new double[]{0.1, 0.4, 0.2, 0.2, 0.2, 0.2});
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            BlockHeatModule.blockHeatExchange(level, pos, state, this, false);
            this.inventory.getStackInSlot(1).getCapability(HeatCapability.HEAT).ifPresent((item) -> ItemHeatModule.heatExchange(this.heat, item, INVENTORY_RESISTANCE));
            this.inventory.getStackInSlot(2).getCapability(HeatCapability.HEAT).ifPresent((item) -> ItemHeatModule.heatExchange(this.heat, item, INVENTORY_RESISTANCE));
            if (this.ignite) {
                double maxConsume;
                if (this.blowVolume > 0) {
                    maxConsume = Math.min(POWER * 3, this.remainEnergy);
                    this.blowVolume--;
                } else maxConsume = Math.min(POWER, this.remainEnergy);
                double consume = maxConsume - this.heat.increaseEnergy(maxConsume, this.getMaxTemperature());
                if (MathUtil.isEquals(this.remainEnergy, consume)) this.consumeFuel();
                else this.remainEnergy -= consume;
            } else this.ignite(Direction.UP, this.heat.getTemperature());
            this.markDirty();
        }
    }

    public double getMaxTemperature() {
        if (this.fuelInfo == null) return 0.0;
        return this.fuelInfo.maxTemperature() + this.blowVolume * 0.1;
    }

    public void consumeFuel() {
        ItemStack stack = this.inventory.getStackInSlot(0);
        FuelInfo info = FuelInfo.getFuelInfoForItem(stack);
        double igniteTemperature = Math.max(this.fuelInfo.maxTemperature(), this.heat.getTemperature());
        if (info != null && igniteTemperature >= info.flashPoint()) {
            this.ignite = true;
            this.fuelInfo = info;
            this.remainEnergy = info.calorificValue();
            this.updateBlockState();
            stack.shrink(1);
        } else this.extinguish();
    }

    public void extinguish() {
        this.ignite = false;
        this.fuelInfo = null;
        this.remainEnergy = 0.0;
        this.updateBlockState();
    }

    @Override
    public boolean ignite(Direction direction, double temperature) {
        ItemStack stack = this.inventory.getStackInSlot(0);
        FuelInfo info = FuelInfo.getFuelInfoForItem(stack);
        if (info != null && !this.ignite && temperature >= info.flashPoint()) {
            this.ignite = true;
            this.fuelInfo = info;
            this.remainEnergy = info.calorificValue();
            this.updateBlockState();
            stack.shrink(1);
            return true;
        }
        return false;
    }

    @Override
    public void blow(Direction direction, int volume) {
        this.blowVolume = Math.min(this.blowVolume + volume, MAX_BLOW_VOLUME);
    }

    public void updateBlockState() {
        this.level.getBlockEntity(this.getBlockPos(), ModBlockEntities.BONFIRE.get()).ifPresent((bonfire) -> {
            this.level.setBlockAndUpdate(this.getBlockPos(), ModBlocks.BONFIRE.get().defaultBlockState().setValue(FuelBlock.IGNITE, bonfire.ignite));
            this.level.setBlockEntity(bonfire);
        });
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public double getTemperature() {
        return this.heat.getTemperature();
    }

    public float getBurnProgress() {
        if (this.fuelInfo == null) return 0.0f;
        return (float) (this.remainEnergy / this.fuelInfo.calorificValue());
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
                .slot(0, 8, 44, this.inventory, 0)
                .slot(1, 71, 35, this.inventory, 1)
                .slot(2, 89, 35, this.inventory, 2)
                .progress(3, 9, 65, 14, 14, MoveType.VERTICAL_INVERTED, this::getBurnProgress, PROGRESS)
                .build(player);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction direction) {
        if (capability == HeatCapability.HEAT) return LazyOptional.of(() -> this.heat).cast();
        return super.getCapability(capability, direction);
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
            super(3);
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
            if (slot == 0) return 16;
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) return FuelInfo.ITEM_FUEL.containsKey(stack.getItem());
            return stack.getCapability(HeatCapability.HEAT).isPresent();
        }
    }
}