package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.gui.IGuiHolderCodec;
import com.atodium.iridynamics.api.gui.ModularContainer;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import com.atodium.iridynamics.api.gui.TextureArea;
import com.atodium.iridynamics.api.gui.impl.BlockEntityCodec;
import com.atodium.iridynamics.api.gui.impl.IBlockEntityGuiHolder;
import com.atodium.iridynamics.api.gui.widget.ScaledDraggableWidget;
import com.atodium.iridynamics.api.research.ResearchModule;
import com.atodium.iridynamics.api.research.ResearchNetwork;
import com.atodium.iridynamics.api.research.ResearchNode;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class ResearchTableBlockEntity extends SyncedBlockEntity implements IBlockEntityGuiHolder<ResearchTableBlockEntity> {
    public static final BlockEntityCodec<ResearchTableBlockEntity> CODEC = BlockEntityCodec.createCodec(Iridynamics.rl("research_table_block_entity"));
    public static final Component TITLE = new TranslatableComponent("gui.iridynamics.research_table.title");
    public static final TextureArea BACKGROUND = TextureArea.createFullTexture(Iridynamics.rl("textures/gui/research/research_table_background.png"));

    public ResearchTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESEARCH_TABLE.get(), pos, state);
    }

    private void drawResearchNodes(ModularContainer container, PoseStack transform, float x, float y, float width, float height, float moveX, float moveY, float scale) {
        ResearchNetwork network = ResearchModule.getPlayerResearchNetwork(container.player(), "agricultural_age");
        float rootX = x + width / 2.0f + moveX - 8.0f;
        float rootY = y + height / 2.0f + moveY - 8.0f;
        for (Map.Entry<ResearchNode, Vector3f> entry : network.position().entrySet()) {
            ResearchNode node = entry.getKey();
            Vector3f pos = entry.getValue();
            float nodeX = rootX + pos.x() * 40.0f * scale;
            float nodeY = rootY + pos.y() * 40.0f * scale;
            node.type().texture().drawInRange(transform, nodeX, nodeY, 16.0f, 16.0f, x, x + width, y, y + height);
        }
    }

    private void drawResearchNodeInfo(ModularContainer container, float moveX, float moveY, float scale, double mouseX, double mouseY, int button) {
        ResearchNetwork network = ResearchModule.getPlayerResearchNetwork(container.player(), "agricultural_age");
    }

    @Override
    public IGuiHolderCodec<IBlockEntityGuiHolder<ResearchTableBlockEntity>> getCodec() {
        return CODEC;
    }

    @Override
    public Component getTitle(Player player) {
        return TITLE;
    }

    @Override
    public ModularGuiInfo createGuiInfo(Player player) {
        return ModularGuiInfo.builder(416, 220).background(BACKGROUND)
                .widget(0, new ScaledDraggableWidget(111, 10, 295, 200, this::drawResearchNodes).setHoveredListener(this::drawResearchNodeInfo))
                .build(player);
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {

    }

    @Override
    protected void readSyncData(CompoundTag tag) {

    }

    @Override
    protected void saveToTag(CompoundTag tag) {

    }

    @Override
    protected void loadFromTag(CompoundTag tag) {

    }
}