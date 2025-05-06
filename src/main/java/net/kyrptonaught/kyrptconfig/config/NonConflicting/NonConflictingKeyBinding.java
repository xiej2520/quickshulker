package net.kyrptonaught.kyrptconfig.config.NonConflicting;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.function.Consumer;

public class NonConflictingKeyBinding extends KeyBinding {
    private final Consumer<InputUtil.KeyCode> keySet;

    public NonConflictingKeyBinding(String name, InputUtil.Type type, int code, String category, Consumer<InputUtil.KeyCode> keySet) {
        super(name, type, code, category);
        this.keySet = keySet;
    }

    public void setBoundKey(InputUtil.KeyCode boundKey) {
        super.setKeyCode(boundKey);
        keySet.accept(boundKey);
    }
}