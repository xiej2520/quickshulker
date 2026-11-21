package net.kyrptonaught.quickshulker.config;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.ornithemc.osl.config.api.ConfigScope;
import net.ornithemc.osl.config.api.LoadingPhase;
import net.ornithemc.osl.config.api.config.BaseConfig;
import net.ornithemc.osl.config.api.config.option.BooleanOption;
import net.ornithemc.osl.config.api.serdes.FileSerializerType;
import net.ornithemc.osl.config.api.serdes.SerializerTypes;

// <ai>Server side settings: which items can be quick opened. Saved to config/quickshulker/server.json,
// loaded on game or dedicated server start (before the providers are registered).</ai>
public class ServerConfig extends BaseConfig {
    public static final ServerConfig INSTANCE = new ServerConfig();

    public static final BooleanOption QUICK_SHULKER_BOX        = new BooleanOption("quickShulkerBox", "Allow quick opening Shulker Boxes", true);
    public static final BooleanOption QUICK_CRAFTING_TABLE     = new BooleanOption("quickCraftingTable", "Allow quick opening Crafting Tables", true);
    public static final BooleanOption QUICK_ENDER_CHEST        = new BooleanOption("quickEnderChest", "Allow quick opening Ender Chests", true);
    public static final BooleanOption QUICK_STONECUTTER        = new BooleanOption("quickStonecutter", "Allow quick opening Stonecutters (unimplemented)", true);

    public static final BooleanOption SUPPORT_BUNDLING_INSERT  = new BooleanOption("supportBundlingInsert", "Allow inserting items into shulkers (unimplemented)", true);
    public static final BooleanOption SUPPORT_BUNDLING_PICKUP  = new BooleanOption("supportBundlingPickup", "Allow picking up items with shulkers (unimplemented)", true);
    public static final BooleanOption SUPPORT_BUNDLING_EXTRACT = new BooleanOption("supportBundlingExtract", "Allow extracting items from shulkers (unimplemented)", true);

    @Override
    public String getNamespace() {
        return QuickShulkerMod.MOD_ID;
    }

    @Override
    public String getName() {
        return "server";
    }

    @Override
    public String getSaveName() {
        return "server.json";
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
        return 1;
    }

    @Override
    public void init() {
        registerOptions("generic",
            QUICK_SHULKER_BOX, QUICK_CRAFTING_TABLE, QUICK_ENDER_CHEST, QUICK_STONECUTTER,
            SUPPORT_BUNDLING_INSERT, SUPPORT_BUNDLING_PICKUP, SUPPORT_BUNDLING_EXTRACT);
    }
}
