package com.atodium.iridynamics.common.tool;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.material.ModMaterials;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.tool.MaterialToolBase;
import com.atodium.iridynamics.api.tool.ToolRenderInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class ToolKnife extends MaterialToolBase {
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(Iridynamics.MODID, "knife");
    public static final ToolKnife INSTANCE = new ToolKnife();
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
        return 6.0f;
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
        if (state.is(Blocks.COBWEB) || state.is(BlockTags.LEAVES)) return true;
        Material material = state.getMaterial();
        return material == Material.PLANT || material == Material.REPLACEABLE_PLANT || material == Material.VEGETABLE;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(Blocks.COBWEB)) return 15.0f;
        Material material = state.getMaterial();
        return material == Material.PLANT || material == Material.REPLACEABLE_PLANT || material == Material.VEGETABLE || state.is(BlockTags.LEAVES) ? 1.5F : 1.0F;
    }

    @Override
    public Map<Integer, Pair<String, MaterialBase>> getDefaultMaterial() {
        Map<Integer, Pair<String, MaterialBase>> map = new HashMap<>();
        map.put(0, Pair.of("head", ModMaterials.IRON));
        map.put(1, Pair.of("handle", ModMaterials.WOOD));
        return map;
    }
}