package net.kyrptonaught.quickshulker.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.kyrptonaught.quickshulker.QuickShulkerMod.MOD_ID;

public class ServerConfig {
    private final Path saveFile;

    public static ServerConfigOptions CONFIG = new ServerConfigOptions();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();

    public ServerConfig(Path saveFile) {
        this.saveFile = saveFile;

        if (!Files.exists(this.saveFile) || !Files.isReadable(this.saveFile)) {
            System.out.printf("%s: Unable to find config file: %s. Saving default config.\n", MOD_ID, this.saveFile);
            this.save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(this.saveFile)) {
            ServerConfigOptions readConfig = gson.fromJson(reader, ServerConfigOptions.class);
            if (readConfig == null) {
                System.out.printf("%s: Failed to load config file: %s! Saving default config\n", MOD_ID, this.saveFile);
                this.save();
            } else {
                CONFIG = readConfig;
            }

        } catch (Exception e) {
            e.printStackTrace();

            System.out.printf("%s: Failed to load config file: %s! Saving default config\n", MOD_ID, this.saveFile);
            this.save();
        }
    }

    public void save() {
        try (OutputStream os = Files.newOutputStream(this.saveFile); OutputStreamWriter out = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            String json = gson.toJson(CONFIG);
            out.write(json);
        } catch (Exception e) {
            System.out.printf("%s: Failed to save config file: %s!\n", MOD_ID, this.saveFile);
            e.printStackTrace();
        }
    }
}
