package com.atodium.iridynamics.api.gui;

import com.atodium.iridynamics.api.gui.widget.ISlotWidget;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;

public interface ISyncedWidgetList {
    void writeToClient(int widgetId, Consumer<FriendlyByteBuf> consumer);

    void writeToServer(int widgetId, Consumer<FriendlyByteBuf> consumer);

    void notifySlotChange(ISlotWidget widget, boolean isEnable);
}