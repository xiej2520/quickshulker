package net.kyrptonaught.kyrptconfig.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class ConfigStorage {
    private File saveFile;
    public AbstractConfigFile config;
    private AbstractConfigFile defaultConfig;

    public ConfigStorage(File fileName, AbstractConfigFile defaultConfig) {
        this.saveFile = fileName;
        this.defaultConfig = defaultConfig;
    }

    public void save(String MOD_ID, Jankson JANKSON) {
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(saveFile, false), StandardCharsets.UTF_8)) {
            if (!saveFile.exists())
                saveFile.createNewFile();
            String json = JANKSON.toJson(config).toJson(true, true, 0);
            out.write(json);
            // out.write(json.getBytes());

        } catch (Exception e) {
            System.out.println(MOD_ID + " Failed to save " + saveFile.getName());
        }
    }

    public AbstractConfigFile load(String MOD_ID, Jankson JANKSON) {
        if (!saveFile.exists() || !saveFile.canRead()) {
            System.out.println(MOD_ID + " Config not found! Creating one.");
            config = defaultConfig;
        }
        boolean failed = false;
        try {
            JsonObject configJson = JANKSON.load(saveFile);
            String regularized = configJson.toJson(false, false, 0);
            config = JANKSON.fromJson(regularized, defaultConfig.getClass());
        } catch (Exception e) {
            failed = true;
        }
        if (failed || (config == null)) {
            System.out.println(MOD_ID + " Failed to load config! Overwriting with default config.");
            config = defaultConfig;
        }
        return config;
    }
}
