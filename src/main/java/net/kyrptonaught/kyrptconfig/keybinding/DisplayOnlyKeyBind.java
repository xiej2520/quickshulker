package net.kyrptonaught.kyrptconfig.keybinding;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.function.Consumer;

public class DisplayOnlyKeyBind extends KeyBinding {
    private CustomKeyBinding customKeyBinding;
    private final Consumer<InputUtil.KeyCode> keySet;

    public DisplayOnlyKeyBind(String translationKey, InputUtil.Type type, int code, String category) {
        super(translationKey, type, code, category);
        keySet = (boundKey) -> {
        };
    }

    public DisplayOnlyKeyBind(String translationKey, String category, CustomKeyBinding customKeyBinding, Consumer<InputUtil.KeyCode> keySet) {
        super(translationKey, customKeyBinding.getDefaultKey().getCategory(), customKeyBinding.getDefaultKey().getKeyCode(), category);
        this.customKeyBinding = customKeyBinding;
        this.keySet = keySet;
        updateSetKey();
    }

    public void setBoundKey(InputUtil.KeyCode boundKey) {
        super.setKeyCode(boundKey);
        if (customKeyBinding != null)
            customKeyBinding.setRaw(getLocalizedName());
        keySet.accept(boundKey);
    }

    public void updateSetKey() {
        super.setKeyCode(customKeyBinding.getKeybinding().orElse(InputUtil.UNKNOWN_KEYCODE));
    }

    @Override
    public String getCategory() {
        updateSetKey();
        return super.getCategory();
    }

    @Override
    public String getId() {
        updateSetKey();
        return super.getName();
    }

    @Override
    public InputUtil.KeyCode getDefaultKeyCode() {
        updateSetKey();
        return super.getDefaultKeyCode();
    }
}
