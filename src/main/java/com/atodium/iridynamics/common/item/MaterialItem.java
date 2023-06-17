package com.atodium.iridynamics.common.item;

import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.ModMaterials;
import com.atodium.iridynamics.api.material.SolidShape;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class MaterialItem extends Item {
    protected final SolidShape shape;
    protected final List<MaterialBase> generatedMaterials;

    public MaterialItem(Item.Properties properties, SolidShape shape) {
        super(properties);
        this.shape = shape;
        this.generatedMaterials = Lists.newArrayList();
        MaterialBase.REGISTRY.values().forEach((material) -> {
            if (shape.generateMaterial(material)) {
                this.generatedMaterials.add(material);
                MaterialEntry.registerMaterialItem(shape, material, this);
            }
        });
    }

    public static SolidShape getItemShape(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof MaterialItem materialItem) {
            return materialItem.getShape();
        }
        return null;
    }

    public static MaterialBase getItemMaterial(ItemStack stack) {
        if (stack.getItem() instanceof MaterialItem) {
            CompoundTag tag = stack.getOrCreateTagElement("material");
            if (!tag.contains("material")) {
                tag.putString("material", ModMaterials.IRON.getName());
            }
            return MaterialBase.getMaterialByName(tag.getString("material"));
        }
        return null;
    }

    public static MaterialEntry getItemMaterialEntry(ItemStack stack) {
        MaterialBase material = getItemMaterial(stack);
        SolidShape shape = getItemShape(stack);
        if (material != null && shape != null) {
            return MaterialEntry.of(shape, material);
        }
        return null;
    }

    public SolidShape getShape() {
        return this.shape;
    }

    public ItemStack createItemStack(MaterialBase material) {
        return this.createItemStack(material, 1);
    }

    public ItemStack createItemStack(MaterialBase material, int count) {
        ItemStack stack = new ItemStack(this, count);
        stack.getOrCreateTagElement("material").putString("material", material.getName());
        return stack;
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
        if (this.allowdedIn(tab)) {
            this.generatedMaterials.forEach((material) -> list.add(MaterialEntry.getMaterialItemStack(this.shape, material, 1)));
        }
    }

    public int getItemColor(ItemStack stack, int tintIndex) {
        MaterialBase material = getItemMaterial(stack);
        if (material != null) {
            return material.getRenderInfo().color();
        }
        return 0xffffff;
    }

    @Override
    public Component getName(ItemStack stack) {
        SolidShape shape = getItemShape(stack);
        MaterialBase material = getItemMaterial(stack);
        if (shape != null && material != null) {
            return new TextComponent(shape.getLocalizedName(material));
        }
        return TextComponent.EMPTY;
    }
}