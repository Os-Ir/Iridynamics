package com.atodium.iridynamics.api.gui.widget;

import com.atodium.iridynamics.api.gui.ModularContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;

import java.util.function.BiConsumer;

public class ButtonWidget extends WidgetBase {
    private final int count;
    private final BiConsumer<ButtonClickData, ModularContainer> callback, callbackClient;
    private ReleaseButtonCallback releaseCallback;
    private long lastClickTime;

    public ButtonWidget(int count, int x, int y, int width, int height) {
        this(count, x, y, width, height, null, null);
    }

    public ButtonWidget(int count, int x, int y, int width, int height, BiConsumer<ButtonClickData, ModularContainer> callback) {
        this(count, x, y, width, height, callback, null);
    }

    public ButtonWidget(int count, int x, int y, int width, int height, BiConsumer<ButtonClickData, ModularContainer> callback, BiConsumer<ButtonClickData, ModularContainer> callbackClient) {
        super(x, y, width, height);
        this.count = count;
        this.callback = callback;
        this.callbackClient = callbackClient;
    }

    public ButtonWidget setReleaseCallback(ReleaseButtonCallback releaseCallback) {
        this.releaseCallback = releaseCallback;
        return this;
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        ButtonClickData data = new ButtonClickData(this.count, (int) mouseX - this.x, (int) mouseY - this.y, button);
        if (this.callbackClient != null) this.callbackClient.accept(data, this.info.getContainer());
        this.writePacketToServer(this.id, (buf) -> {
            buf.writeBoolean(true);
            data.writeToBuf(buf);
        });
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        return true;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        ButtonClickData data = new ButtonClickData(this.count, (int) mouseX - this.x, (int) mouseY - this.y, button);
        this.writePacketToServer(this.id, (buf) -> {
            buf.writeBoolean(false);
            data.writeToBuf(buf);
        });
        return true;
    }

    @Override
    public void receiveMessageFromClient(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            if (this.callback != null) this.callback.accept(ButtonClickData.readFromBuf(buf), this.info.getContainer());
            this.lastClickTime = System.currentTimeMillis();
        } else if (this.releaseCallback != null)
            this.releaseCallback.accept(ButtonClickData.readFromBuf(buf), this.info.getContainer(), System.currentTimeMillis() - this.lastClickTime);
    }

    @FunctionalInterface
    public interface ReleaseButtonCallback {
        void accept(ButtonClickData data, ModularContainer container, long clickTime);
    }

    public record ButtonClickData(int count, int x, int y, int button) {
        public static ButtonClickData readFromBuf(FriendlyByteBuf buf) {
            return new ButtonClickData(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
        }

        public void writeToBuf(FriendlyByteBuf buf) {
            buf.writeInt(this.count);
            buf.writeInt(this.x);
            buf.writeInt(this.y);
            buf.writeInt(this.button);
        }
    }
}