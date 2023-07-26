package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.LiquidContainerCapability;
import com.atodium.iridynamics.api.heat.HeatModule;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.ModSolidShapes;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.atodium.iridynamics.common.item.ModItems;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Map;

public class MoldBlockEntity extends SyncedBlockEntity implements ITickable {
    public static final double RESISTANCE = 0.02;
    public static final int CAPACITY = 144;

    private final LiquidContainerCapability container;

    public MoldBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOLD.get(), pos, state);
        this.container = new LiquidContainerCapability(CAPACITY);
    }

    public static void updateMold(LiquidContainerCapability container) {
        if (!container.isEmpty()) HeatModule.heatExchange(container, HeatModule.AMBIENT_TEMPERATURE, RESISTANCE);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            updateMold(this.container);
            this.markDirty();
        }
    }

    public boolean setup(ItemStack stack) {
        if (stack.getItem() == ModItems.MOLD.get()) {
            this.container.deserializeNBT(((LiquidContainerCapability) stack.getCapability(LiquidContainerCapability.LIQUID_CONTAINER).orElseThrow(NullPointerException::new)).serializeNBT());
            return true;
        }
        return false;
    }

    public boolean pour(LiquidContainerCapability source) {
        double temperature = source.getTemperature();
        if (this.container.isEmpty()) {
            ImmutableSet<Map.Entry<MaterialBase, Integer>> materials = source.getAllMaterials().entrySet();
            for (Map.Entry<MaterialBase, Integer> entry : materials) {
                MaterialBase material = entry.getKey();
                if (material.getHeatInfo().getMeltingPoint() > temperature) continue;
                int add = Math.min(entry.getValue(), CAPACITY);
                this.container.addMaterial(material, add);
                this.container.setTemperature(temperature);
                source.addMaterial(material, -add);
                if (source.isEmpty()) source.setEnergy(0);
                else source.setTemperature(temperature);
                this.sendSyncPacket();
                return true;
            }
            return false;
        }
        MaterialBase material = this.container.getAllMaterials().keySet().stream().toList().get(0);
        int c = this.container.getMaterialUnit(material);
        if (c == CAPACITY || !source.hasMaterial(material)) return false;
        int add = Math.min(source.getMaterialUnit(material), CAPACITY - c);
        this.container.addMaterial(material, add);
        this.container.increaseEnergy(material.getHeatInfo().getMoleEnergy(HeatModule.ATMOSPHERIC_PRESSURE, temperature) * add / 144.0);
        source.addMaterial(material, -add);
        if (!source.isEmpty()) source.setTemperature(temperature);
        this.sendSyncPacket();
        return true;
    }

    public ItemStack take() {
        if (this.container.isEmpty()) return ItemStack.EMPTY;
        MaterialBase material = this.container.getAllMaterials().keySet().stream().toList().get(0);
        double temperature = this.container.getTemperature();
        if (this.container.getMaterialUnit(material) < CAPACITY || temperature >= material.getHeatInfo().getMeltingPoint())
            return ItemStack.EMPTY;
        ItemStack r = MaterialEntry.getMaterialItemStack(ModSolidShapes.INGOT, material);
        r.getCapability(HeatCapability.HEAT).ifPresent((heat) -> heat.setTemperature(temperature));
        this.container.clear();
        System.out.println("sync");
        this.sendSyncPacket();
        return r;
    }

    public LiquidContainerCapability getLiquidContainer() {
        return this.container;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction direction) {
        if (capability == LiquidContainerCapability.LIQUID_CONTAINER)
            return LazyOptional.of(() -> this.container).cast();
        return super.getCapability(capability, direction);
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {
        tag.put("container", this.container.serializeNBT());
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.container.deserializeNBT(tag.getCompound("container"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.put("container", this.container.serializeNBT());
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.container.deserializeNBT(tag.getCompound("container"));
    }
}