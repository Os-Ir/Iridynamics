package com.atodium.iridynamics.common.blockEntity;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.IIgnitable;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.heat.FuelInfo;
import com.atodium.iridynamics.api.heat.HeatModule;
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import com.atodium.iridynamics.api.liquid.LiquidModule;
import com.atodium.iridynamics.api.material.Phase;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.RecipeUtil;
import com.atodium.iridynamics.api.recipe.impl.DryingRecipe;
import com.atodium.iridynamics.api.recipe.impl.PileHeatRecipe;
import com.atodium.iridynamics.api.util.data.ItemDelegate;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Arrays;

public class PileBlockEntity extends SyncedBlockEntity implements ITickable, IIgnitable {
    public static final PileItemInfo EMPTY_INFO = new PileItemInfo("empty");
    public static final UnorderedRegistry<ItemDelegate, PileItemInfo> PILE_ITEM = new UnorderedRegistry<>();

    private boolean pileShapeUpdateFlag, syncFlag, recipeUpdateFlag;
    private int height;
    private ItemDelegate[] content;
    private boolean[] isLiquid;
    private SolidPhasePortrait portrait;
    private HeatCapability heat;
    private PileHeatRecipe heatRecipe;
    private DryingRecipe dryingRecipe;
    private int dryingTick;

    public PileBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PILE.get(), pos, state);
        this.pileShapeUpdateFlag = false;
        this.height = 0;
        this.content = new ItemDelegate[16];
        Arrays.fill(this.content, ItemDelegate.EMPTY);
        this.isLiquid = new boolean[16];
        this.portrait = new SolidPhasePortrait(0.0);
        this.heat = new HeatCapability(this.portrait);
    }

    public static void registerPileItem(Item item, PileItemInfo info) {
        registerPileItem(ItemDelegate.of(item), info);
    }

    public static void registerPileItem(ItemDelegate item, PileItemInfo info) {
        PILE_ITEM.register(item, info);
    }

    public static boolean containsItemInfo(ItemStack stack) {
        return containsItemInfo(ItemDelegate.of(stack));
    }

    public static boolean containsItemInfo(Item item) {
        return containsItemInfo(ItemDelegate.of(item));
    }

    public static boolean containsItemInfo(ItemDelegate item) {
        return PILE_ITEM.containsKey(item);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            if (this.pileShapeUpdateFlag) this.updatePileShape();
            HeatModule.blockHeatExchange(level, pos, state, this, false);
            this.tickHeatRecipe();
            this.tickDryingRecipe();
            this.tickContentState();
            this.markDirty();
            if (this.syncFlag) {
                this.sendSyncPacket();
                this.syncFlag = false;
            }
            if (this.recipeUpdateFlag) {
                this.updateHeatRecipe();
                this.updateDryingRecipe();
                this.recipeUpdateFlag = false;
            }
        }
    }

    private void tickHeatRecipe() {
        if (this.heatRecipe != null && this.heat.getTemperature() >= this.heatRecipe.temperature()) {
            this.level.setBlockAndUpdate(this.getBlockPos(), ModBlocks.HEAT_PROCESS.get().defaultBlockState().setValue(HeatProcessBlock.HEIGHT, this.height));
            this.level.getBlockEntity(this.getBlockPos(), ModBlockEntities.HEAT_PROCESS.get()).ifPresent((process) -> {
                int outputCount = this.height / this.heatRecipe.input().getCount();
                ItemStack output = this.heatRecipe.getResultItem();
                output.setCount(output.getCount() * outputCount);
                process.setup(this.content[0], output, this.height, this.portrait.getCapacity(), this.heatRecipe.temperature(), this.heatRecipe.energy() * outputCount, this.heat.getAllResistance(), this.heat.getTemperature());
            });
        }
    }

    private void tickDryingRecipe() {
        if (this.dryingRecipe != null) {
            if (this.dryingTick >= this.dryingRecipe.tick()) {
                ItemStack output = this.dryingRecipe.getResultItem();
                ItemDelegate outputItem = ItemDelegate.of(output.getItem());
                double temperature = this.heat.getTemperature();
                this.height = output.getCount();
                for (int i = 0; i < this.height; i++) this.content[i] = outputItem;
                this.markAllChange();
                this.heat.setTemperature(temperature);
                this.markSync();
            } else this.dryingTick++;
        }
    }

    private void tickContentState() {
        for (int i = 0; i < this.height; i++) {
            boolean result = PILE_ITEM.get(this.content[i]).meltingPoint() <= this.heat.getTemperature();
            if (result != this.isLiquid[i]) {
                this.isLiquid[i] = result;
                this.markSync();
            }
        }
        if (this.level.getGameTime() % 20 == 0) System.out.println(this.heat.getTemperature());
        if (!LiquidModule.hasLiquidContainer((ServerLevel) this.level, this.getBlockPos())) {
            int index = 0;
            boolean flag = false;
            while (index < this.height) {
                if (this.isLiquid[index]) {
                    this.removeContent(index);
                    flag = true;
                } else index++;
            }
            if (this.height == 0) this.level.removeBlock(this.getBlockPos(), false);
            else if (flag) {
                this.markAllChange();
                this.markSync();
            }
        }
    }

    private void removeContent(int index) {
        for (int i = index; i < this.height - 1; i++) {
            this.content[i] = this.content[i + 1];
            this.isLiquid[i] = this.isLiquid[i + 1];
        }
        this.height--;
    }

    public void markPileShapeUpdate() {
        this.pileShapeUpdateFlag = true;
    }

    public void markSync() {
        this.syncFlag = true;
    }

    public void markRecipeUpdate() {
        this.recipeUpdateFlag = true;
    }

    public int getHeight() {
        return Mth.clamp(this.height, 1, 16);
    }

    public ItemDelegate[] getAllContents() {
        ItemDelegate[] result = new ItemDelegate[this.height];
        System.arraycopy(this.content, 0, result, 0, this.height);
        return result;
    }

    public boolean isFuelBlock() {
        if (this.height < 16) return false;
        ItemDelegate item = this.content[0];
        if (!FuelInfo.ITEM_FUEL.containsKey(item)) return false;
        for (int i = 1; i < 16; i++) if (!item.equals(this.content[i])) return false;
        return true;
    }

    public PileItemInfo getPileItemInfo(int index) {
        if (!MathUtil.between(index, 0, this.height - 1) || !PILE_ITEM.containsKey(this.content[index]))
            return EMPTY_INFO;
        return PILE_ITEM.get(this.content[index]);
    }

    public boolean isLiquid(int index) {
        return this.isLiquid[index];
    }

    public void markAllChange() {
        this.markPileShapeUpdate();
        this.updateContentHeat();
        this.updateHeatRecipe();
        this.updateDryingRecipe();
        this.markDirty();
        this.updateBlockState();
    }

    public boolean setup(Item item) {
        return this.setup(ItemDelegate.of(item));
    }

    public boolean setup(ItemStack stack) {
        return this.setup(ItemDelegate.of(stack));
    }

    public boolean setup(ItemDelegate item) {
        if (!PILE_ITEM.containsKey(item)) return false;
        this.content[0] = item;
        this.height = 1;
        this.markAllChange();
        this.heat.setTemperature(HeatModule.AMBIENT_TEMPERATURE);
        return true;
    }

    public boolean addContent(ItemStack stack) {
        return this.addContent(ItemDelegate.of(stack));
    }

    public boolean addContent(Item item) {
        return this.addContent(ItemDelegate.of(item));
    }

    public boolean addContent(ItemDelegate item) {
        if (!PILE_ITEM.containsKey(item) || this.height >= 16) return false;
        this.content[this.height] = item;
        this.height++;
        this.markAllChange();
        this.heat.increaseEnergy(PILE_ITEM.get(item).capacity() * HeatModule.AMBIENT_TEMPERATURE);
        return true;
    }

    public ItemDelegate removeTopContent() {
        if (this.height <= 0) {
            this.level.removeBlock(this.getBlockPos(), false);
            return ItemDelegate.EMPTY;
        }
        this.height--;
        ItemDelegate ret = this.content[this.height];
        if (this.height > 0) {
            double temperature = this.heat.getTemperature();
            this.markAllChange();
            this.heat.setTemperature(temperature);
        } else this.level.removeBlock(this.getBlockPos(), false);
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
        fuel.setup(this.content[0], pileItemInfo.capacity() * 16, 1.0 / pileItemInfo.conductivity(), this.heat.getTemperature(), 16.0);
        return fuel.ignite(direction, temperature);
    }

    @Override
    public void blow(Direction direction, int volume) {

    }

    public void updateContentHeat() {
        double capacity = 0.0, average = 0.0;
        for (int i = 0; i < this.height; i++) {
            PileItemInfo info = PILE_ITEM.get(this.content[this.height - 1]);
            capacity += info.capacity();
            average += info.conductivity();
        }
        average /= 16;
        this.portrait.setCapacity(capacity);
        this.heat.updateResistance(Direction.UP, 1.0 / (PILE_ITEM.get(this.content[this.height - 1]).conductivity() + HeatModule.RESISTANCE_AIR_FLOW * (1.0 - this.height / 16.0)));
        this.heat.updateResistance(Direction.DOWN, 1.0 / PILE_ITEM.get(this.content[0]).conductivity());
        this.heat.updateResistance(Direction.EAST, 1.0 / average);
        this.heat.updateResistance(Direction.WEST, 1.0 / average);
        this.heat.updateResistance(Direction.NORTH, 1.0 / average);
        this.heat.updateResistance(Direction.SOUTH, 1.0 / average);
    }

    public void updateHeatRecipe() {
        ItemDelegate item = this.content[0];
        for (int i = 1; i < this.height; i++)
            if (!this.content[i].equals(item)) {
                this.heatRecipe = null;
                return;
            }
        this.heatRecipe = RecipeUtil.getRecipe(this.level, ModRecipeTypes.PILE_HEAT.get(), RecipeUtil.container(item.createStack(this.height)));
    }

    public void updateDryingRecipe() {
        ItemDelegate item = this.content[0];
        for (int i = 1; i < this.height; i++)
            if (!this.content[i].equals(item)) {
                this.heatRecipe = null;
                return;
            }
        DryingRecipe t = RecipeUtil.getRecipe(this.level, ModRecipeTypes.DRYING.get(), RecipeUtil.container(item.createStack(this.height)));
        if (this.dryingRecipe != t) this.dryingTick = 0;
        this.dryingRecipe = t;
    }

    public void updatePileShape() {
        this.pileShapeUpdateFlag = false;
        BlockPos posBelow = this.getBlockPos().below();
        BlockState stateBelow = this.level.getBlockState(posBelow);
        if (stateBelow.isAir()) {
            this.level.setBlock(posBelow, ModBlocks.PILE.get().defaultBlockState().setValue(PileBlock.HEIGHT, this.height), Block.UPDATE_ALL);
            PileBlockEntity pileBelow = (PileBlockEntity) this.level.getBlockEntity(posBelow);
            pileBelow.height = this.height;
            pileBelow.content = this.content;
            pileBelow.isLiquid = this.isLiquid;
            pileBelow.portrait = this.portrait;
            pileBelow.heat = this.heat;
            pileBelow.markPileShapeUpdate();
            this.level.removeBlock(this.getBlockPos(), false);
        } else if (stateBelow.getBlock() == ModBlocks.PILE.get()) {
            PileBlockEntity pileBelow = (PileBlockEntity) this.level.getBlockEntity(posBelow);
            int moveHeight = Math.min(this.height, 16 - pileBelow.height);
            if (moveHeight > 0) {
                double temperature = this.heat.getTemperature();
                double moveCapacity = 0.0;
                int remainHeight = this.height - moveHeight;
                for (int i = 0; i < moveHeight; i++) {
                    pileBelow.content[pileBelow.height] = this.content[i];
                    pileBelow.isLiquid[pileBelow.height] = this.isLiquid[i];
                    pileBelow.height++;
                    moveCapacity += PILE_ITEM.get(this.content[i]).capacity();
                }
                pileBelow.markAllChange();
                pileBelow.heat.increaseEnergy(moveCapacity * temperature);
                if (remainHeight == 0) this.level.removeBlock(this.getBlockPos(), false);
                else {
                    int copyStart = moveHeight;
                    for (int i = 0; i < remainHeight; i++) {
                        this.content[i] = this.content[copyStart];
                        this.isLiquid[i] = this.isLiquid[copyStart];
                        copyStart++;
                    }
                    this.height = remainHeight;
                    this.markAllChange();
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
        if (capability == HeatCapability.HEAT) return LazyOptional.of(() -> this.heat).cast();
        return super.getCapability(capability, direction);
    }

    @Override
    protected CompoundTag writeSyncData(CompoundTag tag) {
        tag.putInt("height", this.height);
        ListTag contentTag = new ListTag();
        for (int i = 0; i < this.height; i++) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.putString("item", this.content[i].toString());
            itemTag.putBoolean("isLiquid", this.isLiquid[i]);
            contentTag.add(i, itemTag);
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
            CompoundTag itemTag = (CompoundTag) contentTag.get(i);
            this.content[i] = ItemDelegate.of(itemTag.getString("item"));
            this.isLiquid[i] = itemTag.getBoolean("isLiquid");
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
            CompoundTag itemTag = new CompoundTag();
            itemTag.putString("item", this.content[i].toString());
            itemTag.putBoolean("isLiquid", this.isLiquid[i]);
            contentTag.add(i, itemTag);
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
            CompoundTag itemTag = (CompoundTag) contentTag.get(i);
            this.content[i] = ItemDelegate.of(itemTag.getString("item"));
            this.isLiquid[i] = itemTag.getBoolean("isLiquid");
        }
        this.heat.deserializeNBT(tag.getCompound("heat"));
        this.portrait.setCapacity(tag.getDouble("capacity"));
        this.dryingTick = tag.getInt("dryingTick");
        this.markRecipeUpdate();
    }

    public static class PileItemInfo {
        private final ResourceLocation texture;
        private final MaterialBase material;
        private int materialColor;
        private double meltingPoint, capacity, conductivity;

        private PileItemInfo(String name) {
            this.texture = Iridynamics.rl("block/pile/" + name);
            this.material = null;
            this.materialColor = 0xffffff;
            this.meltingPoint = 1.0;
            this.capacity = 1.0;
            this.conductivity = 1.0;
        }

        public PileItemInfo(String name, MaterialBase material) {
            this.texture = Iridynamics.rl("block/pile/" + name);
            this.material = material;
            this.materialColor = -1;
            this.meltingPoint = this.capacity = this.conductivity = -1.0;
        }

        public ResourceLocation texture() {
            return this.texture;
        }

        public int materialColor() {
            if (this.materialColor == -1) this.materialColor = material.getRenderInfo().color();
            return this.materialColor;
        }

        public double meltingPoint() {
            if (this.meltingPoint < 0.0) this.meltingPoint = material.getHeatInfo().getMeltingPoint();
            return this.meltingPoint;
        }

        public double capacity() {
            if (this.capacity < 0.0)
                this.capacity = material.getHeatInfo().getMoleCapacity(HeatModule.ATMOSPHERIC_PRESSURE, Phase.SOLID);
            return this.capacity;
        }

        public double conductivity() {
            if (this.conductivity < 0.0) this.conductivity = material.getPhysicalInfo().thermalConductivity();
            return this.conductivity;
        }
    }
}