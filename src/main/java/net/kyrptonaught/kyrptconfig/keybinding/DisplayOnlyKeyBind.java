package net.kyrptonaught.kyrptconfig.keybinding;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;

// key binding info needed for showing the key binding button
public class DisplayOnlyKeyBind extends KeyMapping {
    private CustomKeyBinding customKeyBinding;
    private final Consumer<InputConstants.Key> keySet;

    public DisplayOnlyKeyBind(String translationKey, InputConstants.Type type, int code, String category) {
        super(translationKey, type, code, category);
        keySet = (boundKey) -> {
        };
    }

    public DisplayOnlyKeyBind(String translationKey, String category, CustomKeyBinding customKeyBinding, Consumer<InputConstants.Key> keySet) {
        super(translationKey, customKeyBinding.getDefaultKey().getType(), customKeyBinding.getDefaultKey().getValue(), category);
        this.customKeyBinding = customKeyBinding;
        this.keySet = keySet;
        updateSetKey();
    }

    // setBoundKey in 1.16+
    @Override
    public void setKey(InputConstants.Key boundKey) {
        super.setKey(boundKey);
        if (customKeyBinding != null) {
            // getBoundKeyTranslationKey() in future. lgfw crash with wrong function call??
            customKeyBinding.setRaw(saveString());
        }
        keySet.accept(boundKey);
    }

    public void updateSetKey() {
        super.setKey(customKeyBinding.getKeybinding().orElse(InputConstants.UNKNOWN));
    }

    @Override
    public String getCategory() {
        updateSetKey();
        return super.getCategory();
    }

    @Override
    public String getName() {
        updateSetKey();
        return super.getName();
    }

    @Override
    public InputConstants.Key getDefaultKey() {
        updateSetKey();
        return super.getDefaultKey();
    }
}
