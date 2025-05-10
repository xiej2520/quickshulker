package net.kyrptonaught.kyrptconfig.config.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class NotSuckyButton extends ButtonWidget {
    int buttonColor = 16777215;

    public NotSuckyButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        //super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        super(x, y, width, height, message.asString(), onPress);
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
    public void renderButton(int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;

        // from AbstractButtonWidget::renderButton
        minecraftClient.getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int k = this.getYImage(this.isHovered());
        //RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        this.blit(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
        this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);

        this.renderBg(minecraftClient, mouseX, mouseY);
        int j = this.active ? buttonColor : 10526880;
        drawCenteredString(textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
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
