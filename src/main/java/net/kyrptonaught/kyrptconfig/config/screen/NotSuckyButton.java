package net.kyrptonaught.kyrptconfig.config.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class NotSuckyButton extends Button {
    int buttonColor = 16777215;

    public NotSuckyButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        //super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        super(x, y, width, height, message, onPress);
    }

    public void setButtonColor(int color) {
        this.buttonColor = color;
    }

    //@Override
    //public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    //    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    //    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
    //    //These are disabled to prevent text shadows from rending when hidden. Stop removing it dummy
    //    //RenderSystem.enableBlend();
    //    //RenderSystem.enableDepthTest();
    //    drawNineSlicedTexture(matrices, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
    //    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

    //    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    //    int i = this.active ? buttonColor : 0xA0A0A0;
    //    int x = (this.getX() + (width / 2)) - (textRenderer.getWidth(this.getMessage()) / 2);
    //    int y = (this.getY() + (height / 2)) - (9 / 2);
    //    textRenderer.drawWithShadow(matrices, this.getMessage(), x, y, i);
    //}

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Font textRenderer = minecraftClient.font;

        // from AbstractButtonWidget::renderButton
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int k = this.getYImage(this.isHovered());
        //RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.blit(matrices, this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
        this.blit(matrices, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);

        this.renderBg(matrices, minecraftClient, mouseX, mouseY);
        int j = this.active ? buttonColor : 10526880;
        drawCenteredString(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    private int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHovered() || this.isFocused()) {
            i = 2;
        }
        return 46 + i * 20;
    }
}
