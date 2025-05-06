package net.kyrptonaught.kyrptconfig.config.screen.items;

import net.kyrptonaught.kyrptconfig.config.screen.NotSuckyButton;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class EnumItem<T extends Enum<?>> extends ConfigItem<T> {
    private final NotSuckyButton displayWidget;
    T[] enumValues;

    public EnumItem(Text name, T[] enums, T value, T defaultValue) {
        super(name, value, defaultValue);
        this.enumValues = enums;
        this.displayWidget = new NotSuckyButton(0, 0, 100, 20, new LiteralText("EnumButton"), widget -> {
            cycleSelectedValue();
        });
        setValue(value);
        useDefaultResetBTN();
    }


    @Override
    public void setValue(T value) {
        super.setValue(value);
        displayWidget.setMessage(value.toString());
    }

    public void cycleSelectedValue() {
        int selected = 0;
        for (int i = 0; i < enumValues.length; i++)
            if (enumValues[i].equals(value)) {
                selected = i;
                break;
            }
        selected++;
        if (selected >= enumValues.length)
            selected = 0;
        setValue(enumValues[selected]);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        displayWidget.mouseClicked(mouseX, mouseY, button);
    }

    //@Override
    //public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
    //    super.render(matrices, x, y, mouseX, mouseY, delta);
    //    this.displayWidget.y = y;
    //    this.displayWidget.x = resetButton.x - resetButton.getWidth() - (displayWidget.getWidth() / 2) - 20;

    //    displayWidget.render(matrices, mouseX, mouseY, delta);
    //}
    @Override
    public void render(int x, int y, int mouseX, int mouseY, float delta) {
        super.render(x, y, mouseX, mouseY, delta);
        this.displayWidget.y = y;
        this.displayWidget.x = resetButton.x - resetButton.getWidth() - (displayWidget.getWidth() / 2) - 20;

        displayWidget.render(mouseX, mouseY, delta);
    }
}
