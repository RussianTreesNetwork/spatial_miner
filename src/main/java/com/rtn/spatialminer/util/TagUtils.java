package com.rtn.spatialminer.util;

import com.rtn.spatialminer.config.ConfigManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class TagUtils {

    public static List<BlockState> getMineableBlocks() {
        List<BlockState> blocks = new ArrayList<>();

        // Добавляем блоки из настроенных тегов
        for (String tagName : ConfigManager.stoneBlockTags) {
            addBlocksFromTag(blocks, tagName);
        }

        for (String tagName : ConfigManager.oreBlockTags) {
            addBlocksFromTag(blocks, tagName);
        }

        return blocks;
    }

    private static void addBlocksFromTag(List<BlockState> blocks, String tagName) {
        try {
            ResourceLocation location = ResourceLocation.parse(tagName);
            TagKey<Block> tag = TagKey.create(BuiltInRegistries.BLOCK.key(), location);

            for (Block block : BuiltInRegistries.BLOCK) {
                if (block.defaultBlockState().is(tag)) {
                    blocks.add(block.defaultBlockState());
                }
            }
        } catch (Exception e) {
            // Игнорируем неверные теги
        }
    }
}