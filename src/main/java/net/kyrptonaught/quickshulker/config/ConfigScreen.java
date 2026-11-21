package net.kyrptonaught.quickshulker.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Window;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.osl.config.api.ConfigManager;
import net.ornithemc.osl.config.api.config.option.BaseOption;
import net.ornithemc.osl.config.api.config.option.BooleanOption;
import net.ornithemc.osl.config.api.config.option.StringOption;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

// Apply changes immediately, save on screen close.
@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    private static final int DONE_BUTTON = 0;

    private final Screen parent;
    private OptionList list;

    public ConfigScreen(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void init() {
        Keyboard.enableRepeatEvents(true);
        this.list = new OptionList(this.minecraft, this.width, this.height, 32, this.height - 32);
        this.addButton(new ButtonWidget(DONE_BUTTON, this.width / 2 - 100, this.height - 27, 200, 20, I18n.translate("gui.done")));
    }

    private static String nameKey(BaseOption<?> option) {
        return "quickshulker.config.name." + option.getName().toLowerCase();
    }

    private static String displayName(BaseOption<?> option) {
        return I18n.translate(nameKey(option));
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == DONE_BUTTON) {
            this.minecraft.openScreen(this.parent);
        }
    }

    @Override
    public void handleMouse() {
        super.handleMouse();
        this.list.handleMouse();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        TextFieldWidget colorField = this.list.colorEntry.field;
        if (this.list.isMouseInList(mouseY)) {
            colorField.mouseClicked(mouseX, mouseY, button);
        } else {
            colorField.setFocused(false);
        }
        if (!this.list.mouseClicked(mouseX, mouseY, button)) {
            super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        if (!this.list.mouseReleased(mouseX, mouseY, button)) {
            super.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    protected void keyPressed(char chr, int key) {
        TextFieldWidget colorField = this.list.colorEntry.field;
        if (key == Keyboard.KEY_ESCAPE && colorField.isFocused()) {
            colorField.setFocused(false);
        } else if (key == Keyboard.KEY_ESCAPE) {
            this.minecraft.openScreen(this.parent);
        } else {
            this.list.colorEntry.keyPressed(chr, key);
        }
    }

    @Override
    public void tick() {
        this.list.colorEntry.field.tick();
    }

    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
        ConfigManager.save(ClientConfigs.INSTANCE);
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        this.renderBackground();
        this.list.render(mouseX, mouseY, tickDelta);
        this.drawCenteredString(this.textRenderer, I18n.translate("quickshulker.title.screen.configs"), this.width / 2, 8, 0xFFFFFF);
        String keyName = GameOptions.getKeyName(ClientConfigs.HotKeys.getQuickShulkerKeyCode());
        this.drawCenteredString(this.textRenderer, I18n.translate("quickshulker.config.keybindhint", keyName), this.width / 2, 19, 0xA0A0A0);
        super.render(mouseX, mouseY, tickDelta);

        BaseOption<?> hovered = this.list.getHoveredOption(mouseX, mouseY);
        if (hovered != null) {
            List<String> lines = new ArrayList<>(this.textRenderer.split(I18n.translate(hovered.getDescription()), 200));
            lines.add(0, displayName(hovered));
            this.renderTooltip(lines, mouseX, mouseY);
        }
    }

    private class OptionList extends EntryListWidget {
        private static final int ROW_WIDTH = 340;
        private static final int CONTROL_WIDTH = 100;
        private static final int RESET_WIDTH = 44;

        private final List<OptionEntry<?>> entries = new ArrayList<>();
        private final ColorEntry colorEntry;

        OptionList(Minecraft minecraft, int width, int height, int top, int bottom) {
            super(minecraft, width, height, top, bottom, 24);
            for (BooleanOption option : ClientConfigs.Options.TOGGLES) {
                this.entries.add(new ToggleEntry(option));
            }
            this.colorEntry = new ColorEntry();
            this.entries.add(this.colorEntry);
        }

        @Override
        public void render(int mouseX, int mouseY, float tickDelta) {
            if (this.minecraft.world == null) {
                super.render(mouseX, mouseY, tickDelta);
                return;
            }
            if (!this.visible) {
                return;
            }
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.capScrolling();

            int scale = new Window(this.minecraft).getScale();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(0, this.minecraft.height - this.maxY * scale, this.minecraft.width, (this.maxY - this.minY) * scale);
            int rowX = this.minX + this.width / 2 - this.getRowWidth() / 2 + 2;
            int rowY = this.minY + 4 - (int) this.scrollAmount;
            this.renderList(rowX, rowY, mouseX, mouseY, tickDelta);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            int maxScroll = this.getMaxScroll();
            if (maxScroll > 0) {
                int barX = this.getScrollbarPosition();
                int listHeight = this.maxY - this.minY;
                int thumbHeight = Math.max(32, Math.min(listHeight - 8, listHeight * listHeight / this.getHeight()));
                int thumbY = Math.max(this.minY, (int) this.scrollAmount * (listHeight - thumbHeight) / maxScroll + this.minY);
                fill(barX, this.minY, barX + 6, this.maxY, 0x80000000);
                fill(barX, thumbY, barX + 6, thumbY + thumbHeight, 0xFF808080);
                fill(barX, thumbY, barX + 5, thumbY + thumbHeight - 1, 0xFFC0C0C0);
            }
        }

        @Override
        protected int size() {
            return this.entries.size();
        }

        @Override
        public EntryListWidget.Entry getEntry(int index) {
            return this.entries.get(index);
        }

        @Override
        public int getRowWidth() {
            return ROW_WIDTH;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width / 2 + ROW_WIDTH / 2 + 10;
        }

        BaseOption<?> getHoveredOption(int mouseX, int mouseY) {
            if (!this.isMouseInList(mouseY)) {
                return null;
            }
            int index = this.getEntryAt(mouseX, mouseY);
            return index >= 0 ? this.entries.get(index).option : null;
        }

        private int controlX() {
            return this.width / 2 + ROW_WIDTH / 2 - RESET_WIDTH - CONTROL_WIDTH - 8;
        }

        private abstract class OptionEntry<O extends BaseOption<?>> implements EntryListWidget.Entry {
            final O option;
            private final ButtonWidget resetButton;

            OptionEntry(O option) {
                this.option = option;
                this.resetButton = new ButtonWidget(0, 0, 0, RESET_WIDTH, 20, I18n.translate("controls.reset"));
            }

            abstract void renderControl(int x, int y, int mouseX, int mouseY, float tickDelta);

            boolean controlClicked(int mouseX, int mouseY) {
                return false;
            }

            void onReset() {
            }

            @Override
            public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                String label = ConfigScreen.this.textRenderer.trim(displayName(this.option), controlX() - x - 8);
                ConfigScreen.this.textRenderer.draw(label, x + 4, y + (rowHeight - 8) / 2, 0xFFFFFF);
                this.renderControl(controlX(), y, mouseX, mouseY, tickDelta);

                this.resetButton.x = controlX() + CONTROL_WIDTH + 4;
                this.resetButton.y = y;
                this.resetButton.active = !this.option.isDefault();
                this.resetButton.render(ConfigScreen.this.minecraft, mouseX, mouseY, tickDelta);
            }

            @Override
            public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int relativeX, int relativeY) {
                if (this.resetButton.mouseClicked(ConfigScreen.this.minecraft, mouseX, mouseY)) {
                    this.resetButton.playClickSound(ConfigScreen.this.minecraft.getSoundManager());
                    this.option.reset();
                    this.onReset();
                    return true;
                }
                return this.controlClicked(mouseX, mouseY);
            }

            @Override
            public void renderOutOfBounds(int index, int x, int y, float tickDelta) {
            }

            @Override
            public void mouseReleased(int index, int mouseX, int mouseY, int button, int relativeX, int relativeY) {
            }
        }

        private class ToggleEntry extends OptionEntry<BooleanOption> {
            private final ButtonWidget button;

            ToggleEntry(BooleanOption option) {
                super(option);
                this.button = new ButtonWidget(0, 0, 0, CONTROL_WIDTH, 20, "");
            }

            @Override
            void renderControl(int x, int y, int mouseX, int mouseY, float tickDelta) {
                this.button.x = x;
                this.button.y = y;
                this.button.message = I18n.translate(this.option.get() ? "options.on" : "options.off");
                this.button.render(ConfigScreen.this.minecraft, mouseX, mouseY, tickDelta);
            }

            @Override
            boolean controlClicked(int mouseX, int mouseY) {
                if (this.button.mouseClicked(ConfigScreen.this.minecraft, mouseX, mouseY)) {
                    this.button.playClickSound(ConfigScreen.this.minecraft.getSoundManager());
                    this.option.set(!this.option.get());
                    return true;
                }
                return false;
            }
        }

        // <ai>the screen passes every click to the field itself, see ConfigScreen.mouseClicked</ai>
        private class ColorEntry extends OptionEntry<StringOption> {
            private static final int SWATCH_SIZE = 18;
            private final TextFieldWidget field;

            ColorEntry() {
                super(ClientConfigs.Options.OPENED_BACKGROUND_COLOR);
                this.field = new TextFieldWidget(0, ConfigScreen.this.textRenderer, 0, 0, CONTROL_WIDTH - SWATCH_SIZE - 5, 18);
                this.field.setMaxLength(9);
                this.field.setText(this.option.get());
            }

            @Override
            void renderControl(int x, int y, int mouseX, int mouseY, float tickDelta) {
                // swatch with a border, showing the saved color (red border while the text is invalid)
                // <ai>drawn opaque like the slot highlight, which ignores the alpha</ai>
                boolean valid = ClientConfigs.isValidColor(this.field.getText());
                fill(x, y + 1, x + SWATCH_SIZE, y + 1 + SWATCH_SIZE, valid ? 0xFFA0A0A0 : 0xFFFF5555);
                fill(x + 1, y + 2, x + SWATCH_SIZE - 1, y + SWATCH_SIZE, ClientConfigs.getOpenedBackgroundColor() | 0xFF000000);
                this.field.x = x + SWATCH_SIZE + 4;
                this.field.y = y + 1;
                this.field.render();
            }

            @Override
            void onReset() {
                this.field.setText(this.option.get());
            }

            void keyPressed(char chr, int key) {
                if (this.field.keyPressed(chr, key)) {
                    String text = this.field.getText();
                    if (ClientConfigs.isValidColor(text)) {
                        this.option.set(text);
                    }
                }
            }
        }
    }
}
