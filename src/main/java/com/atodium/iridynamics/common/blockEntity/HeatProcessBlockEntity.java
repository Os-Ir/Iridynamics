package com.atodium.iridynamics.common.blockEntity;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.HeatProcessCapability;
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import com.atodium.iridynamics.api.module.BlockHeatModule;
import com.atodium.iridynamics.api.util.data.ItemDelegate;
import com.atodium.iridynamics.common.block.HeatProcessBlock;
import com.atodium.iridynamics.common.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class HeatProcessBlockEntity extends SyncedBlockEntity implements ITickable {
    private boolean updateFlag;
    private ItemDelegate content;
    private ItemStack output;
    private int height, outputCount;
    private SolidPhasePortrait portrait;
    private HeatProcessCapability process;

    public HeatProcessBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HEAT_PROCESS.get(), pos, state);
        this.output = ItemStack.EMPTY;
        this.portrait = new SolidPhasePortrait(0.0);
        this.process = new HeatProcessCapability(this.portrait);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            if (this.updateFlag) this.updateProcessCondition();
            BlockHeatModule.blockHeatExchange(level, pos, state, this, false);
            if (this.isFinish()) this.markForSync();
            this.markDirty();
        }
    }

    public PileBlockEntity.PileItemInfo getContentInfo() {
        ItemDelegate item = this.isFinish() ? ItemDelegate.of(this.output.getItem()) : this.content;
        if (!PileBlockEntity.PILE_ITEM.containsKey(item)) return PileBlockEntity.EMPTY_INFO;
        return PileBlockEntity.PILE_ITEM.get(item);
    }

    public ItemDelegate getContent() {
        return this.content;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public int getHeight() {
        return this.height;
    }

    public HeatProcessCapability getHeatProcessCapability() {
        return this.process;
    }

    public boolean isFinish() {
        return this.process.isFinish();
    }

    public void setup(ItemDelegate content, ItemStack output, int height, double capacity, double recipeTemperature, double recipeEnergy, double[] resistance, double temperature) {
        if (this.level != null && !this.level.isClientSide) {
            this.content = content;
            this.output = output;
            this.height = height;
            this.portrait.setCapacity(capacity);
            this.process = new HeatProcessCapability(this.portrait, recipeTemperature, recipeEnergy, resistance);
            this.process.setTemperature(Math.min(recipeTemperature, temperature));
            this.markDirty();
        }
    }

    public void markUpdate() {
        this.updateFlag = true;
    }

    public void updateProcessCondition() {
        this.updateFlag = false;
        BlockPos posBelow = this.getBlockPos().below();
        BlockState stateBelow = this.level.getBlockState(posBelow);
        if (stateBelow.isAir()) {
            this.level.setBlock(posBelow, ModBlocks.HEAT_PROCESS.get().defaultBlockState().setValue(HeatProcessBlock.HEIGHT, this.height), Block.UPDATE_ALL);
            HeatProcessBlockEntity processBelow = (HeatProcessBlockEntity) this.level.getBlockEntity(posBelow);
            processBelow.content = this.content;
            processBelow.output = this.output;
            processBelow.height = this.height;
            processBelow.portrait = this.portrait;
            processBelow.process = this.process;
            processBelow.markUpdate();
            this.level.removeBlock(this.getBlockPos(), false);
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction direction) {
        if (capability == HeatCapability.HEAT || capability == HeatProcessCapability.HEAT_PROCESS)
            return LazyOptional.of(() -> this.process).cast();
        return super.getCapability(capability, direction);
    }

    @Override
    protected CompoundTag writeSyncData(CompoundTag tag) {
        tag.putString("content", this.content.toString());
        tag.put("output", this.output.serializeNBT());
        tag.putInt("height", this.height);
        tag.putDouble("capacity", this.portrait.getCapacity());
        tag.put("process", this.process.serializeNBT());
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.content = ItemDelegate.of(tag.getString("content"));
        this.output = ItemStack.of(tag);
        this.height = tag.getInt("height");
        this.portrait.setCapacity(tag.getDouble("capacity"));
        this.process.deserializeNBT(tag.getCompound("process"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.putString("content", this.content.toString());
        tag.put("output", this.output.serializeNBT());
        tag.putInt("height", this.height);
        tag.putDouble("capacity", this.portrait.getCapacity());
        tag.put("process", this.process.serializeNBT());
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.content = ItemDelegate.of(tag.getString("content"));
        this.output = ItemStack.of(tag);
        this.height = tag.getInt("height");
        this.portrait.setCapacity(tag.getDouble("capacity"));
        this.process.deserializeNBT(tag.getCompound("process"));
    }
}