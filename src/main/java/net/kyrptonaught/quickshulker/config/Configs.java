package net.kyrptonaught.quickshulker.config;

import com.google.common.collect.ImmutableList;
import malilib.MaLiLibConfigs;
import malilib.config.category.BaseConfigOptionCategory;
import malilib.config.category.ConfigOptionCategory;
import malilib.config.option.BooleanConfig;
import malilib.config.option.ColorConfig;
import malilib.config.option.ConfigOption;
import malilib.config.option.HotkeyConfig;
import malilib.input.CancelCondition;
import malilib.input.Context;
import malilib.input.KeyAction;
import malilib.input.KeyBindSettings;
import net.kyrptonaught.quickshulker.QuickShulkerMod;

import java.util.List;

public class Configs {
    public static final int CURRENT_VERSION = 1;

    public static class Options {
        public static final BooleanConfig RIGHT_CLICK_OPEN_IN_HAND              = new BooleanConfig("rightClickOpenInHand", true);
        public static final BooleanConfig KEYBIND_OPEN_IN_HAND                  = new BooleanConfig("keybindOpenInHand", true);
        public static final BooleanConfig KEYBIND_OPEN_IN_INVENTORY             = new BooleanConfig("keybindOpenInInventory", true);
        public static final BooleanConfig RIGHT_CLICK_OPEN_IN_INVENTORY         = new BooleanConfig("rightClickOpenInInventory", true);
        public static final BooleanConfig RIGHT_CLICK_CLOSE_IN_INVENTORY        = new BooleanConfig("rightClickCloseInInventory", true);
        public static final BooleanConfig SUPPORT_BUNDLING_INSERT               = new BooleanConfig("supportBundlingInsert", false);
        public static final BooleanConfig SUPPORT_BUNDLING_PICKUP               = new BooleanConfig("supportBundlingPickup", false);
        public static final BooleanConfig SUPPORT_BUNDLING_EXTRACT              = new BooleanConfig("supportBundlingExtract", false);

        public static final BooleanConfig QUICK_SHULKER_BOX                     = new BooleanConfig("quickShulkerBox", true);
        public static final BooleanConfig QUICK_CRAFTING_TABLE                  = new BooleanConfig("quickCraftingTable", true);
        public static final BooleanConfig QUICK_ENDER_CHEST                     = new BooleanConfig("quickEnderChest", true);
        public static final BooleanConfig QUICK_STONECUTTER                     = new BooleanConfig("quickStonecutter", false);

        public static final BooleanConfig DRAW_OPENED_BACKGROUND_COLOR          = new BooleanConfig("drawOpenedBackgroundColor", true);
        public static final ColorConfig   OPENED_BACKGROUND_COLOR               = new ColorConfig("openedBackgroundColor", "#FF28F028");

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
            RIGHT_CLICK_OPEN_IN_HAND,
            KEYBIND_OPEN_IN_HAND,
            KEYBIND_OPEN_IN_INVENTORY,
            RIGHT_CLICK_OPEN_IN_INVENTORY,
            RIGHT_CLICK_CLOSE_IN_INVENTORY,
            SUPPORT_BUNDLING_INSERT,
            SUPPORT_BUNDLING_PICKUP,
            SUPPORT_BUNDLING_EXTRACT,

            QUICK_SHULKER_BOX,
            QUICK_CRAFTING_TABLE,
            QUICK_ENDER_CHEST,
            QUICK_STONECUTTER,

            DRAW_OPENED_BACKGROUND_COLOR,
            OPENED_BACKGROUND_COLOR
        );
    }

    public static class HotKeys {
        public static final HotkeyConfig QUICKSHULKER_KEYBIND = new HotkeyConfig("quickShulkerKeybind", "K", new KeyBindSettings(Context.ANY, KeyAction.PRESS, false, false, CancelCondition.ON_SUCCESS));
        public static final List<HotkeyConfig> HOTKEY_LIST = ImmutableList.of(
            QUICKSHULKER_KEYBIND
        );
    }

    public static final List<ConfigOptionCategory> CATEGORIES = ImmutableList.of(
        BaseConfigOptionCategory.normal(QuickShulkerMod.MOD_INFO, "Generic", MaLiLibConfigs.Generic.OPTIONS)
    );
}
