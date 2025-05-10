package net.kyrptonaught.kyrptconfig.config.screen.items;

import net.kyrptonaught.kyrptconfig.config.screen.NotSuckyButton;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

public class KeybindItem extends ConfigItem<String> {
    private final NotSuckyButton keyButton;
    private Boolean isListening = false;

    public KeybindItem(Text name, String key, String defaultKey) {
        super(name, key, defaultKey);
        this.keyButton = new NotSuckyButton(0, 0, 100, 20, getCleanName(key), widget -> {
            this.isListening = !this.isListening;
            if (!this.isListening) {
                widget.setMessage(this.getCleanName(this.value).asString());
                //widget.setTooltip(Tooltip.of(Text.literal(this.value)));
            } else {
                widget.setMessage(new LiteralText("> ").append(this.getCleanName(this.value).append(new LiteralText(" <"))).asString());
            }
        });
        //keyButton.setTooltip(Tooltip.of(Text.literal(this.value)));
        useDefaultResetBTN();
    }

    public void setValue(String value) {
        super.setValue(value);
        isListening = false;
        keyButton.setMessage(this.getCleanName(this.value).asString());
        //keyButton.setTooltip(Tooltip.of(Text.literal(this.value)));
    }

    public Text getCleanName(String str) {
        if (I18n.hasTranslation(value))
            return new TranslatableText(str);
        if (str == null || str.isBlank())
            return new TranslatableText("key.keyboard.unknown");
        return new LiteralText(str.substring(str.length() - 1).toUpperCase());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isListening) {
            // post 1.15: fromKeyCode, getTranslationKey()
            setValue(InputUtil.getKeyCode(keyCode, scanCode).getName());
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                setValue(value);
                return true;
            }
            setValue(InputUtil.getKeyCode(keyCode, scanCode).getName());
            return true;
        }
        return false;
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        boolean handled;
        handled = (keyButton.mouseClicked(mouseX, mouseY, button) || resetButton.mouseClicked(mouseX, mouseY, button));
        if (isListening && !handled) {
            setValue(InputUtil.Type.MOUSE.createFromCode(button).getName());
        }
    }

    //@Override
    //public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
    //    super.render(matrices, x, y, mouseX, mouseY, delta);
    //    this.keyButton.setY(y);

    //    this.keyButton.setX(resetButton.getX() - resetButton.getWidth() - (keyButton.getWidth() / 2) - 20);

    //    keyButton.render(matrices, mouseX, mouseY, delta);
    //}
    @Override
    public void render(int x, int y, int mouseX, int mouseY, float delta) {
        super.render(x, y, mouseX, mouseY, delta);
        this.keyButton.y = y;

        this.keyButton.x = resetButton.x - resetButton.getWidth() - (keyButton.getWidth() / 2) - 20;

        keyButton.render(mouseX, mouseY, delta);
    }
}
