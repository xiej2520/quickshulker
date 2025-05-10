package net.kyrptonaught.kyrptconfig.keybinding;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonPrimitive;
import net.kyrptonaught.kyrptconfig.config.CustomMarshaller;
import net.kyrptonaught.kyrptconfig.config.CustomSerializable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

// Key binding data and handling for config
public class CustomKeyBinding implements CustomSerializable {
    public boolean unknownIsActivated = false;
    public String rawKey = "";
    public String defaultKey = "";
    public InputUtil.KeyCode parsedKey;
    public boolean doParseKey = true;
    private final String MOD_ID;

    public CustomKeyBinding(String MOD_ID) {
        this.MOD_ID = MOD_ID;
    }

    public CustomKeyBinding(String MOD_ID, boolean unknownIsActivated) {
        this.unknownIsActivated = unknownIsActivated;
        this.MOD_ID = MOD_ID;
    }

    public static CustomKeyBinding configDefault(String MOD_ID, String defaultKey) {
        CustomKeyBinding customKeyBinding = new CustomKeyBinding(MOD_ID).setRaw(defaultKey);
        customKeyBinding.defaultKey = defaultKey;
        return customKeyBinding;
    }

    public CustomKeyBinding setRaw(String key) {
        rawKey = key;
        doParseKey = true;
        holding = false;
        return this;
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

    private void parseKeycode() {
        if (doParseKey) {
            parsedKey = getKeybinding().orElse(null);
            doParseKey = false;
        }
    }

    public boolean isKeybindPressed() {
        parseKeycode();
        if (parsedKey == null) // Invalid key
            return false;
        if (parsedKey == InputUtil.UNKNOWN_KEYCODE)
            return unknownIsActivated; // Always pressed for empty or explicitly "key.keyboard.unknown"
        boolean pressed;
        if (parsedKey.getCategory() == InputUtil.Type.MOUSE) {
            pressed = GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), parsedKey.getKeyCode()) == 1;
        } else {
            pressed = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), parsedKey.getKeyCode()) == 1;
        }
        return pressed;
    }

    public boolean matches(int keyCode, InputUtil.Type type) {
        parseKeycode();
        if (parsedKey == null) return false;
        return parsedKey.getCategory() == type && parsedKey.getKeyCode() == keyCode;
    }

    public Optional<InputUtil.KeyCode> getKeybinding() {
        if (rawKey.isEmpty())
            return Optional.of(InputUtil.UNKNOWN_KEYCODE);
        try {
            return Optional.of(InputUtil.fromName(rawKey));
        } catch (IllegalArgumentException e) {
            System.out.println(MOD_ID + ": unknown key entered");
            return Optional.empty();
        }
    }

    public InputUtil.KeyCode getDefaultKey() {
        if (defaultKey == null || defaultKey.isEmpty())
            return InputUtil.UNKNOWN_KEYCODE;
        try {
            return InputUtil.fromName(defaultKey);
        } catch (IllegalArgumentException e) {
            System.out.println(MOD_ID + ": unknown default key entered");
            return InputUtil.UNKNOWN_KEYCODE;
        }
    }

    @Override
    public JsonElement toJson(CustomMarshaller m) {
        return new JsonPrimitive(this.rawKey);
    }

    @Override
    public CustomSerializable fromJson(CustomMarshaller m, JsonElement obj, Class<CustomSerializable> clazz) {
        if (obj instanceof JsonPrimitive string)
            setRaw(string.asString());
        return this;
    }
}
