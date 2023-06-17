package com.atodium.iridynamics.api.gui;

import com.atodium.iridynamics.Iridynamics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Arrays;
import java.util.List;

public class ModularScreen extends AbstractContainerScreen<ModularContainer> {
    public static final TextureArea DEFAULT_BACKGROUND = TextureArea.createFullTexture(new ResourceLocation(Iridynamics.MODID, "textures/gui/default_background.png"));
    public static final TextureArea BACK = TextureArea.createFullTexture(new ResourceLocation(Iridynamics.MODID, "textures/gui/back.png"));
    public static final TextureArea REFRESH = TextureArea.createFullTexture(new ResourceLocation(Iridynamics.MODID, "textures/gui/refresh.png"));
    private final ModularGuiInfo info;

    public ModularScreen(int window, ModularGuiInfo info, IModularGuiHolder<?>[] parentGuiHolders, Inventory inventory, Component title) {
        super(new ModularContainer(null, window, info, parentGuiHolders), inventory, title);
        this.info = info;
    }

    public ModularGuiInfo getGuiInfo() {
        return this.info;
    }

    public ModularContainer getContainer() {
        return this.menu;
    }

    @Override
    public void init() {
        this.imageWidth = this.info.getWidth();
        this.imageHeight = this.info.getHeight();
        super.init();
    }

    @Override
    public void render(PoseStack transform, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(transform);
        this.info.getBackground().draw(transform, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        List<Component> titles = Arrays.stream(this.menu.getParentGuiHolders()).map((holder) -> holder.getTitle(this.menu.getGuiInfo().getPlayer())).toList();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < titles.size(); i++) {
            text.append(titles.get(i).getString());
            if (i != titles.size() - 1) text.append(ChatFormatting.GREEN).append(" > ").append(ChatFormatting.RESET);
        }
        drawString(transform, this.font, text.toString(), this.leftPos + 1, this.topPos - 11, 0xffffff);
        super.render(transform, mouseX, mouseY, partialTicks);
        this.renderTooltip(transform, mouseX, mouseY);
        this.info.handleMouseHovered(mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack transform, int mouseX, int mouseY) {
        this.info.renderLabels(transform, mouseX - this.leftPos, mouseY - this.topPos, this.leftPos, this.topPos);
    }

    @Override
    protected void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY) {
        this.info.renderBg(transform, partialTicks, mouseX - this.leftPos, mouseY - this.topPos, this.leftPos, this.topPos);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        return this.info.handleMouseClicked(mouseX - this.leftPos, mouseY - this.topPos, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        return this.info.handleMouseReleased(mouseX - this.leftPos, mouseY - this.topPos, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
        return this.info.handleMouseClickMove(mouseX - this.leftPos, mouseY - this.topPos, mouseButton, dragX, dragY);
    }
}