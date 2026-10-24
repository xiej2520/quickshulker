package net.kyrptonaught.kyrptconfig.config.screen.items;

import java.math.BigDecimal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class NumberItem<T extends Number> extends ConfigItem<T> {

    protected T min, max;
    EditBox valueEntry;

    public NumberItem(Component name, T value, T defaultValue) {
        super(name, value, defaultValue);
        useDefaultResetBTN();
        valueEntry = new EditBox(Minecraft.getInstance().font, 0, 0, 96, 17, "Number Entry");
        valueEntry.setValue(value.toString());
        valueEntry.setResponder(this::onTyped);
    }

    public NumberItem<T> setMinMax(T min, T max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public void setValue(T value) {
        valueEntry.setValue(fixInput(value).toString());
    }

    public T fixInput(T value) {
        BigDecimal big = new BigDecimal(value.toString());
        if (min != null && big.compareTo(new BigDecimal(min.toString())) < 0)
            return min;
        if (max != null && big.compareTo(new BigDecimal(max.toString())) > 0)
            return max;

        return value;
    }

    public void onTyped(String s) {

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
    //    this.valueEntry.y = y + 2;
    //    this.valueEntry.x = resetButton.x - resetButton.getWidth() - (valueEntry.getWidth() / 2) - 20;
    //    valueEntry.render(matrices, mouseX, mouseY, delta);
    //}
    @Override
    public void render(int x, int y, int mouseX, int mouseY, float delta) {
        super.render(x, y, mouseX, mouseY, delta);
        this.valueEntry.y = y + 2;
        this.valueEntry.x = resetButton.x - resetButton.getWidth() - (valueEntry.getWidth() / 2) - 20;

        valueEntry.render(mouseX, mouseY, delta);
    }
}