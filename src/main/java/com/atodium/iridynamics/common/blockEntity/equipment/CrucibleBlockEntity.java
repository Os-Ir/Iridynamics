package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.LiquidContainerCapability;
import com.atodium.iridynamics.api.heat.HeatModule;
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.ModSolidShapes;
import com.atodium.iridynamics.api.material.alloy.AlloyModule;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class CrucibleBlockEntity extends SyncedBlockEntity implements ITickable {
    public static final double HEAT_CAPACITY = 100000.0, INVENTORY_RESISTANCE = 0.01;
    public static final double[] RESISTANCE = new double[]{0.02, 0.2, 0.1, 0.1, 0.1, 0.1};
    public static final int CAPACITY = 9216;

    private final HeatCapability heat;
    private final LiquidContainerCapability container;

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUCIBLE.get(), pos, state);
        this.heat = new HeatCapability(new SolidPhasePortrait(HEAT_CAPACITY), RESISTANCE);
        this.container = new LiquidContainerCapability(CAPACITY);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            HeatModule.blockHeatExchange(level, pos, state, this, false);
            if (!this.container.isEmpty()) HeatModule.heatExchange(this.heat, this.container, INVENTORY_RESISTANCE);
            while (true)
                if (AlloyModule.maxAlloyUnits(this.container, this.container, this.container.getTemperature(), true).isEmpty())
                    break;
            if (this.level.getGameTime() % 20 == 0) {
                System.out.println("crucible: " + this.container.getTemperature() + "K");
                System.out.println(this.container.getAllMaterials());
            }
            this.markDirty();
            this.sendSyncPacket();
        }
    }

    public static boolean validateItem(ItemStack stack) {
        return MaterialEntry.containsMaterialEntry(stack) && MaterialEntry.getItemMaterialEntry(stack).shape().is(ModSolidShapes.DUSTS) && stack.getCapability(HeatCapability.HEAT).isPresent();
    }

    public ItemStack tryAddItem(ItemStack stack) {
        if (!validateItem(stack)) return stack;
        MaterialEntry entry = MaterialEntry.getItemMaterialEntry(stack);
        MaterialBase material = entry.material();
        int unit = entry.shape().getUnit();
        int maxConsume = this.container.remainCapacity() / unit;
        int count = stack.getCount();
        if (maxConsume >= count) {
            this.container.addMaterial(material, unit * count);
            this.container.increaseEnergy(stack.getCapability(HeatCapability.HEAT).orElseThrow(NullPointerException::new).getEnergy() * count);
            return ItemStack.EMPTY;
        } else {
            this.container.addMaterial(material, unit * maxConsume);
            this.container.increaseEnergy(stack.getCapability(HeatCapability.HEAT).orElseThrow(NullPointerException::new).getEnergy() * maxConsume);
            stack.shrink(maxConsume);
            return stack;
        }
    }

    public LiquidContainerCapability getLiquidContainer() {
        return this.container;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction direction) {
        if (capability == LiquidContainerCapability.LIQUID_CONTAINER)
            return LazyOptional.of(() -> this.container).cast();
        if (capability == HeatCapability.HEAT) return LazyOptional.of(() -> this.heat).cast();
        return super.getCapability(capability, direction);
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {
        tag.put("container", this.container.serializeNBT());
        tag.put("heat", this.heat.serializeNBT());
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.container.deserializeNBT(tag.getCompound("container"));
        this.heat.deserializeNBT(tag.getCompound("heat"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.put("container", this.container.serializeNBT());
        tag.put("heat", this.heat.serializeNBT());
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.container.deserializeNBT(tag.getCompound("container"));
        this.heat.deserializeNBT(tag.getCompound("heat"));
    }
}