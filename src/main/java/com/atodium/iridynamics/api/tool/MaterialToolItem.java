package com.atodium.iridynamics.api.tool;

import com.atodium.iridynamics.api.material.type.MaterialBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MaterialToolItem extends ToolItem {
    public MaterialToolItem(Item.Properties properties, IToolInfo toolInfo) {
        super(properties, toolInfo);
    }

    public static Map<Integer, Pair<String, MaterialBase>> getToolAllMaterial(ItemStack stack) {
        CompoundTag tag = getToolBaseTag(stack);
        if (tag.contains("material")) {
            ListTag list = tag.getList("material", Tag.TAG_COMPOUND);
            Iterator<Tag> ite = list.iterator();
            Map<Integer, Pair<String, MaterialBase>> map = new HashMap<>();
            while (ite.hasNext()) {
                CompoundTag compound = (CompoundTag) ite.next();
                map.put(compound.getInt("index"), Pair.of(compound.getString("part"), MaterialBase.getMaterialByName(compound.getString("material"))));
            }
            return map;
        }
        return Collections.emptyMap();
    }

    public static MaterialBase getToolMaterial(ItemStack stack, int index) {
        CompoundTag tag = getToolBaseTag(stack);
        if (tag.contains("material")) {
            return MaterialBase.getMaterialByName(((CompoundTag) tag.getList("material", Tag.TAG_COMPOUND).get(index)).getString("material"));
        }
        return null;
    }

    public static MaterialBase getToolMaterial(ItemStack stack, String part) {
        CompoundTag nbt = getToolBaseTag(stack);
        if (nbt.contains("material")) {
            ListTag list = nbt.getList("material", Tag.TAG_COMPOUND);
            for (Tag tag : list) {
                CompoundTag compound = (CompoundTag) tag;
                if (compound.getString("part").equals(part)) {
                    return MaterialBase.getMaterialByName(compound.getString("material"));
                }
            }
        }
        return null;
    }

    public static ItemStack setToolMaterial(ItemStack stack, int index, String part, MaterialBase material) {
        CompoundTag tag = getToolBaseTag(stack);
        if (!tag.contains("material", Tag.TAG_LIST)) {
            tag.put("material", new ListTag());
        }
        ListTag list = tag.getList("material", Tag.TAG_COMPOUND);
        CompoundTag compound = new CompoundTag();
        compound.putInt("index", index);
        compound.putString("part", part);
        compound.putString("material", material.getName());
        list.add(compound);
        return stack;
    }

    @Override
    public ItemStack createItemStack() {
        ItemStack stack = new ItemStack(this);
        this.toolInfo.getDefaultMaterial().forEach((index, pair) -> {
            MaterialBase material = pair.getRight();
            if (!this.validateToolMaterial(index, material)) {
                throw new IllegalArgumentException("Tool material [ " + material.getName() + " ] for index [ " + index + " ] is invalid");
            }
            setToolMaterial(stack, index, pair.getLeft(), material);
        });
        return stack;
    }

    public ItemStack createItemStack(Map<Integer, Pair<String, MaterialBase>> materials) {
        ItemStack stack = new ItemStack(this);
        materials.forEach((index, pair) -> {
            MaterialBase material = pair.getRight();
            if (!this.validateToolMaterial(index, material)) {
                throw new IllegalArgumentException("Tool material [ " + material.getName() + " ] for index [ " + index + " ] is invalid");
            }
            setToolMaterial(stack, index, pair.getLeft(), material);
        });
        return stack;
    }

    public boolean validateToolMaterial(int index, MaterialBase material) {
        return this.toolInfo.validateMaterial(index, material);
    }
}