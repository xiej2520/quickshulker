package net.kyrptonaught.kyrptconfig.config.screen.items;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kyrptonaught.kyrptconfig.config.screen.NotSuckyButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class ConfigItem<T> {
    private Component fieldTitle;
    private List<Component> toolTipText;
    protected Consumer<T> saveConsumer, valueUpdatedEvent;
    protected NotSuckyButton resetButton;
    protected T value, defaultValue;
    private boolean requiresRestart = false;
    private boolean isHidden = false;

    public ConfigItem(Component name, T value, T defaultValue) {
        this.fieldTitle = name;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public ConfigItem<?> setSaveConsumer(Consumer<T> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }

    public ConfigItem<?> setValueUpdatedEvent(Consumer<T> valueUpdatedEvent) {
        this.valueUpdatedEvent = valueUpdatedEvent;
        return this;
    }

    public ConfigItem<?> setRequiresRestart() {
        requiresRestart = true;
        ((MutableComponent) fieldTitle).append(" *");
        return this;
    }

    public ConfigItem<?> setTitleText(Component title) {
        this.fieldTitle = title;
        return this;
    }

    public ConfigItem<?> setToolTipWithNewLine(String translatableKey) {
        String[] translated = Language.getInstance().getOrDefault(translatableKey).split("\n");
        this.toolTipText = new ArrayList<>();
        for (String line : translated) {
            this.toolTipText.add(new TextComponent(line));
        }

        return this;
    }

    public ConfigItem<?> setToolTip(Component toolTip) {
        this.toolTipText = Arrays.asList(toolTip);
        return this;
    }

    public ConfigItem<?> setToolTip(Component... toolTips) {
        this.toolTipText = Arrays.asList(toolTips);
        return this;
    }

    public ConfigItem<?> setHidden(boolean hidden) {
        this.isHidden = hidden;
        return this;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean requiresRestart() {
        return requiresRestart;
    }

    protected void runSaveConsumer(T value) {
        if (saveConsumer != null && value != null)
            saveConsumer.accept(value);
    }

    public void save() {
        runSaveConsumer(value);
    }

    public int getSize() {
        if (isHidden) return 0;
        return getHeaderSize() + getContentSize();
    }

    public int getHeaderSize() {
        return 20;
    }

    public int getContentSize() {
        return 0;
    }

    public void useDefaultResetBTN() {
        this.resetButton = new NotSuckyButton(0, 0, 35, 20, new TranslatableComponent("key.kyrptconfig.config.reset"), widget -> {
            resetToDefault();
        });
    }

    public void resetToDefault() {
        setValue(defaultValue);
    }

    public boolean isValueDefault() {
        return value.equals(defaultValue);
    }

    public void setValue(T value) {
        this.value = value;
        if (valueUpdatedEvent != null)
            valueUpdatedEvent.accept(this.value);
    }

    public void tick() {
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHidden) return;
        if (resetButton != null)
            resetButton.mouseClicked(mouseX, mouseY, button);
    }

    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    //public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
    //    if (isHidden) return;

    //    int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
    //    int height = y + getHeaderSize();
    //    if (mouseY > y && mouseY < height)
    //        DrawableHelper.fill(matrices, 0, y - 1, width, height + 1, ColorHelper.Argb.getArgb(255, 55, 55, 55));

    //    MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, this.fieldTitle, x, y + 6, 16777215);
    //    if (resetButton != null) {
    //        this.resetButton.setY(y);
    //        this.resetButton.setX(width - resetButton.getWidth() - 20);
    //        resetButton.active = !isValueDefault();
    //        resetButton.render(matrices, mouseX, mouseY, delta);
    //    }
    //}
    public void render(PoseStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        if (isHidden) return;

        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = y + getHeaderSize();
        if (mouseY > y && mouseY < height)
            GuiComponent.fill(matrices, 0, y - 1, width, height + 1, 16725815);

        Minecraft.getInstance().font.drawShadow(matrices, this.fieldTitle, x, y + 6, 16777215);
        if (resetButton != null) {
            this.resetButton.y = y;
            this.resetButton.x = width - resetButton.getWidth() - 20;
            resetButton.active = !isValueDefault();
            resetButton.render(matrices, mouseX, mouseY, delta);
        }
    }

    //public void render2(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
    //    if (isHidden) return;
    //    if (mouseX > x && mouseX < x + MinecraftClient.getInstance().textRenderer.getWidth(fieldTitle) &&
    //            mouseY > y && mouseY < y + 12)
    //        renderToolTip(matrices, mouseX, mouseY);
    //}
    public void render2(PoseStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        if (isHidden) return;
        if (mouseX > x && mouseX < x + Minecraft.getInstance().font.width(fieldTitle) &&
                mouseY > y && mouseY < y + 12)
            renderToolTip(matrices, mouseX, mouseY);
    }

    //public void renderToolTip(MatrixStack matrices, int x, int y) {
    //    if (toolTipText != null && requiresRestart) {
    //        List<Text> newList = new ArrayList<>(toolTipText);
    //        newList.add(Text.translatable("key.kyrptconfig.config.restartRequired"));
    //        MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, newList, x, y);
    //    } else if (toolTipText != null)
    //        MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, toolTipText, x, y);
    //    else if (requiresRestart) {
    //        MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, Text.translatable("key.kyrptconfig.config.restartRequired"), x, y);
    //    }
    //}
    public void renderToolTip(PoseStack matrices, int x, int y) {
        if (toolTipText != null && requiresRestart) {
            List<Component> newList = new ArrayList<>(toolTipText);
            newList.add(new TranslatableComponent("key.kyrptconfig.config.restartRequired"));
            Minecraft.getInstance().screen.renderComponentTooltip(matrices, newList, x, y);
        } else if (toolTipText != null)
            Minecraft.getInstance().screen.renderComponentTooltip(matrices, toolTipText, x, y);
        else if (requiresRestart) {
            Minecraft.getInstance().screen.renderTooltip(matrices, new TranslatableComponent("key.kyrptconfig.config.restartRequired"), x, y);
        }
    }
}
