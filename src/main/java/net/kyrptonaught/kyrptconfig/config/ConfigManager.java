package net.kyrptonaught.kyrptconfig.config;

import blue.endless.jankson.Jankson;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class ConfigManager {
    protected final Jankson JANKSON = Jankson.builder().build();
    protected final HashMap<String, ConfigStorage> configs = new HashMap<>();
    protected File dir;
    protected String MOD_ID;

    private ConfigManager(String mod_id) {
        this.MOD_ID = mod_id;
        dir = FabricLoader.getInstance().getConfigDirectory();
    }

    public AbstractConfigFile getConfig(String name) {
        if (!name.endsWith(".json5")) name = name + ".json5";
        return configs.get(name).config;
    }

    public void registerFile(String name, AbstractConfigFile defaultConfig) {
        if (!name.endsWith(".json5")) name = name + ".json5";
        configs.put(name, new ConfigStorage(new File(dir, name), defaultConfig));
    }

    public void save() {
        configs.values().forEach(configStorage -> configStorage.save(MOD_ID, JANKSON));
    }

    public void load() {
        configs.values().forEach(configStorage -> configStorage.load(MOD_ID, JANKSON));
        save();
    }

    public Jankson getJANKSON() {
        return JANKSON;
    }

    public static class SingleConfigManager extends ConfigManager {
        public SingleConfigManager(String mod_id, AbstractConfigFile defaultConfig) {
            super(mod_id);
            dir = FabricLoader.getInstance().getConfigDirectory();
            registerFile(mod_id + "config", defaultConfig);
        }

        public AbstractConfigFile getConfig() {
            return getConfig(MOD_ID + "config");
        }
    }

    public static class MultiConfigManager extends ConfigManager {
        public MultiConfigManager(String mod_id) {
            super(mod_id);
            dir = new File(FabricLoader.getInstance().getConfigDirectory() + "/" + MOD_ID);
            if (!Files.exists(dir.toPath())) {
                try {
                    Files.createDirectories(dir.toPath());
                } catch (IOException e) {
                }
            }
        }

        public void load(String config) {
            if (!config.endsWith(".json5")) config = config + ".json5";
            this.configs.get(config).load(MOD_ID, JANKSON);
            save(config);
        }

        public void save(String config) {
            if (!config.endsWith(".json5")) config = config + ".json5";
            this.configs.get(config).save(MOD_ID, JANKSON);
        }
    }
}
