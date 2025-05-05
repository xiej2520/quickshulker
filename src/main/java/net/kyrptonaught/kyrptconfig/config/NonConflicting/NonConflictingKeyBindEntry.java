package net.kyrptonaught.kyrptconfig.config.NonConflicting;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.options.ControlsListWidget;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.Collections;
import java.util.List;
@Environment(EnvType.CLIENT)
public class NonConflictingKeyBindEntry extends ControlsListWidget.Entry {
    private final KeyBinding binding;
    private final Text bindingName;
    private final ButtonWidget editButton;
    private final ButtonWidget resetButton;
    private final ControlsOptionsScreen controlsOptionsScreen;
    private final int MaxKeyNameLength;

    public NonConflictingKeyBindEntry(final KeyBinding binding, final Text text, ControlsOptionsScreen controlsOptionsScreen, int maxKeyNameLength) {
        this.binding = binding;
        this.bindingName = text;
        this.controlsOptionsScreen = controlsOptionsScreen;
        this.MaxKeyNameLength = maxKeyNameLength;
        this.editButton = new ButtonWidget(0, 0, 75, 20, text, (buttonWidget) -> controlsOptionsScreen.focusedBinding = binding) {
            protected MutableText getNarrationMessage() {
                return binding.isNotBound() ? new TranslatableText("narrator.controls.unbound", text) : new TranslatableText("narrator.controls.bound", text, super.getNarrationMessage());
            }
        };
        this.resetButton = new ButtonWidget(0, 0, 50, 20, new TranslatableText("controls.reset"), (buttonWidget) -> binding.setKeyCode(binding.getDefaultKeyCode())) {
            protected MutableText getNarrationMessage() {
                return new TranslatableText("narrator.controls.reset", text);
            }
        };
    }

    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        boolean bl = controlsOptionsScreen.focusedBinding == this.binding;
        TextRenderer var10000 = MinecraftClient.getInstance().textRenderer;
        float var10003 = (float) (x + 90 - MaxKeyNameLength);
        int var10004 = y + entryHeight / 2;
        var10000.draw(matrices, this.bindingName, var10003, (float) (var10004 - 9 / 2), 16777215);
        this.resetButton.x = x + 190;
        this.resetButton.y = y;
        this.resetButton.active = !this.binding.isDefault();
        this.resetButton.render(matrices, mouseX, mouseY, tickDelta);
        this.editButton.x = x + 105;
        this.editButton.y = y;
        this.editButton.setMessage(this.binding.getBoundKeyLocalizedText());

        if (bl)
            this.editButton.setMessage((new LiteralText("> ")).append(this.editButton.getMessage().shallowCopy().formatted(Formatting.YELLOW)).append(" <").formatted(Formatting.YELLOW));

        this.editButton.render(matrices, mouseX, mouseY, tickDelta);
    }

    public List<? extends Element> children() {
        return ImmutableList.of(this.editButton, this.resetButton);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.editButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else {
            return this.resetButton.mouseClicked(mouseX, mouseY, button);
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.editButton.mouseReleased(mouseX, mouseY, button) || this.resetButton.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public List<? extends Selectable> method_37025() {
        return ImmutableList.of(this.editButton, this.resetButton);
    }

    @Environment(EnvType.CLIENT)
    public static class CategoryEntry extends ControlsListWidget.Entry {
        private final Text text;
        private final int textWidth;

        public CategoryEntry(Text text) {
            this.text = text;
            this.textWidth = MinecraftClient.getInstance().textRenderer.getWidth(this.text);
        }

        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            TextRenderer var10000 = MinecraftClient.getInstance().textRenderer;
            float var10003 = (float) (MinecraftClient.getInstance().currentScreen.width / 2 - this.textWidth / 2);
            int var10004 = y + entryHeight;
            var10000.draw(matrices, this.text, var10003, (float) (var10004 - 9 - 1), 16777215);
        }

        public boolean changeFocus(boolean lookForwards) {
            return false;
        }

        public List<? extends Element> children() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends Selectable> method_37025() {
            return Collections.emptyList();
        }
    }
}

