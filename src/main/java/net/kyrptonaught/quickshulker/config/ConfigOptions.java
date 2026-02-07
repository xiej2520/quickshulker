package net.kyrptonaught.quickshulker.config;

//import net.kyrptonaught.kyrptconfig.config.AbstractConfigFile;

//public class ConfigOptions implements AbstractConfigFile {
public class ConfigOptions {
    public static String defaultKeybind = "key.keyboard.k";
    // Right-Clicking with shulker in hand opens it
    public boolean rightClickToOpen = true;
    // Hitting the keybind with shulker in hand opens it
    public boolean keybind = true;
    // Hitting the keybind while hovering over shulker in inv opens it
    public boolean keybindInInv = true;
    // Right Clicking a shulker in your inv opens it
    public boolean rightClickInv = true;

    // Right Clicking the opened shulker in your inv closes it
    public boolean rightClickClose = true;
    // Right Clicking a shulker with an item inserts it
    public boolean supportsBundlingInsert = true;
    // Right Clicking an item with a shulker inserts it
    public boolean supportsBundlingPickup = true;
    // Right Clicking an empty slot with a shulker extracts an item
    public boolean supportsBundlingExtract = true;

    // Enable opening Shulker Boxes
    public boolean quickShulkerBox = true;
    // Enable opening Crafting Tables
    public boolean quickCraftingTables = true;
    // Enable opening Stonecutter
    public boolean quickStonecutter = true;
    // Enable opening EnderChest
    public boolean quickEnderChest = true;

    // Enable filling the background of opened items with a color
    public boolean fillOpenedBackground = true;
    // The color to use as a background of opened items
    public Integer colorBackground = (40 << 16) + (240 << 8) + (40 << 0);
}
