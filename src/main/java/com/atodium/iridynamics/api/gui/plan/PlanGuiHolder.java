package com.atodium.iridynamics.api.gui.plan;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.gui.IModularGuiHolder;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import com.atodium.iridynamics.api.gui.TextureArea;
import com.atodium.iridynamics.api.gui.widget.ButtonWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

public class PlanGuiHolder implements IModularGuiHolder<PlanGuiHolder> {
    public static final TextureArea BACKGROUND = TextureArea.createFullTexture(Iridynamics.rl("textures/gui/plan/background.png"));
    public static final TextureArea BUTTON = TextureArea.createFullTexture(Iridynamics.rl("textures/gui/plan/button.png"));

    private IPlanBlockEntity planInfoProvider;

    public PlanGuiHolder() {

    }

    public PlanGuiHolder(IPlanBlockEntity planInfoProvider) {
        this.planInfoProvider = planInfoProvider;
    }

    public IPlanBlockEntity planInfoProvider() {
        return this.planInfoProvider;
    }

    @Override
    public PlanGuiCodec getCodec() {
        return PlanGuiCodec.INSTANCE;
    }

    @Override
    public Component getTitle(Player player) {
        return new TranslatableComponent("gui.iridynamics.plan.title");
    }

    @Override
    public ModularGuiInfo createGuiInfo(Player player) {
        ModularGuiInfo.Builder builder = ModularGuiInfo.builder(168, 114).background(BACKGROUND);
        for (int i = 0; i < this.planInfoProvider.getPlanCount(); i++) {
            int x = i % 9;
            int y = i / 9;
            builder.widget(i, new ButtonWidget(i, x * 18 + 3, y * 18 + 3, 18, 18).setReleaseCallback((data, container, time) -> {
                this.planInfoProvider.callback(data.count());
                ModularGuiInfo.backToParentGui(container);
            }).setRenderer(BUTTON.merge(this.planInfoProvider.getOverlayRenderer(i))));
        }
        return builder.build(player);
    }
}