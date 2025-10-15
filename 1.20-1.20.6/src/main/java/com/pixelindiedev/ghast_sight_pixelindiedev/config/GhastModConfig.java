package com.pixelindiedev.ghast_sight_pixelindiedev.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GhastModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "ghast_sight.json";
    private static final Logger LOGGER = LoggerFactory.getLogger("GhastSight");
    public static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), FILE_NAME);
    public HeightEnum HeightDifference = HeightEnum.Medium;
    public transient long lastModified = 0L;

    public static GhastModConfig load() {
        GhastModConfig config = new GhastModConfig();
        JsonObject obj = new JsonObject();
        boolean changed = false;

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonElement element = JsonParser.parseReader(reader);
                if (element.isJsonObject()) obj = element.getAsJsonObject();
            } catch (IOException e) {
                LOGGER.error("Failed to read config, restoring defaults.", e);
                config = new GhastModConfig();
            }
        } else {
            LOGGER.warn("Config file not found, creating a new one.");
            config = new GhastModConfig();
            changed = true;
        }

        // Check for missing options
        if (!obj.has("HeightDifference")) {
            LOGGER.warn("Missing option 'HeightDifference', adding default (Medium).");
            obj.addProperty("HeightDifference", HeightEnum.Medium.name());
            changed = true;
        }

        config = GSON.fromJson(obj, GhastModConfig.class);

        if (changed) {
            config.save();
        }

        config.lastModified = configFile.lastModified();

        return config;
    }

    public void save() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), FILE_NAME);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(this, writer);
            lastModified = configFile.lastModified();
        } catch (IOException e) {
            LOGGER.error("Failed to save config:", e);
        }
    }

    public boolean hasExternalChange() {
        return configFile.exists() && configFile.lastModified() != lastModified;
    }
}
