package net.kyrptonaught.kyrptconfig.config.NonConflicting;

import net.minecraft.client.util.InputUtil;

import java.util.function.Consumer;

public class NonConflictingKeyBindData {
    public String name, category;
    public InputUtil.Type inputType;
    public int keyCode;
    public Consumer<InputUtil.KeyCode> keySetEvent;
    public InputUtil.KeyCode defaultKeyCode;

    public NonConflictingKeyBindData(String Name, String Category, InputUtil.Type type, int KeyCode, Consumer<InputUtil.KeyCode> keySetEvent) {
        this.name = Name;
        this.category = Category;
        this.inputType = type;
        this.keyCode = KeyCode;
        this.keySetEvent = keySetEvent;
    }

    public NonConflictingKeyBindData(String Name, String Category, InputUtil.KeyCode boundKey, InputUtil.KeyCode defaultKey, Consumer<InputUtil.KeyCode> keySetEvent) {
        this(Name, Category, boundKey.getCategory(), boundKey.getKeyCode(), keySetEvent);
        this.defaultKeyCode = defaultKey;
    }
}
