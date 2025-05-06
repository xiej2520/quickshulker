package net.kyrptonaught.kyrptconfig.config.screen.items;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class SubItem extends ConfigItem {
    boolean expanded = false;
    int subStart = 0;
    List<ConfigItem> configs = new ArrayList<>();

    public SubItem(Text name, boolean isExpanded) {
        super(name, null, null);
        this.expanded = isExpanded;
    }

    public SubItem(Text name) {
        this(name, false);
    }

    public boolean requiresRestart() {
        for (ConfigItem item : configs) {
            if (item.requiresRestart())
                return true;
        }
        return false;
    }

    public void save() {
        for (ConfigItem item : configs)
            item.save();
    }

    public boolean isValueDefault() {
        for (ConfigItem item : configs) {
            if (!item.isValueDefault())
                return false;
        }
        return true;
    }

    public void tick() {
        for (ConfigItem item : configs)
            item.tick();
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (mouseY > subStart && mouseY < subStart + 20)
            expanded = !expanded;

        if (expanded) {
            for (ConfigItem item : configs)
                item.mouseClicked(mouseX, mouseY, button);
        }
    }

    public boolean charTyped(char chr, int modifiers) {
        if (expanded) {
            for (ConfigItem item : configs)
                if (item.charTyped(chr, modifiers))
                    return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (expanded) {
            for (ConfigItem item : configs)
                if (item.keyPressed(keyCode, scanCode, modifiers))
                    return true;
        }
        return false;
    }

    public int getSize() {
        if (expanded) {
            int size = 20;
            for (ConfigItem item : configs) {
                size += item.getSize() + 3;
            }
            return size;
        }
        return 20;
    }

    public void clearConfigItems() {
        configs.clear();
    }

    public ConfigItem addConfigItem(ConfigItem item) {
        this.configs.add(item);
        return item;
    }

    //@Override
    //public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
    //    super.render(matrices, x, y, mouseX, mouseY, delta);
    //    MinecraftClient.getInstance().textRenderer.draw(matrices, expanded ? "-" : "+", x - 10, y + 5, 16777215);
    //    subStart = y;
    //    if (expanded) {
    //        int runningY = subStart + 23;
    //        for (ConfigItem item : configs) {
    //            item.render(matrices, 30, runningY, mouseX, mouseY, delta);
    //            runningY += item.getSize() + 3;
    //        }
    //    }
    //}
    @Override
    public void render(int x, int y, int mouseX, int mouseY, float delta) {
        super.render(x, y, mouseX, mouseY, delta);
        MinecraftClient.getInstance().textRenderer.draw(expanded ? "-" : "+", x - 10, y + 5, 16777215);
        subStart = y;
        if (expanded) {
            int runningY = subStart + 23;
            for (ConfigItem item : configs) {
                item.render(30, runningY, mouseX, mouseY, delta);
                runningY += item.getSize() + 3;
            }
        }
    }
}
