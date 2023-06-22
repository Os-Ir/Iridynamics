package com.atodium.iridynamics.api.gui.widget;

import com.atodium.iridynamics.api.gui.TextureArea;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class ProgressWidget extends WidgetBase {
    protected TextureArea texture;
    protected final Supplier<Float> supplier;
    protected float progress;
    protected final MoveType moveType;

    public ProgressWidget(int x, int y, int width, int height, Supplier<Float> supplier, MoveType moveType) {
        super(x, y, width, height);
        this.supplier = supplier;
        this.moveType = moveType;
    }

    public WidgetBase setTexture(TextureArea texture) {
        this.texture = texture;
        return this;
    }

    @Override
    public WidgetBase setRenderer(IWidgetRenderer renderer) {
        throw new UnsupportedOperationException("Use #setTexture(texture)");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY, int guiLeft,
                         int guiTop) {
        if (this.texture == null) return;
        switch (this.moveType) {
            case HORIZONTAL ->
                    this.texture.drawSubArea(transform, guiLeft + this.getX(), guiTop + this.getY(), (int) (this.getWidth() * this.progress), this.getHeight(), 0.0f, 0.0f, ((int) (this.getWidth() * this.progress)) / ((float) this.getWidth()), 1.0f);
            case HORIZONTAL_INVERTED ->
                    this.texture.drawSubArea(transform, guiLeft + this.getX() + this.getWidth() - (int) (this.getWidth() * this.progress), guiTop + this.getY(), (int) (this.getWidth() * this.progress), this.getHeight(), 1.0f - ((int) (this.getWidth() * this.progress)) / ((float) this.getWidth()), 0.0f, ((int) (this.getWidth() * this.progress)) / ((float) this.getWidth()), 1.0f);
            case VERTICAL ->
                    this.texture.drawSubArea(transform, guiLeft + this.getX(), guiTop + this.getY(), this.getWidth(), (int) (this.getHeight() * this.progress), 0.0f, 0.0f, 1.0f, ((int) (this.getHeight() * this.progress)) / ((float) this.getHeight()));
            case VERTICAL_INVERTED ->
                    this.texture.drawSubArea(transform, guiLeft + this.getX(), guiTop + this.getY() + this.getHeight() - (int) (this.getHeight() * this.progress), this.getWidth(), (int) (this.getHeight() * this.progress), 0, 1 - ((int) (this.getHeight() * this.progress)) / ((float) this.getHeight()), 1, ((int) (this.getHeight() * this.progress)) / ((float) this.getHeight()));
        }
    }

    @Override
    public void detectAndSendChanges() {
        float update = Mth.clamp(this.supplier.get(), 0.0f, 1.0f);
        if (!MathUtil.isEquals(this.progress, update)) {
            this.progress = update;
            this.writePacketToClient(this.id, (buf) -> buf.writeFloat(update));
        }
    }

    @Override
    public void receiveMessageFromServer(FriendlyByteBuf buffer) {
        this.progress = buffer.readFloat();
    }
}