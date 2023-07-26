package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.LiquidContainerCapability;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.heat.HeatModule;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.atodium.iridynamics.common.item.ModItems;
import com.atodium.iridynamics.common.item.MoldToolItem;
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

public class MoldToolBlockEntity extends SyncedBlockEntity implements ITickable {
    public static final double RESISTANCE = 0.02;

    private SolidShape shape;
    private final LiquidContainerCapability container;

    public MoldToolBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOLD_TOOL.get(), pos, state);
        this.container = new LiquidContainerCapability(0);
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
        if (stack.getItem() == ModItems.MOLD_TOOL.get()) {
            this.shape = MoldToolItem.getMoldShape(stack);
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
                int add = Math.min(entry.getValue(), this.shape.getUnit());
                this.container.addMaterial(material, add);
                this.container.setTemperature(temperature);
                source.addMaterial(material, -add);
                if (source.isEmpty()) source.setEnergy(0);
                else source.setTemperature(temperature);
                return true;
            }
            return false;
        }
        MaterialBase material = this.container.getAllMaterials().keySet().stream().toList().get(0);
        int c = this.container.getMaterialUnit(material);
        if (c == this.shape.getUnit() || !source.hasMaterial(material)) return false;
        int add = Math.min(source.getMaterialUnit(material), this.shape.getUnit() - c);
        this.container.addMaterial(material, add);
        this.container.increaseEnergy(material.getHeatInfo().getMoleEnergy(HeatModule.ATMOSPHERIC_PRESSURE, temperature) * add / 144.0);
        source.addMaterial(material, -add);
        if (!source.isEmpty()) source.setTemperature(temperature);
        return true;
    }

    public ItemStack take() {
        if (this.container.isEmpty()) return ItemStack.EMPTY;
        MaterialBase material = this.container.getAllMaterials().keySet().stream().toList().get(0);
        double temperature = this.container.getTemperature();
        if (this.container.getMaterialUnit(material) < this.shape.getUnit() || temperature >= material.getHeatInfo().getMeltingPoint())
            return ItemStack.EMPTY;
        ItemStack r = MaterialEntry.getMaterialItemStack(this.shape, material);
        r.getCapability(HeatCapability.HEAT).ifPresent((heat) -> heat.setTemperature(temperature));
        this.container.clear();
        return r;
    }

    public SolidShape getShape() {
        return this.shape;
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
        tag.putString("shape", this.shape.getName());
        tag.put("container", this.container.serializeNBT());
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.shape = SolidShape.getShapeByName(tag.getString("shape"));
        this.container.deserializeNBT(tag.getCompound("container"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.putString("shape", this.shape.getName());
        tag.put("container", this.container.serializeNBT());
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.shape = SolidShape.getShapeByName(tag.getString("shape"));
        this.container.deserializeNBT(tag.getCompound("container"));
    }
}