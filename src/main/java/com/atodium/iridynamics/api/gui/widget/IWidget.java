package com.atodium.iridynamics.api.gui.widget;

import com.atodium.iridynamics.api.gui.ISyncedWidgetList;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

public interface IWidget {
    ModularGuiInfo getGuiInfo();

    void setGuiInfo(ModularGuiInfo info);

    ISyncedWidgetList getWidgetList();

    void setWidgetList(ISyncedWidgetList list);

    int getWidgetId();

    void setWidgetId(int id);

    int getWidth();

    int getHeight();

    void setWidth(int width);

    void setHeight(int height);

    int getX();

    int getY();

    void setX(int x);

    void setY(int y);

    boolean isEnable();

    void setEnable(boolean enable);

    default boolean isInRange(double x, double y) {
        int posX = this.getX();
        int posY = this.getY();
        return MathUtil.between(x, posX, posX + this.getWidth()) && MathUtil.between(y, posY, posY + this.getHeight());
    }

    default void initWidget(ModularGuiInfo info) {
        this.setGuiInfo(info);
    }

    default void receiveMessageFromServer(FriendlyByteBuf data) {

    }

    default void receiveMessageFromClient(FriendlyByteBuf data) {

    }

    default void detectAndSendChanges() {

    }

    default void onMouseHovered(int mouseX, int mouseY) {

    }

    default boolean onMouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    default boolean onMouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    default boolean onMouseClickMove(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    default boolean onMouseScrolled(double mouseX, double mouseY, double move) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default void renderLabels(PoseStack transform, int mouseX, int mouseY, int guiLeft, int guiTop) {

    }

    @OnlyIn(Dist.CLIENT)
    default void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY, int guiLeft, int guiTop) {

    }

    default void writePacketToClient(int widgetId, Consumer<FriendlyByteBuf> consumer) {
        this.getWidgetList().writeToClient(widgetId, consumer);
    }

    default void writePacketToServer(int widgetId, Consumer<FriendlyByteBuf> consumer) {
        this.getWidgetList().writeToServer(widgetId, consumer);
    }
}