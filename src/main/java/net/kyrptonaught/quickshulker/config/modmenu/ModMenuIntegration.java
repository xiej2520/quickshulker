package net.kyrptonaught.quickshulker.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.client.QuickShulkerModClient;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (screen) -> {
            ConfigOptions options = QuickShulkerMod.getConfig();
            CustomKeyBinding keybinding = QuickShulkerModClient.getKeybinding();

            ConfigScreen configScreen = new ConfigScreen(screen, new TranslatableText("key.quickshulker.config.category.title"));
            configScreen.setSavingEvent(() -> {
                QuickShulkerMod.config.save();
            });
            ConfigSection activationSection = new ConfigSection(configScreen, new TranslatableText("key.quickshulker.config.category.activation"));
            activationSection.addConfigItem(new KeybindItem(new TranslatableText("key.quickshulker.config.keybinding"), keybinding.rawKey, ConfigOptions.defaultKeybind).setSaveConsumer(value -> keybinding.setRaw(value)));
            activationSection.addConfigItem(new BooleanItem(new TranslatableText("key.quickshulker.config.keybind"), options.keybind, true).setSaveConsumer(value -> options.keybind = value));
            activationSection.addConfigItem(new BooleanItem(new TranslatableText("key.quickshulker.config.rightClick"), options.rightClickToOpen, true).setSaveConsumer(value -> options.rightClickToOpen = value));
            activationSection.addConfigItem(new BooleanItem(new TranslatableText("key.quickshulker.config.keybindInInv"), options.keybindInInv, true).setSaveConsumer(value -> options.keybindInInv = value));
            activationSection.addConfigItem(new BooleanItem(new TranslatableText("key.quickshulker.config.rightClickInInv"), options.rightClickInv, true).setSaveConsumer(value -> options.rightClickInv = value));

            ConfigSection optionsSection = new ConfigSection(configScreen, new TranslatableText("key.quickshulker.config.category.options"));
            optionsSection.addConfigItem(new BooleanItem(new TranslatableText("key.quickshulker.config.rightClickClose"), options.rightClickClose, false).setSaveConsumer(value -> options.rightClickClose = value));

            //SubItem subItem = (SubItem) optionsSection.addConfigItem(new SubItem(new TranslatableText("key.quickshulker.config.category.bundleing"), true));
            //subItem.addConfigItem(new BooleanItem(new TranslatableText("key.quickshulker.config.supportsBundlingInsert"), options.supportsBundlingInsert, true).setSaveConsumer(value -> options.supportsBundlingInsert = value));
            //subItem.addConfigItem(new BooleanItem(new TranslatableText("key.quickshulker.config.supportsBundlingPickup"), options.supportsBundlingPickup, true).setSaveConsumer(value -> options.supportsBundlingPickup = value));
            //subItem.addConfigItem(new BooleanItem(new TranslatableText("key.quickshulker.config.supportsBundlingExtract"), options.supportsBundlingExtract, true).setSaveConsumer(value -> options.supportsBundlingExtract = value));

            ConfigSection enabledSection = new ConfigSection(configScreen, new TranslatableText("key.quickshulker.config.category.enabled"));
            enabledSection.addConfigItem(new BooleanItem(new TranslatableText("key.quickshulker.config.quickShulkerBox"), options.quickShulkerBox, true).setSaveConsumer(value -> options.quickShulkerBox = value).setRequiresRestart());
            enabledSection.addConfigItem(new BooleanItem(new TranslatableText("key.quickshulker.config.quickCraftingTable"), options.quickCraftingTables, true).setSaveConsumer(value -> options.quickCraftingTables = value).setRequiresRestart());
            enabledSection.addConfigItem(new BooleanItem(new TranslatableText("key.quickshulker.config.quickStonecutter"), options.quickStonecutter, true).setSaveConsumer(value -> options.quickStonecutter = value).setRequiresRestart());
            enabledSection.addConfigItem(new BooleanItem(new TranslatableText("key.quickshulker.config.quickEnderChest"), options.quickEnderChest, true).setSaveConsumer(value -> options.quickEnderChest = value).setRequiresRestart());
            enabledSection.addConfigItem(new BooleanItem(new TranslatableText("key.quickshulker.config.fillOpenedBackground"), options.fillOpenedBackground, true).setSaveConsumer(value -> options.fillOpenedBackground = value));
            enabledSection.addConfigItem(new IntegerItem(new TranslatableText("key.quickshulker.config.colorBackground"), options.colorBackground, (40 << 16) + (240 << 8) + (40 << 0)).setSaveConsumer(value -> options.colorBackground = value));

            return configScreen;
        };
    }
}