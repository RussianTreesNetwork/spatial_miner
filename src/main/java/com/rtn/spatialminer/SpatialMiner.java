package com.rtn.spatialminer;

import com.mojang.logging.LogUtils;
import com.rtn.spatialminer.block.mechanic.BatteryBlock;
import com.rtn.spatialminer.block.mechanic.CableBlock;
import com.rtn.spatialminer.block.mechanic.MinerBlock;
import com.rtn.spatialminer.block.mechanic.PhotolithographBlock;
import com.rtn.spatialminer.block.mechanic.VacuumExposureChamberMK1;
import com.rtn.spatialminer.block.mechanic.VacuumExposureChamberMK2;
import com.rtn.spatialminer.block.ores.ChroniteEndStoneOre;
import com.rtn.spatialminer.block.ores.KepleriteDeepslateOre;
import com.rtn.spatialminer.item.ChroniteIngot;
import com.rtn.spatialminer.item.KepleriteShard;
import com.rtn.spatialminer.tileentity.BatteryTileEntity;
import com.rtn.spatialminer.tileentity.CableTileEntity;
import com.rtn.spatialminer.tileentity.MinerTileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(SpatialMiner.MODID)
public class SpatialMiner {
    public static final String MODID = "spatialminer";
    private static final Logger LOGGER = LogUtils.getLogger();

    // ---- РЕГИСТРАЦИЯ БЛОКОВ ----
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    // 1. БАЗОВЫЕ МЕХАНИЗМЫ (Кабель, Батарея, Шахтёр)
    public static final DeferredBlock<Block> CABLE_BLOCK = BLOCKS.register("cable_block",
            () -> new CableBlock(BlockBehaviour.Properties.of().strength(1.5f).noOcclusion()));

    public static final DeferredBlock<Block> BATTERY_BLOCK = BLOCKS.register("battery_block",
            () -> new BatteryBlock(BlockBehaviour.Properties.of().strength(3.0f).mapColor(MapColor.COLOR_PURPLE)));

    public static final DeferredBlock<Block> MINER_BLOCK = BLOCKS.register("miner_block",
            () -> new MinerBlock(BlockBehaviour.Properties.of().strength(3.0f).mapColor(MapColor.COLOR_GRAY).noOcclusion()));

    // 2. НОВЫЕ МЕХАНИЗМЫ (КВЭ и ЛИТОГРАФ)
    public static final DeferredBlock<Block> VACUUM_EXPOSURE_CHAMBER_MK1 = BLOCKS.register("vacuum_exposure_chamber_mk1",
            () -> new VacuumExposureChamberMK1(BlockBehaviour.Properties.of().strength(4.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> VACUUM_EXPOSURE_CHAMBER_MK2 = BLOCKS.register("vacuum_exposure_chamber_mk2",
            () -> new VacuumExposureChamberMK2(BlockBehaviour.Properties.of().strength(6.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> PHOTOLITHOGRAPH = BLOCKS.register("photolithograph",
            () -> new PhotolithographBlock(BlockBehaviour.Properties.of().strength(4.0f).requiresCorrectToolForDrops()));

    // 3. НОВЫЕ РУДЫ
    public static final DeferredBlock<Block> KEPLERITE_DEEPSLATE_ORE = BLOCKS.register("keplerite_deepslate_ore",
            () -> new KepleriteDeepslateOre(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(4.5f, 3.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> CHRONITE_END_STONE_ORE = BLOCKS.register("chronite_end_stone_ore",
            () -> new ChroniteEndStoneOre(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(6.0f, 3.0f).requiresCorrectToolForDrops()));


    // ---- РЕГИСТРАЦИЯ ПРЕДМЕТОВ ----
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    // 1. ПРЕДМЕТЫ БЛОКОВ (для инвентаря)
    public static final DeferredItem<BlockItem> CABLE_ITEM = ITEMS.registerSimpleBlockItem("cable_block", CABLE_BLOCK);
    public static final DeferredItem<BlockItem> BATTERY_ITEM = ITEMS.registerSimpleBlockItem("battery_block", BATTERY_BLOCK);
    public static final DeferredItem<BlockItem> MINER_ITEM = ITEMS.registerSimpleBlockItem("miner_block", MINER_BLOCK);

    public static final DeferredItem<BlockItem> VACUUM_EXPOSURE_CHAMBER_MK1_ITEM = ITEMS.registerSimpleBlockItem("vacuum_exposure_chamber_mk1", VACUUM_EXPOSURE_CHAMBER_MK1);
    public static final DeferredItem<BlockItem> VACUUM_EXPOSURE_CHAMBER_MK2_ITEM = ITEMS.registerSimpleBlockItem("vacuum_exposure_chamber_mk2", VACUUM_EXPOSURE_CHAMBER_MK2);
    public static final DeferredItem<BlockItem> PHOTOLITHOGRAPH_ITEM = ITEMS.registerSimpleBlockItem("photolithograph", PHOTOLITHOGRAPH);

    public static final DeferredItem<BlockItem> KEPLERITE_ORE_ITEM = ITEMS.registerSimpleBlockItem("keplerite_deepslate_ore", KEPLERITE_DEEPSLATE_ORE);
    public static final DeferredItem<BlockItem> CHRONITE_ORE_ITEM = ITEMS.registerSimpleBlockItem("chronite_end_stone_ore", CHRONITE_END_STONE_ORE);

    // 2. ЧИСТЫЕ ПРЕДМЕТЫ (Осколок и Слиток)
    public static final DeferredItem<Item> KEPLERITE_SHARD = ITEMS.register("keplerite_shard",
            () -> new KepleriteShard(new Item.Properties()));

    public static final DeferredItem<Item> CHRONITE_INGOT = ITEMS.register("chronite_ingot",
            () -> new ChroniteIngot(new Item.Properties()));


    // ---- РЕГИСТРАЦИЯ ТИЛОВ (Tile Entities) ----
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableTileEntity>> CABLE_TILE = TILE_ENTITIES.register("cable_tile",
            () -> BlockEntityType.Builder.of(CableTileEntity::new, CABLE_BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatteryTileEntity>> BATTERY_TILE = TILE_ENTITIES.register("battery_tile",
            () -> BlockEntityType.Builder.of(BatteryTileEntity::new, BATTERY_BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MinerTileEntity>> MINER_TILE = TILE_ENTITIES.register("miner_tile",
            () -> BlockEntityType.Builder.of(MinerTileEntity::new, MINER_BLOCK.get()).build(null));


    // ---- КРЕАТИВНАЯ ВКЛАДКА (Creative Tab) ----
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPAWN_TAB = CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(net.minecraft.network.chat.Component.literal("Spatial Miner"))
            .icon(() -> new ItemStack(MINER_ITEM.get()))
            .displayItems((params, output) -> {
                output.accept(CABLE_ITEM.get());
                output.accept(BATTERY_ITEM.get());
                output.accept(MINER_ITEM.get());

                output.accept(VACUUM_EXPOSURE_CHAMBER_MK1_ITEM.get());
                output.accept(VACUUM_EXPOSURE_CHAMBER_MK2_ITEM.get());
                output.accept(PHOTOLITHOGRAPH_ITEM.get());

                output.accept(KEPLERITE_ORE_ITEM.get());
                output.accept(CHRONITE_ORE_ITEM.get());
                output.accept(KEPLERITE_SHARD.get());
                output.accept(CHRONITE_INGOT.get());
            }).build());


    // ---- КОНСТРУКТОР (Constructor) ----
    public SpatialMiner(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerCapabilities);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        Config.loadConfig();
        LOGGER.info("Spatial Miner Common Setup completed.");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Spatial Miner Client Setup completed.");
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Регистрируем энергетические интерфейсы для наших TileEntities
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, CABLE_TILE.get(), (be, side) -> be);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BATTERY_TILE.get(), (be, side) -> be);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, MINER_TILE.get(), (be, side) -> be);
    }
}