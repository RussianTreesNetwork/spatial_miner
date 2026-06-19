package com.rtn.spatialminer.config;

import com.rtn.spatialminer.SpatialMiner;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class ConfigManager {
    public static int batteryCapacity = 1000000;
    public static int minerCapacity = 200000;
    public static int minerEnergyPerTick = 100;
    public static int minerWorkTicks = 10;
    public static int minerInventorySize = 12;
    public static int minerUpgradeSlots = 4;
    public static int transferRate = 4;
    public static List<String> stoneBlockTags = List.of("minecraft:base_stone_overworld", "minecraft:base_stone_nether");
    public static List<String> oreBlockTags = List.of("c:ores", "c:ores_in_ground/stone");

    private static final String CONFIG_FILE_NAME = "spatialminer-config.toml";

    public static void init() {
        File configDir = new File("config");
        if (!configDir.exists()) {
            configDir.mkdir();
        }

        File configFile = new File(configDir, CONFIG_FILE_NAME);

        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        }

        loadConfig(configFile);
    }

    private static void createDefaultConfig(File configFile) {
        try {
            String defaultConfig =
                    "# Spatial Miner Configuration\n" +
                            "# This file will NOT be overwritten on server restart\n" +
                            "\n" +
                            "[energy]\n" +
                            "batteryCapacity = 1000000\n" +
                            "minerCapacity = 200000\n" +
                            "minerEnergyPerTick = 100\n" +
                            "minerWorkTicks = 10\n" +
                            "\n" +
                            "[inventory]\n" +
                            "minerInventorySize = 12\n" +
                            "minerUpgradeSlots = 4\n" +
                            "\n" +
                            "[transfer]\n" +
                            "transferRate = 4\n" +
                            "\n" +
                            "[tags]\n" +
                            "stoneBlocks = \"minecraft:base_stone_overworld,minecraft:base_stone_nether\"\n" +
                            "oreBlocks = \"c:ores,c:ores_in_ground/stone\"\n";

            Files.writeString(configFile.toPath(), defaultConfig);
            SpatialMiner.LOGGER.info("Created default config file");
        } catch (Exception e) {
            SpatialMiner.LOGGER.error("Failed to create default config", e);
        }
    }

    private static void loadConfig(File configFile) {
        try {
            String content = Files.readString(configFile.toPath());

            batteryCapacity = parseIntValue(content, "batteryCapacity", 1000000);
            minerCapacity = parseIntValue(content, "minerCapacity", 200000);
            minerEnergyPerTick = parseIntValue(content, "minerEnergyPerTick", 100);
            minerWorkTicks = parseIntValue(content, "minerWorkTicks", 10);
            minerInventorySize = parseIntValue(content, "minerInventorySize", 12);
            minerUpgradeSlots = parseIntValue(content, "minerUpgradeSlots", 4);
            transferRate = parseIntValue(content, "transferRate", 4);

            String stoneBlocksStr = parseStringValue(content, "stoneBlocks", "minecraft:base_stone_overworld,minecraft:base_stone_nether");
            stoneBlockTags = List.of(stoneBlocksStr.split(","));

            String oreBlocksStr = parseStringValue(content, "oreBlocks", "c:ores,c:ores_in_ground/stone");
            oreBlockTags = List.of(oreBlocksStr.split(","));

            SpatialMiner.LOGGER.info("Loaded config successfully");
        } catch (Exception e) {
            SpatialMiner.LOGGER.error("Failed to load config, using defaults", e);
        }
    }

    private static int parseIntValue(String content, String key, int defaultValue) {
        try {
            String pattern = key + " = ";
            int start = content.indexOf(pattern);
            if (start == -1) return defaultValue;

            start += pattern.length();
            int end = content.indexOf("\n", start);
            if (end == -1) end = content.length();

            String value = content.substring(start, end).trim();
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static String parseStringValue(String content, String key, String defaultValue) {
        try {
            String pattern = key + " = \"";
            int start = content.indexOf(pattern);
            if (start == -1) return defaultValue;

            start += pattern.length();
            int end = content.indexOf("\"", start);
            if (end == -1) return defaultValue;

            return content.substring(start, end).trim();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}