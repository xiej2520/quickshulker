package net.kyrptonaught.kyrptconfig.config.screen.items;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kyrptonaught.kyrptconfig.config.screen.NotSuckyButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DyeColor;

public class BooleanItem extends ConfigItem<Boolean> {
    private final NotSuckyButton boolWidget;

    public BooleanItem(Component name, Boolean value, Boolean defaultValue) {
        super(name, value, defaultValue);
        this.boolWidget = new NotSuckyButton(0, 0, 100, 20, new TextComponent("BoolButton"), widget -> {
            setValue(!this.value);
        });
        setValue(value);
        useDefaultResetBTN();
    }

    @Override
    public void setValue(Boolean value) {
        super.setValue(value);
        if (value) {
            boolWidget.setMessage(new TranslatableComponent("key.kyrptconfig.config.true"));
            boolWidget.setButtonColor(DyeColor.LIME.getFireworkColor());
        } else {
            boolWidget.setMessage(new TranslatableComponent("key.kyrptconfig.config.false"));
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
    public void render(PoseStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        super.render(matrices, x, y, mouseX, mouseY, delta);
        this.boolWidget.y = y;
        this.boolWidget.x = resetButton.x - resetButton.getWidth() - (boolWidget.getWidth() / 2) - 20;

        boolWidget.render(matrices, mouseX, mouseY, delta);
    }
}