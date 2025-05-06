package net.kyrptonaught.quickshulker.client;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
    public String rawKey;
    // InputUtil.Key in 1.16+
    public InputUtil.KeyCode keycode;
    public boolean doParseKeycode = true;

    public KeyBinding() {
    }

    public void setRaw(String key) {
        rawKey = key;
        doParseKeycode = true;
        holding = false;
    }

    boolean holding = false;

    public boolean wasPressed() {
        boolean pressed = isKeybindPressed();
        if (!holding) {
            holding = pressed;
            return pressed;
        }
        if (!pressed)
            holding = false;
        return false;
    }

    public void parseKeycode() {
        if (doParseKeycode) {
            keycode = getKeybinding();
            doParseKeycode = false;
        }
    }

    public boolean isKeybindPressed() {
        parseKeycode();
        if (keycode == null) // Invalid key
            return false;
        if (keycode == InputUtil.UNKNOWN_KEYCODE)
            return false;

        if (keycode.getCategory() == InputUtil.Type.MOUSE)
            return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), keycode.getKeyCode()) == 1;
        else
            return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), keycode.getKeyCode()) == 1;

    }

    public boolean doesMatch(InputUtil.Type type, int code) {
        parseKeycode();
        if (keycode == null) // Invalid key
            return false;
        if (keycode == InputUtil.UNKNOWN_KEYCODE)
            return false;
        return keycode.getCategory() == type && keycode.getKeyCode() == code;
    }

    public InputUtil.KeyCode getKeybinding() {
        if (rawKey == null || rawKey.isEmpty())
            return InputUtil.UNKNOWN_KEYCODE;
        try {
            // InputUtil.fromTranslationKey in 1.16+
            return InputUtil.fromName(rawKey);
        } catch (IllegalArgumentException e) {
            System.out.println(QuickShulkerMod.MOD_ID + ": unknown key entered");
            return InputUtil.UNKNOWN_KEYCODE;
        }
    }
}