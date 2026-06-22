package com.rtn.spatialminer.tileentity;

import com.rtn.spatialminer.Config;
import com.rtn.spatialminer.SpatialMiner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class MinerTileEntity extends BlockEntity implements IEnergyStorage, MenuProvider {
    private final EnergyStorage storage = new EnergyStorage(Config.MINER_CAPACITY);
    private final ItemStackHandler inventory = new ItemStackHandler(12) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private int progress = 0;
    private final int COST_PER_BLOCK = Config.MINER_COST_PER_BLOCK;
    private final int EXPORT_SPEED = Config.MINER_EXPORT_SPEED;

    public MinerTileEntity(BlockPos pos, BlockState state) {
        super(SpatialMiner.MINER_TILE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        // 1. Проверяем, есть ли место в инвентаре
        if (isInventoryFull()) return;

        // 2. Проверяем, достаточно ли энергии для генерации
        if (storage.getEnergyStored() < COST_PER_BLOCK) return;

        // 3. Генерируем блок (пока просто камень для теста)
        storage.extractEnergy(COST_PER_BLOCK, false);
        ItemStack generated = new ItemStack(net.minecraft.world.level.block.Blocks.COBBLESTONE, 1);

        // 4. Кладём в инвентарь
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.insertItem(i, generated, false).isEmpty()) {
                break;
            }
        }

        // 5. Экспорт в соседние контейнеры
        exportItems();
    }

    private boolean isInventoryFull() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    private void exportItems() {
        if (level == null) return;

        for (Direction dir : Direction.values()) {
            BlockPos targetPos = worldPosition.relative(dir);
            BlockEntity targetEntity = level.getBlockEntity(targetPos);
            if (targetEntity == null) continue;

            // Проверяем, что это не печь и не плавильня (исключения)
            if (isForbiddenContainer(targetEntity)) continue;

            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, targetPos, targetEntity.getBlockState(), targetEntity, dir.getOpposite());
            if (handler == null) continue;

            for (int i = 0; i < handler.getSlots(); i++) {
                for (int j = 0; j < inventory.getSlots(); j++) {
                    ItemStack stack = inventory.getStackInSlot(j);
                    if (stack.isEmpty()) continue;

                    ItemStack remainder = handler.insertItem(i, stack, false);
                    if (remainder.getCount() < stack.getCount()) {
                        inventory.setStackInSlot(j, remainder);
                        return; // Вышли 4 предмета за тик
                    }
                }
            }
        }
    }

    private boolean isForbiddenContainer(BlockEntity entity) {
        // Исключаем печи, коптильни и т.д.
        String name = entity.getBlockState().getBlock().getDescriptionId();
        return name.contains("furnace") || name.contains("smoker") || name.contains("blast_furnace");
    }

    // --- IEnergyStorage ---
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
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    // --- GUI/Container ---
    @Override
    public Component getDisplayName() {
        return Component.literal("Spatial Miner");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        // Здесь мы создадим меню в следующей итерации, пока оставим заглушку
        return null;
    }

    // --- NBT ---
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Energy", storage.getEnergyStored());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));

        // Восстанавливаем энергию через цикл, потому что setEnergy нет в стандартном EnergyStorage
        int savedEnergy = tag.getInt("Energy");
        while (storage.getEnergyStored() < savedEnergy) {
            storage.receiveEnergy(savedEnergy - storage.getEnergyStored(), false);
        }
    }
}