package com.rtn.spatialminer.tileentity;

import com.rtn.spatialminer.registry.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class CableTileEntity extends BlockEntity implements IEnergyStorage {
    private long energy;
    private final int capacity = 1000;

    public CableTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.CABLE_TILE.get(), pos, state);
        this.energy = 0;
    }

    @Override
    public void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.energy = tag.getLong("energy");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putLong("energy", this.energy);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CableTileEntity cable) {
        if (level.isClientSide()) return;

        cable.transferEnergyThroughNetwork();
    }

    private void transferEnergyThroughNetwork() {
        // Логика передачи энергии
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) return 0;

        long canReceive = Math.min(capacity - energy, maxReceive);
        if (!simulate) {
            energy += canReceive;
            setChanged();
        }
        return (int) canReceive;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract <= 0) return 0;

        long canExtract = Math.min(energy, maxExtract);
        if (!simulate) {
            energy -= canExtract;
            setChanged();
        }
        return (int) canExtract;
    }

    @Override
    public int getEnergyStored() {
        return (int) energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}