package com.rtn.spatialminer.network;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.*;

public class NetworkManager {
    private static final Map<Level, Map<Integer, EnergyNetwork>> networks = new HashMap<>();
    private static int nextNetworkId = 1;

    public static EnergyNetwork getOrCreateNetwork(Level level, BlockPos pos) {
        Map<Integer, EnergyNetwork> levelNetworks = networks.computeIfAbsent(level, k -> new HashMap<>());

        // Проверяем, есть ли уже сеть для этой позиции
        for (EnergyNetwork network : levelNetworks.values()) {
            if (network.getConnectedBlocks().contains(pos)) {
                return network;
            }
        }

        // Создаём новую сеть
        int networkId = nextNetworkId++;
        EnergyNetwork network = new EnergyNetwork(level, networkId);
        network.addBlock(pos);
        levelNetworks.put(networkId, network);

        return network;
    }

    public static void connectNetworks(Level level, BlockPos pos1, BlockPos pos2) {
        Map<Integer, EnergyNetwork> levelNetworks = networks.get(level);
        if (levelNetworks == null) return;

        EnergyNetwork network1 = null;
        EnergyNetwork network2 = null;

        for (EnergyNetwork network : levelNetworks.values()) {
            if (network.getConnectedBlocks().contains(pos1)) {
                network1 = network;
            }
            if (network.getConnectedBlocks().contains(pos2)) {
                network2 = network;
            }
        }

        if (network1 != null && network2 != null && network1 != network2) {
            // Объединяем сети
            network1.mergeWith(network2);
            levelNetworks.remove(network2.getNetworkId());
        } else if (network1 != null) {
            network1.addBlock(pos2);
        } else if (network2 != null) {
            network2.addBlock(pos1);
        }
    }

    public static void removeBlockFromNetwork(Level level, BlockPos pos) {
        Map<Integer, EnergyNetwork> levelNetworks = networks.get(level);
        if (levelNetworks == null) return;

        for (EnergyNetwork network : levelNetworks.values()) {
            if (network.getConnectedBlocks().contains(pos)) {
                network.removeBlock(pos);
                if (network.getConnectedBlocks().isEmpty()) {
                    levelNetworks.remove(network.getNetworkId());
                }
                break;
            }
        }
    }

    public static void updateAllNetworks(Level level) {
        Map<Integer, EnergyNetwork> levelNetworks = networks.get(level);
        if (levelNetworks == null) return;

        for (EnergyNetwork network : levelNetworks.values()) {
            network.updateEnergy();
            network.distributeEnergy();
        }
    }
}