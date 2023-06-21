package com.atodium.iridynamics.common.blockEntity;

import com.atodium.iridynamics.api.blockEntity.IIgnitable;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.heat.FuelInfo;
import com.atodium.iridynamics.api.heat.HeatUtil;
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.ModMaterials;
import com.atodium.iridynamics.api.material.ModSolidShapes;
import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.FuelBlock;
import com.atodium.iridynamics.common.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class FuelBlockEntity extends SyncedBlockEntity implements ITickable, IIgnitable {
    public static final double POWER = 80000.0;
    public static final double MAX_FUEL_ITEMS = 16.0;
    public static final int MAX_BLOW_VOLUME = 4000;

    private Item fuelItem;
    private FuelInfo fuelInfo;
    private boolean updateFlag, ignite;
    private double remainItems, starterTemperature, starterFlashPoint;
    private int blowVolume;
    private SolidPhasePortrait portrait;
    private HeatCapability heat;

    public FuelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FUEL.get(), pos, state);
        this.portrait = new SolidPhasePortrait(0.0);
        this.heat = new HeatCapability(this.portrait);
    }

    public boolean isIgniteStarter(ItemStack stack) {
        return MaterialEntry.containsMaterialEntry(stack) && MaterialEntry.getItemMaterialEntry(stack).equals(new MaterialEntry(ModSolidShapes.DUST, ModMaterials.WOOD));
    }

    public double getStarterFlashPoint(ItemStack stack) {
        if (MaterialEntry.containsMaterialEntry(stack) && MaterialEntry.getItemMaterialEntry(stack).equals(new MaterialEntry(ModSolidShapes.DUST, ModMaterials.WOOD)))
            return 400.0;
        return 0.0;
    }

    public double getStarterTemperature(ItemStack stack) {
        if (MaterialEntry.containsMaterialEntry(stack) && MaterialEntry.getItemMaterialEntry(stack).equals(new MaterialEntry(ModSolidShapes.DUST, ModMaterials.WOOD)))
            return 1000.0;
        return 0.0;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            if (this.updateFlag) this.updateFuelCondition();
            HeatUtil.blockHeatExchange(level, pos, state, this, false);
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
    }

    public void setup(Item fuelItem, double capacity, double resistance, double temperature, double remainItems) {
        if (this.level != null && !this.level.isClientSide) {
            this.ignite = false;
            this.fuelItem = fuelItem;
            this.fuelInfo = FuelInfo.ITEM_FUEL.get(fuelItem);
            this.portrait.setCapacity(capacity);
            this.remainItems = remainItems;
            this.heat.updateResistance(new double[]{resistance, resistance, resistance, resistance, resistance, resistance});
            this.heat.setTemperature(temperature);
            this.markDirty();
        }
    }

    public boolean addIgniteStarter(ItemStack stack) {
        if (this.isIgniteStarter(stack)) {
            double t = this.getStarterTemperature(stack);
            if (!MathUtil.isEquals(t, this.starterTemperature)) {
                this.starterTemperature = t;
                this.starterFlashPoint = this.getStarterFlashPoint(stack);
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    public boolean addFuel(ItemStack stack) {
        if (stack.getItem() != this.fuelItem || this.remainItems + 1.0 > MAX_FUEL_ITEMS) return false;
        this.remainItems++;
        stack.shrink(1);
        return true;
    }

    public Item getFuelItem() {
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

    public void markUpdate() {
        this.updateFlag = true;
    }

    public void updateFuelCondition() {
        this.updateFlag = false;
        BlockPos posBelow = this.getBlockPos().below();
        BlockState stateBelow = this.level.getBlockState(posBelow);
        if (stateBelow.isAir()) {
            this.level.setBlock(posBelow, ModBlocks.FUEL.get().defaultBlockState().setValue(FuelBlock.IGNITE, this.ignite), Block.UPDATE_ALL);
            FuelBlockEntity fuelBelow = (FuelBlockEntity) this.level.getBlockEntity(posBelow);
            fuelBelow.fuelItem = this.fuelItem;
            fuelBelow.fuelInfo = this.fuelInfo;
            fuelBelow.ignite = this.ignite;
            fuelBelow.portrait = this.portrait;
            fuelBelow.heat = this.heat;
            fuelBelow.markUpdate();
            this.level.removeBlock(this.getBlockPos(), false);
        }
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

    public void updateBlockState() {
        this.level.getBlockEntity(this.getBlockPos(), ModBlockEntities.FUEL.get()).ifPresent((fuel) -> {
            this.level.setBlockAndUpdate(this.getBlockPos(), ModBlocks.FUEL.get().defaultBlockState().setValue(FuelBlock.IGNITE, fuel.ignite));
            this.level.setBlockEntity(fuel);
        });
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction direction) {
        if (capability == HeatCapability.HEAT) {
            return LazyOptional.of(() -> this.heat).cast();
        }
        return super.getCapability(capability, direction);
    }

    @Override
    protected CompoundTag writeSyncData(CompoundTag tag) {
        tag.putString("fuelItem", DataUtil.writeItemToString(this.fuelItem));
        tag.putBoolean("ignite", this.ignite);
        tag.putDouble("remainItems", this.remainItems);
        tag.putInt("blowVolume", this.blowVolume);
        tag.putDouble("capacity", this.portrait.getCapacity());
        tag.put("heat", this.heat.serializeNBT());
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.fuelItem = DataUtil.readItemFromString(tag.getString("fuelItem"));
        this.fuelInfo = FuelInfo.getFuelInfoForItem(this.fuelItem);
        this.ignite = tag.getBoolean("ignite");
        this.remainItems = tag.getDouble("remainItems");
        this.blowVolume = tag.getInt("blowVolume");
        this.portrait.setCapacity(tag.getDouble("capacity"));
        this.heat.deserializeNBT(tag.getCompound("heat"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.putString("fuelItem", DataUtil.writeItemToString(this.fuelItem));
        tag.putBoolean("ignite", this.ignite);
        tag.putDouble("remainItems", this.remainItems);
        tag.putInt("blowVolume", this.blowVolume);
        tag.putDouble("capacity", this.portrait.getCapacity());
        tag.put("heat", this.heat.serializeNBT());
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.fuelItem = DataUtil.readItemFromString(tag.getString("fuelItem"));
        this.fuelInfo = FuelInfo.getFuelInfoForItem(this.fuelItem);
        this.ignite = tag.getBoolean("ignite");
        this.remainItems = tag.getDouble("remainItems");
        this.blowVolume = tag.getInt("blowVolume");
        this.portrait.setCapacity(tag.getDouble("capacity"));
        this.heat.deserializeNBT(tag.getCompound("heat"));
    }
}