package com.atodium.iridynamics.common.blockEntity;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.IIgnitable;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.heat.FuelInfo;
import com.atodium.iridynamics.api.heat.HeatUtil;
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import com.atodium.iridynamics.api.recipe.impl.DryingRecipe;
import com.atodium.iridynamics.api.recipe.impl.PileHeatRecipe;
import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.HeatProcessBlock;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.block.PileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Arrays;

public class PileBlockEntity extends SyncedBlockEntity implements ITickable, IIgnitable {
    public static final PileItemInfo EMPTY_INFO = new PileItemInfo("empty", 4200.0, 1.0);
    public static final UnorderedRegistry<Item, PileItemInfo> PILE_ITEM = new UnorderedRegistry<>();

    private boolean pileStateUpdateFlag;
    private int height;
    private Item[] content;
    private SolidPhasePortrait portrait;
    private HeatCapability heat;
    private PileHeatRecipe heatRecipe;
    private DryingRecipe dryingRecipe;
    private int dryingTick;

    public PileBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PILE.get(), pos, state);
        this.pileStateUpdateFlag = false;
        this.height = 0;
        this.content = new Item[16];
        Arrays.fill(this.content, Items.AIR);
        this.portrait = new SolidPhasePortrait(0.0);
        this.heat = new HeatCapability(this.portrait);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            if (this.pileStateUpdateFlag) this.updatePileCondition();
            System.out.println("Pile: " + this.heat.getTemperature() + "K");
            HeatUtil.blockHeatExchange(level, pos, state, this, false);
            if (this.heatRecipe != null && this.heat.getTemperature() >= this.heatRecipe.temperature()) {
                this.level.setBlockAndUpdate(this.getBlockPos(), ModBlocks.HEAT_PROCESS.get().defaultBlockState().setValue(HeatProcessBlock.HEIGHT, this.height));
                this.level.getBlockEntity(this.getBlockPos(), ModBlockEntities.HEAT_PROCESS.get()).ifPresent((process) -> {
                    int outputCount = this.height / this.heatRecipe.input().getCount();
                    ItemStack output = this.heatRecipe.getResultItem();
                    output.setCount(output.getCount() * outputCount);
                    process.setup(this.content[0], output, this.height, this.portrait.getCapacity(), this.heatRecipe.temperature(), this.heatRecipe.energy() * outputCount, this.heat.getAllResistance(), this.heat.getTemperature());
                });
            }
            if (this.dryingRecipe != null) {
                if (this.dryingTick >= this.dryingRecipe.tick()) {
                    ItemStack output = this.dryingRecipe.getResultItem();
                    Item outputItem = output.getItem();
                    double temperature = this.heat.getTemperature();
                    this.height = output.getCount();
                    for (int i = 0; i < this.height; i++) this.content[i] = outputItem;
                    this.markPileBlockChange();
                    this.heat.setTemperature(temperature);
                    this.markForSync();
                } else this.dryingTick++;
            }
            this.markDirty();
        }
    }

    public void markPileStateUpdate() {
        this.pileStateUpdateFlag = true;
    }

    public int getHeight() {
        return Mth.clamp(this.height, 1, 16);
    }

    public Item[] getAllContents() {
        Item[] result = new Item[this.height];
        System.arraycopy(this.content, 0, result, 0, this.height);
        return result;
    }

    public boolean isFuelBlock() {
        if (this.height < 16) {
            return false;
        }
        Item item = this.content[0];
        if (!FuelInfo.ITEM_FUEL.containsKey(item)) {
            return false;
        }
        for (int i = 1; i < 16; i++) {
            if (item != this.content[i]) {
                return false;
            }
        }
        return true;
    }

    public PileItemInfo getPileItemInfo(int count) {
        if (!MathUtil.between(count, 0, this.height - 1) || !PILE_ITEM.containsKey(this.content[count]))
            return EMPTY_INFO;
        return PILE_ITEM.get(this.content[count]);
    }

    public void markPileBlockChange() {
        this.markPileStateUpdate();
        this.updateContentHeat();
        this.updateHeatRecipe();
        this.markDirty();
        this.updateBlockState();
    }

    public boolean setup(Item item) {
        if (!PILE_ITEM.containsKey(item)) {
            return false;
        }
        this.content[0] = item;
        this.height = 1;
        this.markPileBlockChange();
        this.heat.setTemperature(HeatUtil.AMBIENT_TEMPERATURE);
        return true;
    }

    public boolean addContent(Item item) {
        if (!PILE_ITEM.containsKey(item) || this.height >= 16) {
            return false;
        }
        this.content[this.height] = item;
        this.height++;
        this.markPileBlockChange();
        this.heat.increaseEnergy(PILE_ITEM.get(item).capacity * HeatUtil.AMBIENT_TEMPERATURE);
        return true;
    }

    public Item removeTopContent() {
        assert this.level != null;
        if (this.height <= 0) {
            this.level.removeBlock(this.getBlockPos(), false);
            return Items.AIR;
        }
        this.height--;
        Item ret = this.content[this.height];
        if (this.height > 0) {
            double temperature = this.heat.getTemperature();
            this.markPileBlockChange();
            this.heat.setTemperature(temperature);
        } else {
            this.level.removeBlock(this.getBlockPos(), false);
        }
        return ret;
    }

    @Override
    public boolean ignite(Direction direction, double temperature) {
        if (!this.isFuelBlock()) return false;
        FuelInfo fuelInfo = FuelInfo.ITEM_FUEL.get(this.content[0]);
        PileItemInfo pileItemInfo = PILE_ITEM.get(this.content[0]);
        BlockPos pos = this.getBlockPos();
        this.level.setBlock(pos, ModBlocks.FUEL.get().defaultBlockState(), Block.UPDATE_ALL);
        FuelBlockEntity fuel = (FuelBlockEntity) this.level.getBlockEntity(pos);
        fuel.setup(this.content[0], pileItemInfo.capacity * 16, 1.0 / pileItemInfo.conductivity, this.heat.getTemperature(), 16.0);
        fuel.ignite(direction, temperature);
        return true;
    }

    @Override
    public void blow(Direction direction, int volume) {

    }

    public void updateContentHeat() {
        double capacity = 0.0, average = 0.0;
        for (int i = 0; i < this.height; i++) {
            PileItemInfo info = PILE_ITEM.get(this.content[this.height - 1]);
            capacity += info.capacity;
            average += info.conductivity;
        }
        average /= 16;
        this.portrait.setCapacity(capacity);
        this.heat.updateResistance(Direction.UP, 1.0 / (PILE_ITEM.get(this.content[this.height - 1]).conductivity + HeatUtil.RESISTANCE_AIR_FLOW * (1.0 - this.height / 16.0)));
        this.heat.updateResistance(Direction.DOWN, 1.0 / PILE_ITEM.get(this.content[0]).conductivity);
        this.heat.updateResistance(Direction.EAST, 1.0 / average);
        this.heat.updateResistance(Direction.WEST, 1.0 / average);
        this.heat.updateResistance(Direction.NORTH, 1.0 / average);
        this.heat.updateResistance(Direction.SOUTH, 1.0 / average);
    }

    public void updateHeatRecipe() {
        Item item = this.content[0];
        for (int i = 1; i < this.height; i++)
            if (this.content[i] != item) {
                this.heatRecipe = null;
                return;
            }
        this.heatRecipe = PileHeatRecipe.getRecipe(new ItemStack(item, this.height), this.level);
    }

    public void updateDryingRecipe() {
        Item item = this.content[0];
        for (int i = 1; i < this.height; i++)
            if (this.content[i] != item) {
                this.heatRecipe = null;
                return;
            }
        DryingRecipe t = DryingRecipe.getRecipe(new ItemStack(item, this.height), this.level);
        if (this.dryingRecipe != t) this.dryingTick = 0;
        this.dryingRecipe = t;
    }

    public void updatePileCondition() {
        this.pileStateUpdateFlag = false;
        assert this.level != null;
        BlockPos posBelow = this.getBlockPos().below();
        BlockState stateBelow = this.level.getBlockState(posBelow);
        if (stateBelow.isAir()) {
            this.level.setBlock(posBelow, ModBlocks.PILE.get().defaultBlockState().setValue(PileBlock.HEIGHT, this.height), Block.UPDATE_ALL);
            PileBlockEntity pileBelow = (PileBlockEntity) this.level.getBlockEntity(posBelow);
            assert pileBelow != null;
            pileBelow.height = this.height;
            pileBelow.content = this.content;
            pileBelow.portrait = this.portrait;
            pileBelow.heat = this.heat;
            pileBelow.markPileStateUpdate();
            this.level.removeBlock(this.getBlockPos(), false);
        } else if (stateBelow.getBlock() == ModBlocks.PILE.get()) {
            PileBlockEntity pileBelow = (PileBlockEntity) this.level.getBlockEntity(posBelow);
            assert pileBelow != null;
            int moveHeight = Math.min(this.height, 16 - pileBelow.height);
            if (moveHeight > 0) {
                double temperature = this.heat.getTemperature();
                double moveCapacity = 0.0;
                int remainHeight = this.height - moveHeight;
                for (int i = 0; i < moveHeight; i++) {
                    pileBelow.content[pileBelow.height] = this.content[i];
                    pileBelow.height++;
                    moveCapacity += PILE_ITEM.get(this.content[i]).capacity;
                }
                pileBelow.markPileBlockChange();
                pileBelow.heat.increaseEnergy(moveCapacity * temperature);
                if (remainHeight == 0) {
                    this.level.removeBlock(this.getBlockPos(), false);
                } else {
                    int copyStart = moveHeight;
                    for (int i = 0; i < remainHeight; i++) {
                        this.content[i] = this.content[copyStart];
                        copyStart++;
                    }
                    this.height = remainHeight;
                    this.markPileBlockChange();
                    this.heat.setTemperature(temperature);
                }
            }
        }
    }

    public void updateBlockState() {
        this.level.getBlockEntity(this.getBlockPos(), ModBlockEntities.PILE.get()).ifPresent((pile) -> {
            this.level.setBlockAndUpdate(this.getBlockPos(), ModBlocks.PILE.get().defaultBlockState().setValue(PileBlock.HEIGHT, pile.height));
            this.level.setBlockEntity(pile);
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
        tag.putInt("height", this.height);
        ListTag contentTag = new ListTag();
        for (int i = 0; i < this.height; i++) {
            CompoundTag item = new CompoundTag();
            item.putString("item", DataUtil.writeItemToString(this.content[i]));
            contentTag.add(i, item);
        }
        tag.put("content", contentTag);
        tag.put("heat", this.heat.serializeNBT());
        tag.putDouble("capacity", this.portrait.getCapacity());
        tag.putInt("dryingTick", this.dryingTick);
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.height = tag.getInt("height");
        ListTag contentTag = tag.getList("content", Tag.TAG_COMPOUND);
        for (int i = 0; i < this.height; i++) {
            CompoundTag item = (CompoundTag) contentTag.get(i);
            this.content[i] = DataUtil.readItemFromString(item.getString("item"));
        }
        this.heat.deserializeNBT(tag.getCompound("heat"));
        this.portrait.setCapacity(tag.getDouble("capacity"));
        this.dryingTick = tag.getInt("dryingTick");
        this.updateHeatRecipe();
        this.updateDryingRecipe();
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.putInt("height", this.height);
        ListTag contentTag = new ListTag();
        for (int i = 0; i < this.height; i++) {
            CompoundTag item = new CompoundTag();
            item.putString("item", DataUtil.writeItemToString(this.content[i]));
            contentTag.add(i, item);
        }
        tag.put("content", contentTag);
        tag.put("heat", this.heat.serializeNBT());
        tag.putDouble("capacity", this.portrait.getCapacity());
        tag.putInt("dryingTick", this.dryingTick);
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.height = tag.getInt("height");
        ListTag contentTag = tag.getList("content", Tag.TAG_COMPOUND);
        for (int i = 0; i < this.height; i++) {
            CompoundTag item = (CompoundTag) contentTag.get(i);
            this.content[i] = DataUtil.readItemFromString(item.getString("item"));
        }
        this.heat.deserializeNBT(tag.getCompound("heat"));
        this.portrait.setCapacity(tag.getDouble("capacity"));
        this.dryingTick = tag.getInt("dryingTick");
        this.updateHeatRecipe();
        this.updateDryingRecipe();
    }

    public record PileItemInfo(String name, double capacity, double conductivity) {
        public ResourceLocation getTextureName() {
            return new ResourceLocation(Iridynamics.MODID, "block/pile/" + this.name);
        }
    }
}