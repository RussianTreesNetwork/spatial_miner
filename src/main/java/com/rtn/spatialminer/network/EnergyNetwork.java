package com.rtn.spatialminer.network;

import com.rtn.spatialminer.tileentity.BatteryTileEntity;
import com.rtn.spatialminer.tileentity.CableTileEntity;
import com.rtn.spatialminer.tileentity.MinerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;

public class EnergyNetwork {
    private final Set<BlockPos> connectedBlocks = new HashSet<>();
    private final Level level;
    private long totalEnergy = 0;
    private int networkId;

    public EnergyNetwork(Level level, int networkId) {
        this.level = level;
        this.networkId = networkId;
    }

    public void addBlock(BlockPos pos) {
        connectedBlocks.add(pos);
    }

    public void removeBlock(BlockPos pos) {
        connectedBlocks.remove(pos);
    }

    public void mergeWith(EnergyNetwork other) {
        // При объединении сетей берём наименьший ID
        this.networkId = Math.min(this.networkId, other.networkId);
        this.connectedBlocks.addAll(other.connectedBlocks);
        this.totalEnergy += other.totalEnergy;
    }

    public void updateEnergy() {
        totalEnergy = 0;
        for (BlockPos pos : connectedBlocks) {
            if (level.getBlockEntity(pos) instanceof IEnergyStorage energyStorage) {
                totalEnergy += energyStorage.getEnergyStored();
            }
        }
    }

    public void distributeEnergy() {
        // Распределяем энергию по сети
        if (connectedBlocks.isEmpty()) return;

        long averageEnergy = totalEnergy / connectedBlocks.size();

        for (BlockPos pos : connectedBlocks) {
            if (level.getBlockEntity(pos) instanceof IEnergyStorage energyStorage) {
                int currentEnergy = energyStorage.getEnergyStored();
                int targetEnergy = (int) Math.min(averageEnergy, energyStorage.getMaxEnergyStored());

                if (currentEnergy < targetEnergy) {
                    // Нужно добавить энергии
                    int needed = targetEnergy - currentEnergy;
                    // Здесь будет логика передачи энергии
                } else if (currentEnergy > targetEnergy) {
                    // Нужно убрать энергию
                    int excess = currentEnergy - targetEnergy;
                    // Здесь будет логика извлечения энергии
                }
            }
        }
    }

    public Set<BlockPos> getConnectedBlocks() {
        return Collections.unmodifiableSet(connectedBlocks);
    }

    public int getNetworkId() {
        return networkId;
    }

    public long getTotalEnergy() {
        return totalEnergy;
    }
}