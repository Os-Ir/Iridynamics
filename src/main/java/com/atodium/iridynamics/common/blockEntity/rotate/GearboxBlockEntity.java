package com.atodium.iridynamics.common.blockEntity.rotate;

import com.atodium.iridynamics.api.blockEntity.IRotateNodeHolder;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.material.ModMaterials;
import com.atodium.iridynamics.api.material.ModSolidShapes;
import com.atodium.iridynamics.api.module.rotate.Gearbox;
import com.atodium.iridynamics.api.module.rotate.IRotateNode;
import com.atodium.iridynamics.api.module.rotate.RotateModule;
import com.atodium.iridynamics.api.util.data.ItemDelegate;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.compress.utils.Lists;

import java.util.EnumMap;
import java.util.List;

public class GearboxBlockEntity extends SyncedBlockEntity implements ITickable, IRotateNodeHolder {
    public static final ItemDelegate SMALL_GEAR = ItemDelegate.of(ModSolidShapes.SMALL_GEAR, ModMaterials.IRON);
    public static final ItemDelegate GEAR = ItemDelegate.of(ModSolidShapes.GEAR, ModMaterials.IRON);

    private final EnumMap<Direction, Integer> gearType;
    private Gearbox rotate;

    public GearboxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GEARBOX.get(), pos, state);
        this.gearType = Maps.newEnumMap(Direction.class);
        for (Direction direction : Direction.values()) this.gearType.put(direction, 0);
        this.rotate = RotateModule.gearbox(null, null, 0, 0);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        RotateModule.tryTick((ServerLevel) level, pos);
        this.markForSync();
    }

    public void setupRotate(ServerLevel level) {
        RotateModule.addRotateBlock(level, this.getBlockPos(), this.rotate);
        this.markForSync();
    }

    public boolean isDirectionValid(Direction direction) {
        return this.gearType.get(direction) != 0;
    }

    public List<ItemStack> getAllGears() {
        List<ItemStack> result = Lists.newArrayList();
        for (Direction direction : Direction.values())
            if (this.gearType.get(direction) != 0)
                result.add(this.gearType.get(direction) == 1 ? SMALL_GEAR.createStack() : GEAR.createStack());
        return result;
    }

    public ItemStack updateGearbox(Direction direction, ItemStack stack) {
        int original = this.gearType.get(direction);
        if (original == 0) {
            if (this.rotate.getValidDirections() < 2) {
                if (SMALL_GEAR.is(stack)) {
                    this.gearType.put(direction, 1);
                    if (this.rotate.getDirectionA() == null) this.rotate = RotateModule.gearbox(direction, null, 1, 0);
                    else
                        this.rotate = RotateModule.gearbox(this.rotate.getDirectionA(), direction, this.rotate.getGearA(), 1);
                    stack.shrink(1);
                    RotateModule.updateRotateBlock((ServerLevel) this.level, this.getBlockPos(), this.rotate);
                } else if (GEAR.is(stack)) {
                    this.gearType.put(direction, 2);
                    if (this.rotate.getDirectionA() == null) this.rotate = RotateModule.gearbox(direction, null, 2, 0);
                    else
                        this.rotate = RotateModule.gearbox(this.rotate.getDirectionA(), direction, this.rotate.getGearA(), 2);
                    stack.shrink(1);
                    RotateModule.updateRotateBlock((ServerLevel) this.level, this.getBlockPos(), this.rotate);
                }
            }
        } else {
            this.gearType.put(direction, 0);
            if (this.rotate.getDirectionA() == direction)
                this.rotate = RotateModule.gearbox(this.rotate.getDirectionB(), null, this.rotate.getGearB(), 0);
            else this.rotate = RotateModule.gearbox(this.rotate.getDirectionA(), null, this.rotate.getGearA(), 0);
            RotateModule.updateRotateBlock((ServerLevel) this.level, this.getBlockPos(), this.rotate);
            return original == 1 ? SMALL_GEAR.createStack() : GEAR.createStack();
        }
        return ItemStack.EMPTY;
    }

    public double getRenderAngle(Direction direction, float partialTicks) {
        return MathUtil.castAngle(this.rotate.getAngle(direction));
    }

    @Override
    public void receive(IRotateNode node) {
        this.rotate = (Gearbox) node;
    }

    @Override
    protected CompoundTag writeSyncData(CompoundTag tag) {
        tag.put("rotateStructure", RotateModule.writeRotateNode(this.rotate));
        tag.put("rotateSync", RotateModule.writeSyncTag(this.rotate));
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.rotate = (Gearbox) RotateModule.readRotateNode(tag.getCompound("rotateStructure"));
        for (Direction direction : Direction.values()) this.gearType.put(direction, 0);
        if (this.rotate.getDirectionA() != null)
            this.gearType.put(this.rotate.getDirectionA(), this.rotate.getGearA());
        if (this.rotate.getDirectionB() != null)
            this.gearType.put(this.rotate.getDirectionB(), this.rotate.getGearB());
        RotateModule.readSyncTag(this.rotate, tag.getCompound("rotateSync"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        CompoundTag gearTypeTag = new CompoundTag();
        for (Direction direction : Direction.values())
            gearTypeTag.putInt(direction.getName(), this.gearType.get(direction));
        tag.put("gearType", gearTypeTag);
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        CompoundTag gearTypeTag = tag.getCompound("gearType");
        for (Direction direction : Direction.values())
            this.gearType.put(direction, gearTypeTag.getInt(direction.getName()));
    }
}