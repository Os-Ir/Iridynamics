package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.api.blockEntity.IIgnitable;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.heat.FuelInfo;
import com.atodium.iridynamics.api.heat.HeatModule;
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import com.atodium.iridynamics.api.item.InventoryUtil;
import com.atodium.iridynamics.api.item.ItemDelegate;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.ModMaterials;
import com.atodium.iridynamics.api.material.ModSolidShapes;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.block.equipment.FurnaceBlock;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class FurnaceBlockEntity extends SyncedBlockEntity implements ITickable, IIgnitable {
    public static final int MAX_FUEL_ITEMS = 64;
    public static final int MAX_BLOW_VOLUME = 6000;
    public static final double CAPACITY = 450000.0;
    public static final double POWER = 240000.0;
    public static final double[] RESISTANCE = new double[]{0.1, 0.01, 0.1, 0.1, 0.1, 0.1};

    private ItemDelegate fuelItem;
    private FuelInfo fuelInfo;
    private boolean ignite;
    private double remainItems, starterTemperature, starterFlashPoint;
    private int blowVolume;
    private final HeatCapability heat;
    private final InventoryUtil.Inventory inventory;

    public FurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FURNACE.get(), pos, state);
        this.heat = new HeatCapability(new SolidPhasePortrait(CAPACITY), RESISTANCE);
        this.inventory = InventoryUtil.inventory(1);
    }

    public static boolean isIgniteStarter(ItemStack stack) {
        return MaterialEntry.containsMaterialEntry(stack) && MaterialEntry.getItemMaterialEntry(stack).equals(new MaterialEntry(ModSolidShapes.DUST, ModMaterials.WOOD));
    }

    public static double getStarterFlashPoint(ItemStack stack) {
        if (MaterialEntry.containsMaterialEntry(stack) && MaterialEntry.getItemMaterialEntry(stack).equals(new MaterialEntry(ModSolidShapes.DUST, ModMaterials.WOOD)))
            return 400.0;
        return Double.MAX_VALUE;
    }

    public static double getStarterTemperature(ItemStack stack) {
        if (MaterialEntry.containsMaterialEntry(stack) && MaterialEntry.getItemMaterialEntry(stack).equals(new MaterialEntry(ModSolidShapes.DUST, ModMaterials.WOOD)))
            return 1000.0;
        return 0.0;
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        HeatModule.blockHeatExchange(level, pos, state, this, false);
        if (this.ignite) {
            double remainEnergy = this.remainItems * this.fuelInfo.calorificValue();
            double maxConsume;
            if (this.blowVolume > 0) {
                maxConsume = Math.min(POWER * 3, remainEnergy);
                this.blowVolume--;
            } else maxConsume = Math.min(POWER, remainEnergy);
            double consume = maxConsume - this.heat.increaseEnergy(maxConsume, this.getMaxTemperature());
            if (MathUtil.isEquals(remainEnergy, consume)) {
                this.remainItems = 0.0;
                this.ignite = false;
                this.updateBlockState();
            } else this.remainItems -= consume / this.fuelInfo.calorificValue();
        } else this.ignite(Direction.UP, this.heat.getTemperature());
        this.markDirty();
    }

    public void updateBlockState() {
        this.level.getBlockEntity(this.getBlockPos(), ModBlockEntities.FURNACE.get()).ifPresent((furnace) -> {
            this.level.setBlockAndUpdate(this.getBlockPos(), ModBlocks.FURNACE.get().defaultBlockState().setValue(FurnaceBlock.DIRECTION, furnace.getBlockState().getValue(FurnaceBlock.DIRECTION)).setValue(FurnaceBlock.IGNITE, furnace.ignite));
            this.level.setBlockEntity(furnace);
        });
    }

    public boolean addIgniteStarter(ItemStack stack) {
        if (isIgniteStarter(stack)) {
            double t = getStarterTemperature(stack);
            if (!MathUtil.isEquals(t, this.starterTemperature)) {
                this.starterTemperature = t;
                this.starterFlashPoint = getStarterFlashPoint(stack);
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    public boolean addFuel(ItemStack stack) {
        if (this.fuelItem == null) {
            if (!FuelInfo.containsItemInfo(stack)) return false;
            this.fuelItem = ItemDelegate.of(stack);
            this.fuelInfo = FuelInfo.getFuelInfoForItem(this.fuelItem);
        } else if (!this.fuelItem.is(stack) || this.remainItems + 1.0 > MAX_FUEL_ITEMS) return false;
        this.remainItems++;
        stack.shrink(1);
        return true;
    }

    public ItemDelegate getFuelItem() {
        return this.fuelItem;
    }

    public FuelInfo getFuelInfo() {
        return this.fuelInfo;
    }

    public double getRemainItems() {
        return this.remainItems;
    }

    public double getTemperature() {
        return this.heat.getTemperature();
    }

    public double getMaxTemperature() {
        return this.fuelInfo.maxTemperature() + this.blowVolume * 0.1;
    }

    @Override
    public boolean ignite(Direction direction, double temperature) {
        if (!this.ignite && this.remainItems > 0.0 && (temperature >= this.fuelInfo.flashPoint() || (temperature >= this.starterFlashPoint && this.starterTemperature >= this.fuelInfo.flashPoint()))) {
            this.ignite = true;
            this.starterTemperature = this.starterFlashPoint = 0.0;
            this.updateBlockState();
            return true;
        }
        return false;
    }

    @Override
    public void blow(Direction direction, int volume) {
        this.blowVolume = Math.min(this.blowVolume + volume, MAX_BLOW_VOLUME);
        System.out.println("Max temp:  " + this.getMaxTemperature());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction direction) {
        if (capability == HeatCapability.HEAT) return LazyOptional.of(() -> this.heat).cast();
        return super.getCapability(capability, direction);
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {

    }

    @Override
    protected void readSyncData(CompoundTag tag) {

    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.put("heat", this.heat.serializeNBT());
        tag.put("inventory", this.inventory.serializeNBT());
        if (this.fuelItem != null) tag.putString("fuelItem", this.fuelItem.toString());
        tag.putBoolean("ignite", this.ignite);
        tag.putDouble("remainItems", this.remainItems);
        tag.putInt("blowVolume", this.blowVolume);
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.heat.deserializeNBT(tag.getCompound("heat"));
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        if (tag.contains("fuelItem")) {
            this.fuelItem = ItemDelegate.of(tag.getString("fuelItem"));
            this.fuelInfo = FuelInfo.getFuelInfoForItem(this.fuelItem);
        }
        this.ignite = tag.getBoolean("ignite");
        this.remainItems = tag.getDouble("remainItems");
        this.blowVolume = tag.getInt("blowVolume");
    }
}