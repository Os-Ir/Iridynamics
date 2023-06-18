package com.atodium.iridynamics.api.material.type;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.heat.MaterialHeatInfo;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.api.material.MaterialPhysicalInfo;
import com.atodium.iridynamics.api.material.MaterialToolPropertyInfo;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import com.atodium.iridynamics.api.material.MaterialRenderInfo;
import com.atodium.iridynamics.common.item.MaterialItem;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class MaterialBase {
    public static final UnorderedRegistry<String, MaterialBase> REGISTRY = new UnorderedRegistry<>();

    public static final IMaterialFlag GENERATE_DUST = createFlag(0);
    public static final IMaterialFlag GENERATE_PLATE = createFlag(1);
    public static final IMaterialFlag GENERATE_ROD = createFlag(2);
    public static final IMaterialFlag GENERATE_GEAR = createFlag(3);
    public static final IMaterialFlag GENERATE_CRYSTAL = createFlag(4);
    public static final IMaterialFlag GENERATE_INGOT = createFlag(5);
    public static final IMaterialFlag GENERATE_FOIL = createFlag(6);
    public static final IMaterialFlag GENERATE_SCREW = createFlag(7);
    public static final IMaterialFlag GENERATE_SPRING = createFlag(8);
    public static final IMaterialFlag GENERATE_RING = createFlag(9);
    public static final IMaterialFlag GENERATE_WIRE = createFlag(10);
    public static final IMaterialFlag GENERATE_NUGGET = createFlag(12);
    public static final IMaterialFlag GENERATE_TOOL = createFlag(16);
    public static final IMaterialFlag GENERATE_ORE = createFlag(17);

    private final String name;
    private long flags;
    private MaterialToolPropertyInfo toolPropertyInfo;
    private MaterialRenderInfo renderInfo;
    private MaterialPhysicalInfo physicalInfo;
    private MaterialHeatInfo heatInfo;

    public MaterialBase(String name) {
        this.name = name;
        this.flags = this.getDefaultFlags();
        this.register();
    }

    public MaterialBase(String name, long flags) {
        this.name = name;
        this.flags = flags;
        this.register();
    }

    public static MaterialBase getItemMaterial(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof MaterialItem) {
            return MaterialItem.getItemMaterial(stack);
        } else if (MaterialEntry.MATERIAL_ITEM.containsValue(item)) {
            return MaterialEntry.MATERIAL_ITEM.getKeyForValue(item).material();
        }
        return null;
    }

    public static IMaterialFlag createFlag(int id) {
        if (id < 64 && id >= 0) {
            return () -> id;
        }
        throw new IllegalArgumentException("The maximum Material Flag ID is 63");
    }

    public static long combineFlags(IMaterialFlag... flags) {
        long ret = 0;
        for (IMaterialFlag flag : flags) {
            ret |= flag.getValue();
        }
        return ret;
    }

    abstract ResourceLocation getRegistryName();

    abstract long getDefaultFlags();

    public boolean validatePointTemp(double meltingPoint, double boilingPoint) {
        return boilingPoint >= meltingPoint && meltingPoint >= 0;
    }

    public static MaterialBase getMaterialByName(String name) {
        if (REGISTRY.containsKey(name)) return REGISTRY.get(name);
        return null;
    }

    public MaterialBase addFlag(IMaterialFlag flag) {
        this.flags |= flag.getValue();
        return this;
    }

    public String getName() {
        return this.name;
    }

    public boolean hasFlag(IMaterialFlag flag) {
        return (this.flags & (flag.getValue())) != 0;
    }

    public MaterialBase color(int color) {
        this.renderInfo = new MaterialRenderInfo(color).setMaterial(this);
        return this;
    }

    public MaterialBase setRenderInfo(MaterialRenderInfo renderInfo) {
        this.renderInfo = renderInfo.setMaterial(this);
        return this;
    }

    public MaterialRenderInfo getRenderInfo() {
        if (this.renderInfo == null) this.renderInfo = MaterialRenderInfo.empty(this);
        return this.renderInfo;
    }

    public MaterialBase setToolProperty(int durability, int harvestLevel, float efficiency) {
        this.toolPropertyInfo = new MaterialToolPropertyInfo(durability, harvestLevel, efficiency).setMaterial(this);
        return this;
    }

    public MaterialBase setToolPropertyInfo(MaterialToolPropertyInfo toolPropertyInfo) {
        this.toolPropertyInfo = toolPropertyInfo.setMaterial(this);
        return this;
    }

    public MaterialToolPropertyInfo getToolPropertyInfo() {
        if (this.toolPropertyInfo == null) this.toolPropertyInfo = MaterialToolPropertyInfo.empty(this);
        return this.toolPropertyInfo;
    }

    public MaterialBase setPhysicalInfo(MaterialPhysicalInfo physicalInfo) {
        this.physicalInfo = physicalInfo.setMaterial(this);
        return this;
    }

    public MaterialPhysicalInfo getPhysicalInfo() {
        if (this.physicalInfo == null) this.physicalInfo = MaterialPhysicalInfo.empty(this);
        return this.physicalInfo;
    }

    public boolean hasHeatInfo() {
        return this.heatInfo != null;
    }

    public void setHeatInfo(MaterialHeatInfo heatInfo) {
        this.heatInfo = heatInfo;
    }

    public MaterialHeatInfo getHeatInfo() {
        return this.heatInfo;
    }

    public int getDurability() {
        return this.getToolPropertyInfo().durability();
    }

    public int getHarvestLevel() {
        return this.getToolPropertyInfo().harvestLevel();
    }

    public float getEfficiency() {
        return this.getToolPropertyInfo().efficiency();
    }

    public String getUnlocalizedName() {
        return Iridynamics.MODID + ".material." + this.name;
    }

    public String getLocalizedName() {
        return I18n.get(this.getUnlocalizedName());
    }

    @SuppressWarnings("unchecked")
    public <T extends MaterialBase> T cast() {
        return (T) this;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void register() {
        REGISTRY.register(this.name, this);
    }
}