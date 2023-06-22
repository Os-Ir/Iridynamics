package com.atodium.iridynamics.common.blockEntity;

import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.ModMaterials;
import com.atodium.iridynamics.api.material.ModSolidShapes;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

public class PlacedStoneBlockEntity extends SyncedBlockEntity {
    public static final MaterialBase[] RESULT = new MaterialBase[]{ModMaterials.STONE, ModMaterials.FLINT, ModMaterials.COPPER, ModMaterials.IRON, ModMaterials.TIN, ModMaterials.LEAD, ModMaterials.BISMUTH, ModMaterials.ZINC, ModMaterials.ANTIMONY};
    public static final double[] WEIGHTS = new double[]{2.0, 4.0, 1.0, 1.0, 0.5, 0.3, 0.3, 0.3, 0.3};

    private MaterialBase material;

    public PlacedStoneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLACED_STONE.get(), pos, state);
    }

    public MaterialBase getMaterial() {
        if (!this.level.isClientSide && this.material == null) {
            this.material = RESULT[MathUtil.getWeightedRandom(WEIGHTS)];
            this.markForSync();
        }
        if (this.level.isClientSide && this.material == null) return ModMaterials.STONE;
        return this.material;
    }

    public ItemStack getTakeItem() {
        MaterialBase m = this.getMaterial();
        if (m == ModMaterials.STONE) return new ItemStack(ModItems.CRUSHED_STONE.get());
        if (m == ModMaterials.FLINT) return new ItemStack(Items.FLINT);
        return MaterialEntry.getMaterialItemStack(ModSolidShapes.ORE_NUGGET, m);
    }

    @Override
    protected CompoundTag writeSyncData(CompoundTag tag) {
        tag.putString("material", this.getMaterial().getName());
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.material = MaterialBase.getMaterialByName(tag.getString("material"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.putString("material", this.getMaterial().getName());
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.material = MaterialBase.getMaterialByName(tag.getString("material"));
    }
}