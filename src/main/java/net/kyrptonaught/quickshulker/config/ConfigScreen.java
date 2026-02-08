package net.kyrptonaught.quickshulker.config;

import com.google.common.collect.ImmutableList;
import malilib.config.option.ConfigInfo;
import malilib.gui.BaseScreen;
import malilib.gui.config.BaseConfigScreen;
import malilib.gui.config.BaseConfigTab;
import malilib.gui.config.ConfigTab;
import malilib.gui.tab.ScreenTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;

import static net.kyrptonaught.quickshulker.QuickShulkerMod.MOD_INFO;

@Environment(EnvType.CLIENT)
public class ConfigScreen {
    private static final BaseConfigTab GENERIC = new BaseConfigTab(MOD_INFO, "generic", 160, getAllConfigs(), ConfigScreen::create);

    private static final ImmutableList<ConfigTab> CONFIG_TABS = ImmutableList.of(
        GENERIC
    );

    private static final ImmutableList<ScreenTab> ALL_TABS = ImmutableList.of(
        GENERIC
    );

    public static void open() {
        BaseScreen.openScreen(create());
    }

    public static BaseConfigScreen create() {
        return new BaseConfigScreen(MOD_INFO, ALL_TABS, GENERIC, "quickshulker.title.screen.configs");
    }

    public static ImmutableList<ConfigTab> getConfigTabs() {
        return CONFIG_TABS;
    }

    private static ImmutableList<ConfigInfo> getAllConfigs() {
        ArrayList<ConfigInfo> list = new ArrayList<>(ClientConfigs.Options.OPTIONS);
        list.addAll(ClientConfigs.HotKeys.HOTKEY_LIST);

        //ConfigUtils.sortConfigsInPlaceByDisplayName(list);

        return ImmutableList.copyOf(list);
    }

}
