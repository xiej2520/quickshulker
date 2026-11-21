package net.kyrptonaught.quickshulker.config;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.ornithemc.osl.config.api.ConfigScope;
import net.ornithemc.osl.config.api.LoadingPhase;
import net.ornithemc.osl.config.api.config.BaseConfig;
import net.ornithemc.osl.config.api.config.option.BooleanOption;
import net.ornithemc.osl.config.api.config.option.Option;
import net.ornithemc.osl.config.api.config.option.StringOption;
import net.ornithemc.osl.config.api.serdes.FileSerializerType;
import net.ornithemc.osl.config.api.serdes.SerializerTypes;
import net.ornithemc.osl.keybinds.api.KeybindRegistry;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class ClientConfigs extends BaseConfig {
    public static final int CURRENT_VERSION = 1;
    public static final ClientConfigs INSTANCE = new ClientConfigs();

    public static class Options {
        public static final BooleanOption RIGHT_CLICK_OPEN_IN_HAND              = toggle("rightClickOpenInHand", true);
        public static final BooleanOption KEYBIND_OPEN_IN_HAND                  = toggle("keybindOpenInHand", true);
        public static final BooleanOption KEYBIND_OPEN_IN_INVENTORY             = toggle("keybindOpenInInventory", true);
        public static final BooleanOption RIGHT_CLICK_OPEN_IN_INVENTORY         = toggle("rightClickOpenInInventory", true);
        public static final BooleanOption RIGHT_CLICK_CLOSE_IN_INVENTORY        = toggle("rightClickCloseInInventory", true);
        //public static final BooleanOption SUPPORT_BUNDLING_INSERT = toggle("supportBundlingInsert", false);
        //public static final BooleanOption SUPPORT_BUNDLING_PICKUP = toggle("supportBundlingPickup", false);
        //public static final BooleanOption SUPPORT_BUNDLING_EXTRACT = toggle("supportBundlingExtract", false);

        public static final BooleanOption QUICK_SHULKER_BOX                     = toggle("quickShulkerBox", true);
        public static final BooleanOption QUICK_CRAFTING_TABLE                  = toggle("quickCraftingTable", true);
        public static final BooleanOption QUICK_ENDER_CHEST                     = toggle("quickEnderChest", true);
        //public static final BooleanOption QUICK_STONECUTTER = toggle("quickStonecutter", false);

        public static final BooleanOption DRAW_OPENED_BACKGROUND_COLOR          = toggle("drawOpenedBackgroundColor", true);
        public static final StringOption  OPENED_BACKGROUND_COLOR               = new StringOption("openedBackgroundColor", comment("openedBackgroundColor"), "#FF28F028", ClientConfigs::isValidColor);

        public static final ImmutableList<BooleanOption> TOGGLES = ImmutableList.of(
            RIGHT_CLICK_OPEN_IN_HAND,
            KEYBIND_OPEN_IN_HAND,
            KEYBIND_OPEN_IN_INVENTORY,
            RIGHT_CLICK_OPEN_IN_INVENTORY,
            RIGHT_CLICK_CLOSE_IN_INVENTORY,
            //SUPPORT_BUNDLING_INSERT,
            //SUPPORT_BUNDLING_PICKUP,
            //SUPPORT_BUNDLING_EXTRACT,

            QUICK_SHULKER_BOX,
            QUICK_CRAFTING_TABLE,
            QUICK_ENDER_CHEST,
            //QUICK_STONECUTTER,

            DRAW_OPENED_BACKGROUND_COLOR
        );

        private static BooleanOption toggle(String name, boolean defaultValue) {
            return new BooleanOption(name, comment(name), defaultValue);
        }

        private static String comment(String name) {
            return "quickshulker.config.comment." + name.toLowerCase();
        }
    }

    public static class HotKeys {
        private static KeyBinding QUICKSHULKER_KEYBIND;

        public static void register() {
            QUICKSHULKER_KEYBIND = KeybindRegistry.register("quickshulker.hotkey.name.quickshulkerkeybind", Keyboard.KEY_K, "quickshulker.keybind.category");
        }

        public static int getQuickShulkerKeyCode() {
            return QUICKSHULKER_KEYBIND.getKeyCode();
        }

        // vanilla only updates KeyBinding state outside of screens, so poll the key directly
        public static boolean isQuickShulkerKeyHeld() {
            return GameOptions.isPressed(QUICKSHULKER_KEYBIND);
        }
    }

    private static final Pattern COLOR = Pattern.compile("#([0-9a-fA-F]{6}|[0-9a-fA-F]{8})");

    public static boolean isValidColor(String color) {
        return color != null && COLOR.matcher(color).matches();
    }

    // #RRGGBB or #AARRGGBB to ARGB
    public static int parseColor(String color) {
        int argb = (int) Long.parseLong(color.substring(1), 16);
        return color.length() == 7 ? argb | 0xFF000000 : argb;
    }

    public static int getOpenedBackgroundColor() {
        return parseColor(Options.OPENED_BACKGROUND_COLOR.get());
    }

    public static List<Option> getAllOptions() {
        return ImmutableList.<Option>builder().addAll(Options.TOGGLES).add(Options.OPENED_BACKGROUND_COLOR).build();
    }

    @Override
    public String getNamespace() {
        return QuickShulkerMod.MOD_ID;
    }

    @Override
    public String getName() {
        return "client";
    }

    @Override
    public String getSaveName() {
        return "client.json";
    }

    @Override
    public ConfigScope getScope() {
        return ConfigScope.GLOBAL;
    }

    @Override
    public LoadingPhase getLoadingPhase() {
        return LoadingPhase.START;
    }

    @Override
    public FileSerializerType<?> getType() {
        return SerializerTypes.JSON;
    }

    @Override
    public int getVersion() {
        return CURRENT_VERSION;
    }

    @Override
    public void init() {
        registerOptions("generic", getAllOptions().toArray(new Option[0]));
    }
}
