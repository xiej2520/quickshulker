package net.kyrptonaught.kyrptconfig.config.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class NotSuckyButton extends ButtonWidget {
    int buttonColor = 16777215;

    public NotSuckyButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message.asString(), onPress);
    }

    public void setButtonColor(int color) {
        this.buttonColor = color;
    }

    //@Override
    //public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    //    MinecraftClient minecraftClient = MinecraftClient.getInstance();
    //    TextRenderer textRenderer = minecraftClient.textRenderer;
    //    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    //    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    //    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
    //    int i = this.getYImage(this.isHovered());
    //    RenderSystem.enableBlend();
    //    RenderSystem.defaultBlendFunc();
    //    RenderSystem.enableDepthTest();
    //    this.drawTexture(matrices, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
    //    this.drawTexture(matrices, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
    //    this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
    //    int j = this.active ? buttonColor : 10526880;
    //    drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * //255.0F) << 24);
    //}

    @Override
    public void renderButton(int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;

        // from AbstractButtonWidget::renderButton
        minecraftClient.getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int k = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        this.blit(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
        this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);

        this.renderBg(minecraftClient, mouseX, mouseY);
        int j = this.active ? buttonColor : 10526880;
        drawCenteredString(textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }
}
