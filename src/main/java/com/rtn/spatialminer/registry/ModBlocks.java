package com.rtn.spatialminer.registry;

import com.rtn.spatialminer.SpatialMiner;
import com.rtn.spatialminer.block.BatteryBlock;
import com.rtn.spatialminer.block.MinerBlock;
import com.rtn.spatialminer.block.CableBlock;
import com.rtn.spatialminer.block.ProtectionBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlocks {
    // Регистр блоков
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpatialMiner.MOD_ID);

    // Регистр предметов
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, SpatialMiner.MOD_ID);

    // === БЛОКИ ===

    public static final DeferredHolder<Block, Block> BATTERY_BLOCK = BLOCKS.register("battery_block",
            () -> new BatteryBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredHolder<Block, Block> MINER_BLOCK = BLOCKS.register("miner_block",
            () -> new MinerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredHolder<Block, Block> CABLE_BLOCK = BLOCKS.register("cable_block",
            () -> new CableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredHolder<Block, Block> PROTECTION_BLOCK = BLOCKS.register("protection_block",
            () -> new ProtectionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    // === ПРЕДМЕТЫ ДЛЯ БЛОКОВ (регистрируются сразу!) ===

    public static final DeferredHolder<Item, Item> BATTERY_ITEM = ITEMS.register("battery_block",
            () -> new BlockItem(BATTERY_BLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> MINER_ITEM = ITEMS.register("miner_block",
            () -> new BlockItem(MINER_BLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> CABLE_ITEM = ITEMS.register("cable_block",
            () -> new BlockItem(CABLE_BLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> PROTECTION_ITEM = ITEMS.register("protection_block",
            () -> new BlockItem(PROTECTION_BLOCK.get(), new Item.Properties()));
}