package com.atodium.iridynamics.common.tool;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.material.ModMaterials;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.tool.MaterialToolBase;
import com.atodium.iridynamics.api.tool.ToolRenderInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class ToolChisel extends MaterialToolBase {
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(Iridynamics.MODID, "chisel");
    public static final ToolChisel INSTANCE = new ToolChisel();
    public static final ToolRenderInfo RENDER_INFO = new ToolRenderInfo(INSTANCE);

    @Override
    public ResourceLocation getRegistryName() {
        return REGISTRY_NAME;
    }

    @Override
    public ToolRenderInfo getRenderInfo() {
        return RENDER_INFO;
    }

    @Override
    public float getAttackDamage() {
        return 2.0f;
    }

    @Override
    public boolean validateMaterial(int index, MaterialBase material) {
        switch (index) {
            case 0 -> {
                return material.hasFlag(MaterialBase.GENERATE_TOOL);
            }
            case 1 -> {
                return material == ModMaterials.WOOD;
            }
        }
        return false;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return false;
    }

    @Override
    public Map<Integer, Pair<String, MaterialBase>> getDefaultMaterial() {
        Map<Integer, Pair<String, MaterialBase>> map = new HashMap<>();
        map.put(0, Pair.of("head", ModMaterials.IRON));
        map.put(1, Pair.of("handle", ModMaterials.WOOD));
        return map;
    }
}