package com.rtn.spatialminer;

import com.rtn.spatialminer.config.ConfigManager;
import com.rtn.spatialminer.registry.ModBlocks;
import com.rtn.spatialminer.registry.ModTileEntities;
import com.rtn.spatialminer.registry.ModCreativeTabs;
import com.rtn.spatialminer.network.NetworkManager;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SpatialMiner.MOD_ID)
public class SpatialMiner {
    public static final String MOD_ID = "spatialminer";
    public static final Logger LOGGER = LogManager.getLogger();

    public SpatialMiner(IEventBus modEventBus) {
        // Регистрируем ВСЕ регистры сразу
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModTileEntities.TILE_ENTITIES.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);

        // Слушатели событий инициализации
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::registerCapabilities);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        // Инициализация конфига (без регистрации предметов!)
        ConfigManager.init();
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Энергия для батареи
        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                ModTileEntities.BATTERY_TILE.get(),
                (battery, context) -> battery
        );

        // Энергия для шахтёра
        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                ModTileEntities.MINER_TILE.get(),
                (miner, context) -> miner
        );

        // Энергия для кабеля
        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                ModTileEntities.CABLE_TILE.get(),
                (cable, context) -> cable
        );

        // Инвентарь для шахтёра
        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                ModTileEntities.MINER_TILE.get(),
                (miner, context) -> miner.getInventory()
        );
    }

    @EventBusSubscriber
    public static class ServerEvents {
        @SubscribeEvent
        public static void onServerTick(ServerTickEvent.Post event) {
            for (Level level : event.getServer().getAllLevels()) {
                NetworkManager.updateAllNetworks(level);
            }
        }
    }
}