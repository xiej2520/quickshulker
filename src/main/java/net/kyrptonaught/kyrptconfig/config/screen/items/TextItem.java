package net.kyrptonaught.kyrptconfig.config.screen.items;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class TextItem extends ConfigItem<String> {

    EditBox valueEntry;

    public TextItem(Component name, String value, String defaultValue) {
        super(name, value, defaultValue);
        useDefaultResetBTN();
        valueEntry = new EditBox(Minecraft.getInstance().font, 0, 0, 96, 17, new TextComponent("Text Entry"));
        setMaxLength(256);
        valueEntry.setValue(value);
        valueEntry.setResponder(this::setValue);
    }

    public TextItem setMaxLength(int length) {
        valueEntry.setMaxLength(length);
        return this;
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
    }

    @Override
    public void resetToDefault() {
        setValue(defaultValue);
        valueEntry.setValue(value);
    }

    @Override
    public void tick() {
        valueEntry.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        return valueEntry.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return valueEntry.charTyped(chr, modifiers);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        valueEntry.mouseClicked(mouseX, mouseY, button);
    }

    //@Override
    //public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
    //    super.render(matrices, x, y, mouseX, mouseY, delta);

    //    if (valueEntry.isFocused())
    //        this.valueEntry.setWidth(150);
    //    else
    //        this.valueEntry.setWidth(96);

    //    this.valueEntry.setY(y + 1);
    //    this.valueEntry.setX(resetButton.getX() - (valueEntry.getWidth()) - 7);

    //    valueEntry.render(matrices, mouseX, mouseY, delta);
    //}
    @Override
    public void render(PoseStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        super.render(matrices, x, y, mouseX, mouseY, delta);

        if (valueEntry.isFocused()) {
            this.valueEntry.setWidth(150);
        } else {
            this.valueEntry.setWidth(96);
        }

        this.valueEntry.y = y + 1;
        this.valueEntry.setX(resetButton.x - (valueEntry.getWidth()) - 7);

        valueEntry.render(matrices, mouseX, mouseY, delta);
    }
}
