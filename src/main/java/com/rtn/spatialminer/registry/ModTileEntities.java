package com.rtn.spatialminer.registry;

import com.rtn.spatialminer.SpatialMiner;
import com.rtn.spatialminer.tileentity.BatteryTileEntity;
import com.rtn.spatialminer.tileentity.MinerTileEntity;
import com.rtn.spatialminer.tileentity.CableTileEntity;
import com.rtn.spatialminer.tileentity.ProtectionTileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModTileEntities {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpatialMiner.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatteryTileEntity>> BATTERY_TILE =
            TILE_ENTITIES.register("battery_tile",
                    () -> BlockEntityType.Builder.of(BatteryTileEntity::new, ModBlocks.BATTERY_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MinerTileEntity>> MINER_TILE =
            TILE_ENTITIES.register("miner_tile",
                    () -> BlockEntityType.Builder.of(MinerTileEntity::new, ModBlocks.MINER_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableTileEntity>> CABLE_TILE =
            TILE_ENTITIES.register("cable_tile",
                    () -> BlockEntityType.Builder.of(CableTileEntity::new, ModBlocks.CABLE_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ProtectionTileEntity>> PROTECTION_TILE =
            TILE_ENTITIES.register("protection_tile",
                    () -> BlockEntityType.Builder.of(ProtectionTileEntity::new, ModBlocks.PROTECTION_BLOCK.get()).build(null));
}