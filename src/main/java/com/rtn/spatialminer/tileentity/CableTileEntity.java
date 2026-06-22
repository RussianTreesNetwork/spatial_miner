package com.rtn.spatialminer.tileentity;

import com.rtn.spatialminer.Config;
import com.rtn.spatialminer.SpatialMiner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.energy.EnergyStorage;

public class CableTileEntity extends BlockEntity implements IEnergyStorage {
    private final EnergyStorage storage = new EnergyStorage(2000); // Небольшой буфер

    public CableTileEntity(BlockPos pos, BlockState state) {
        super(SpatialMiner.CABLE_TILE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;
        if (storage.getEnergyStored() <= 0) return;

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor != null && neighbor != this) {
                IEnergyStorage target = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK, neighborPos, neighbor.getBlockState(), neighbor, dir.getOpposite());
                if (target != null && target.canReceive()) {
                    int extracted = storage.extractEnergy(100, true);
                    int accepted = target.receiveEnergy(extracted, false);
                    storage.extractEnergy(accepted, false);
                }
            }
        }
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