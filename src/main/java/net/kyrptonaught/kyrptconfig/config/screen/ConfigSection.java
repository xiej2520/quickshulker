package net.kyrptonaught.kyrptconfig.config.screen;

import net.kyrptonaught.kyrptconfig.config.screen.items.ConfigItem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConfigSection extends Screen {

    Text title;
    public List<ConfigItem> configs = new CopyOnWriteArrayList<>();
    public NotSuckyButton sectionSelectionBTN;
    int selectionIndex = 0;
    int scrollOffset = 0;

    public ConfigSection(ConfigScreen configScreen, Text title) {
        super(title);
        this.title = title;
        this.sectionSelectionBTN = new NotSuckyButton(0, 32, 10, 20, title, widget -> {
            configScreen.setSelectedSection(selectionIndex);
        });
        configScreen.addConfigSection(this);
    }

    public void save() {
        for (ConfigItem configItem : configs) {
            configItem.save();
        }
    }

    public int getTotalSectionSize() {
        int size = configs.size() * 3 + 5;
        for (ConfigItem configItem : configs) {
            size += configItem.getSize();
        }
        return size;
    }

    public ConfigItem addConfigItem(ConfigItem item) {
        this.configs.add(item);
        return item;
    }

    public ConfigItem insertConfigItem(ConfigItem item, int slot) {
        this.configs.add(slot, item);
        return item;
    }

    public ConfigItem removeConfigItem(int slot) {
        return this.configs.remove(slot);
    }

    @Override
    public void tick() {
        super.tick();
        for (ConfigItem configItem : configs) {
            configItem.tick();
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (ConfigItem configItem : configs) {
            if (configItem.keyPressed(keyCode, scanCode, modifiers))
                return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (ConfigItem configItem : configs) {
            if (configItem.charTyped(chr, modifiers))
                return true;
        }
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ConfigItem configItem : configs) {
            configItem.mouseClicked(mouseX, mouseY, button);
        }
        mouseScrolled(mouseX, mouseY, 0); // update scroll if option changes screen size
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scrollOffset = MathHelper.clamp(scrollOffset + (int) (amount * 15), -calculateSectionHeight(), 0);
        return true;
    }

    public int calculateSectionHeight() {
        int visibleHeight = this.height;
        int sectionSize = getTotalSectionSize();
        if (sectionSize <= visibleHeight) return 0;
        return sectionSize - visibleHeight;
    }

    //public void render(MatrixStack matrices, int startY, int mouseX, int mouseY, float delta) {
    //    super.render(matrices, mouseX, mouseY, delta);
    //    int runningY = scrollOffset + startY + 5;
    //    for (ConfigItem configItem : configs) {
    //        configItem.render(matrices, 20, runningY, mouseX, mouseY, delta);
    //        runningY += configItem.getSize() + 3;
    //    }
    //}
    public void render(int startY, int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        int runningY = scrollOffset + startY + 5;
        for (ConfigItem configItem : configs) {
            configItem.render(20, runningY, mouseX, mouseY, delta);
            runningY += configItem.getSize() + 3;
        }
    }

    //public void render2(MatrixStack matrices, int startY, int mouseX, int mouseY, float delta) {
    //    int runningY = scrollOffset + startY + 5;
    //    for (ConfigItem configItem : configs) {
    //        configItem.render2(matrices, 20, runningY, mouseX, mouseY, delta);
    //        runningY += configItem.getSize() + 3;
    //    }
    //}
    public void render2(int startY, int mouseX, int mouseY, float delta) {
        int runningY = scrollOffset + startY + 5;
        for (ConfigItem configItem : configs) {
            configItem.render2(20, runningY, mouseX, mouseY, delta);
            runningY += configItem.getSize() + 3;
        }
    }
}