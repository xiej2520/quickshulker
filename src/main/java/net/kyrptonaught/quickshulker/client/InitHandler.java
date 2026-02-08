package net.kyrptonaught.quickshulker.client;

import malilib.config.JsonModConfig;
import malilib.config.JsonModConfig.ConfigDataUpdater;
import malilib.config.util.ConfigUpdateUtils.ChainedConfigDataUpdater;
import malilib.event.InitializationHandler;
import malilib.registry.Registry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.config.ConfigScreen;
import net.kyrptonaught.quickshulker.config.ClientConfigs;
import net.kyrptonaught.quickshulker.config.QuickShulkerHotkeyProvider;

@Environment(EnvType.CLIENT)
public class InitHandler implements InitializationHandler {
    @Override
    public void registerModHandlers() {
        ConfigDataUpdater updater = new ChainedConfigDataUpdater();
        Registry.CONFIG_MANAGER.registerConfigHandler(JsonModConfig.createJsonModConfig(QuickShulkerMod.MOD_INFO, ClientConfigs.CURRENT_VERSION, ClientConfigs.CATEGORIES, updater));

        Registry.CONFIG_SCREEN.registerConfigScreenFactory(QuickShulkerMod.MOD_INFO, ConfigScreen::create);
        Registry.CONFIG_TAB.registerConfigTabSupplier(QuickShulkerMod.MOD_INFO, ConfigScreen::getConfigTabs);

        Registry.HOTKEY_MANAGER.registerHotkeyProvider(QuickShulkerHotkeyProvider.INSTANCE);
    }
}
