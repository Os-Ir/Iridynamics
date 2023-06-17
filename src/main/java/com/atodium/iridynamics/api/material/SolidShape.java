package com.atodium.iridynamics.api.material;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import com.atodium.iridynamics.common.item.MaterialItem;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class SolidShape {
    public static final UnorderedRegistry<String, SolidShape> REGISTRY = new UnorderedRegistry<>();

    private final String name;
    private final int[] forgeShape;
    private final int unit;
    private final Predicate<MaterialBase> materialPredicate;
    private final Set<MaterialBase> ignoredMaterials;

    public SolidShape(String name, int[] forgeShape, int unit, Predicate<MaterialBase> materialPredicate) {
        if (REGISTRY.containsKey(name)) {
            throw new IllegalStateException("Solid shape [ " + name + " ] has registered");
        }
        this.name = name;
        if (forgeShape != null) {
            this.forgeShape = new int[49];
            for (int i = 0; i < 49; i++) {
                this.forgeShape[i] = Mth.clamp(forgeShape[i], 0, 7);
            }
        } else {
            this.forgeShape = null;
        }
        this.unit = unit;
        this.materialPredicate = materialPredicate;
        this.ignoredMaterials = new HashSet<>();
        this.register();
    }

    public static SolidShape getShapeByName(String name) {
        if (REGISTRY.containsKey(name)) {
            return REGISTRY.get(name);
        }
        return null;
    }

    public static boolean hasItemForgeShape(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof MaterialItem) {
            return MaterialItem.getItemShape(stack).hasForgeShape();
        } else if (MaterialEntry.MATERIAL_ITEM.containsValue(item)) {
            return MaterialEntry.MATERIAL_ITEM.getKeyForValue(item).shape().hasForgeShape();
        }
        return false;
    }

    public static int[] getItemForgeShape(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof MaterialItem materialItem) {
            return materialItem.getShape().getForgeShape();
        } else if (MaterialEntry.MATERIAL_ITEM.containsValue(item)) {
            return MaterialEntry.MATERIAL_ITEM.getKeyForValue(item).shape().forgeShape;
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public boolean hasForgeShape() {
        return this.forgeShape != null;
    }

    public int[] getForgeShape() {
        return this.forgeShape;
    }

    public int getForgeShapeHeight(int index) {
        if (this.hasForgeShape()) {
            return this.forgeShape[index];
        }
        return 0;
    }

    public int getForgeShapeHeight(int x, int y) {
        if (this.hasForgeShape()) {
            return this.forgeShape[y * 7 + x];
        }
        return 0;
    }

    public int getUnit() {
        return this.unit;
    }

    public void addIgnoredMaterial(MaterialBase... material) {
        this.ignoredMaterials.addAll(Arrays.asList(material));
    }

    public boolean generateMaterial(MaterialBase material) {
        return this.materialPredicate.test(material) && !this.ignoredMaterials.contains(material);
    }

    public String getUnlocalizedName() {
        return Iridynamics.MODID + ".shape." + this.name;
    }

    public String getLocalizedName(MaterialBase material) {
        return I18n.get(this.getUnlocalizedName(), material.getLocalizedName());
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void register() {
        REGISTRY.register(this.name, this);
    }
}