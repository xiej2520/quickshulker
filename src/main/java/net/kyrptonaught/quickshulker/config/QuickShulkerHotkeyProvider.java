package net.kyrptonaught.quickshulker.config;

import com.google.common.collect.ImmutableList;
import malilib.input.Hotkey;
import malilib.input.HotkeyCategory;
import malilib.input.HotkeyProvider;
import net.kyrptonaught.quickshulker.QuickShulkerMod;

import java.util.List;

public class QuickShulkerHotkeyProvider implements HotkeyProvider {
    public static final QuickShulkerHotkeyProvider INSTANCE = new QuickShulkerHotkeyProvider();

    @Override
    public List<? extends Hotkey> getAllHotkeys() {
        ImmutableList.Builder<Hotkey> builder = ImmutableList.builder();

        builder.addAll(ClientConfigs.HotKeys.HOTKEY_LIST);

        return builder.build();
    }

    @Override
    public List<HotkeyCategory> getHotkeysByCategories() {
        return ImmutableList.of(
            new HotkeyCategory(QuickShulkerMod.MOD_INFO, "quickshulker.hotkeys", ClientConfigs.HotKeys.HOTKEY_LIST)
        );
    }
}
