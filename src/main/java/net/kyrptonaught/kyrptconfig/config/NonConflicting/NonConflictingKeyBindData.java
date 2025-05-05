package net.kyrptonaught.kyrptconfig.config.NonConflicting;

import net.minecraft.client.util.InputUtil;

import java.util.function.Consumer;

public class NonConflictingKeyBindData {
    public String name, category;
   public InputUtil.Type inputType;
    public int keyCode;
    public Consumer<InputUtil.Key> keySetEvent;
    public NonConflictingKeyBindData(String Name, String Category, InputUtil.Type type, int KeyCode, Consumer<InputUtil.Key> keySetEvent){
        this.name = Name;
        this.category = Category;
        this.inputType = type;
        this.keyCode = KeyCode;
        this.keySetEvent = keySetEvent;
    }
}
