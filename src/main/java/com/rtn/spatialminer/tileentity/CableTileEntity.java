package com.rtn.spatialminer.tileentity;

import com.rtn.spatialminer.registry.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class CableTileEntity extends BlockEntity implements IEnergyStorage {
    // Делаем MAX_TRANSFER статической константой, чтобы использовать в static tick()
    private static final int MAX_TRANSFER = 1000;

    private int buffer = 0;

    public CableTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.CABLE_TILE.get(), pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.buffer = tag.getInt("buffer");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("buffer", this.buffer);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CableTileEntity cable) {
        if (level.isClientSide()) return;

        // 1. Собираем энергию со всех сторон, где есть источники
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);

            // Ищем источник (может отдавать энергию)
            IEnergyStorage source = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, neighborState, null, dir.getOpposite());

            if (source != null && source.canExtract()) {
                // Сначала симулируем извлечение, чтобы узнать сколько реально можно взять
                int extracted = source.extractEnergy(MAX_TRANSFER - cable.buffer, true);
                if (extracted > 0) {
                    // Реальное извлечение
                    int actuallyExtracted = source.extractEnergy(extracted, false);
                    cable.buffer += actuallyExtracted;
                }
            }
        }

        // 2. Распределяем энергию по всем сторонам, где есть потребители
        if (cable.buffer > 0) {
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = pos.relative(dir);
                BlockState neighborState = level.getBlockState(neighborPos);

                IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, neighborState, null, dir.getOpposite());

                if (target != null && target.canReceive()) {
                    int received = target.receiveEnergy(cable.buffer, false);
                    cable.buffer -= received;
                    if (cable.buffer <= 0) break;
                }
            }
        }

        // Ограничиваем буфер, чтобы кабель не превращался в бесконечное хранилище
        if (cable.buffer > MAX_TRANSFER) cable.buffer = MAX_TRANSFER;

        cable.setChanged();
    }

    // --- Реализация интерфейса для внешних подключений ---

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) return 0;
        int toAdd = Math.min(MAX_TRANSFER - buffer, maxReceive);
        if (!simulate) buffer += toAdd;
        return toAdd;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract <= 0) return 0;
        int toRemove = Math.min(buffer, maxExtract);
        if (!simulate) buffer -= toRemove;
        return toRemove;
    }

    @Override
    public int getEnergyStored() { return buffer; }

    @Override
    public int getMaxEnergyStored() { return MAX_TRANSFER; }

    @Override
    public boolean canExtract() { return true; }

    @Override
    public boolean canReceive() { return true; }
}