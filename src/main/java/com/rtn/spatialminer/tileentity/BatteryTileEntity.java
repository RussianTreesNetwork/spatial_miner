package com.rtn.spatialminer.tileentity;

import com.rtn.spatialminer.Config;
import com.rtn.spatialminer.SpatialMiner;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.energy.EnergyStorage;

public class BatteryTileEntity extends BlockEntity implements IEnergyStorage {
    private final EnergyStorage storage = new EnergyStorage(Config.BATTERY_CAPACITY);

    public BatteryTileEntity(BlockPos pos, BlockState state) {
        super(SpatialMiner.BATTERY_TILE.get(), pos, state);
    }

    public void tick() {
        // Ничего не делаем, просто храним энергию
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return storage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return storage.getEnergyStored() > 0;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}