package net.kyrptonaught.kyrptconfig.config.screen.items;

import net.kyrptonaught.kyrptconfig.config.screen.NotSuckyButton;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;

public class BooleanItem extends ConfigItem<Boolean> {
    private final NotSuckyButton boolWidget;

    public BooleanItem(Text name, Boolean value, Boolean defaultValue) {
        super(name, value, defaultValue);
        this.boolWidget = new NotSuckyButton(0, 0, 100, 20, new LiteralText("BoolButton"), widget -> {
            setValue(!this.value);
        });
        setValue(value);
        useDefaultResetBTN();
    }

    @Override
    public void setValue(Boolean value) {
        super.setValue(value);
        if (value) {
            boolWidget.setMessage((new TranslatableText("key.kyrptconfig.config.true")).asString());
            boolWidget.setButtonColor(DyeColor.LIME.getFireworkColor());
        } else {
            boolWidget.setMessage((new TranslatableText("key.kyrptconfig.config.false")).asString());
            boolWidget.setButtonColor(DyeColor.RED.getFireworkColor());
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        boolWidget.mouseClicked(mouseX, mouseY, button);
    }

    //@Override
    //public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
    //    super.render(matrices, x, y, mouseX, mouseY, delta);
    //    this.boolWidget.y = y;
    //    this.boolWidget.x = resetButton.x - resetButton.getWidth() - (boolWidget.getWidth() / 2) - 20;
    //    boolWidget.render(matrices, mouseX, mouseY, delta);
    //}

    @Override
    public void render(int x, int y, int mouseX, int mouseY, float delta) {
        super.render(x, y, mouseX, mouseY, delta);
        this.boolWidget.y = y;
        this.boolWidget.x = resetButton.x - resetButton.getWidth() - (boolWidget.getWidth() / 2) - 20;

        boolWidget.render(mouseX, mouseY, delta);
    }
}