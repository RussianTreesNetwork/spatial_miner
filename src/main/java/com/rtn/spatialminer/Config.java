package com.rtn.spatialminer;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class Config {
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_ENERGY = "energy";

    // Значения по умолчанию
    public static int BATTERY_CAPACITY = 1000000;
    public static int MINER_CAPACITY = 200000;
    public static int MINER_COST_PER_BLOCK = 10000;
    public static int MINER_EXPORT_SPEED = 4;

    public static void loadConfig() {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("spatialminer.toml");
        CommentedFileConfig config = CommentedFileConfig.builder(configPath).autosave().writingMode(WritingMode.REPLACE).build();

        // Если файла нет, он создастся. Если есть, прочитает существующий.
        config.load();

        // Читаем значения из файла или ставим дефолтные
        BATTERY_CAPACITY = config.getOrElse(CATEGORY_ENERGY + ".battery_capacity", 1000000);
        MINER_CAPACITY = config.getOrElse(CATEGORY_ENERGY + ".miner_capacity", 200000);
        MINER_COST_PER_BLOCK = config.getOrElse(CATEGORY_GENERAL + ".miner_cost_per_block", 10000);
        MINER_EXPORT_SPEED = config.getOrElse(CATEGORY_GENERAL + ".miner_export_speed", 4);

        config.save(); // Сохраняем, если нужно
    }
}