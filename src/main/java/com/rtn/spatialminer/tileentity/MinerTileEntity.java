package com.rtn.spatialminer.tileentity;

import com.rtn.spatialminer.config.ConfigManager;
import com.rtn.spatialminer.registry.ModTileEntities;
import com.rtn.spatialminer.util.TagUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class MinerTileEntity extends BlockEntity implements IEnergyStorage {
    private long energy;
    private final int capacity;
    private int workProgress;
    private final ItemStackHandler inventory;
    private final ItemStackHandler upgradeSlots;

    public MinerTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.MINER_TILE.get(), pos, state);
        this.capacity = ConfigManager.minerCapacity;
        this.energy = 0;
        this.workProgress = 0;
        this.inventory = new ItemStackHandler(ConfigManager.minerInventorySize);
        this.upgradeSlots = new ItemStackHandler(ConfigManager.minerUpgradeSlots);
    }

    @Override
    public void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.energy = tag.getLong("energy");
        this.workProgress = tag.getInt("workProgress");
        this.inventory.deserializeNBT(provider, tag.getCompound("inventory"));
        this.upgradeSlots.deserializeNBT(provider, tag.getCompound("upgradeSlots"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putLong("energy", this.energy);
        tag.putInt("workProgress", this.workProgress);
        tag.put("inventory", this.inventory.serializeNBT(provider));
        tag.put("upgradeSlots", this.upgradeSlots.serializeNBT(provider));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MinerTileEntity miner) {
        if (level.isClientSide()) return;

        if (miner.hasInventorySpace()) {
            if (miner.energy >= ConfigManager.minerEnergyPerTick) {
                miner.energy -= ConfigManager.minerEnergyPerTick;
                miner.workProgress++;

                if (miner.workProgress >= ConfigManager.minerWorkTicks) {
                    miner.generateBlock();
                    miner.workProgress = 0;
                }
            }
        } else {
            miner.workProgress = 0;
        }

        miner.exportItems();
        miner.setChanged();
    }

    private boolean hasInventorySpace() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void generateBlock() {
        List<BlockState> mineableBlocks = TagUtils.getMineableBlocks();
        if (!mineableBlocks.isEmpty() && level != null) {
            BlockState blockToGenerate = mineableBlocks.get(level.random.nextInt(mineableBlocks.size()));
            ItemStack blockItem = new ItemStack(blockToGenerate.getBlock().asItem());

            for (int i = 0; i < inventory.getSlots(); i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    inventory.setStackInSlot(i, blockItem);
                    break;
                } else if (ItemStack.isSameItemSameComponents(inventory.getStackInSlot(i), blockItem)) {
                    int count = inventory.getStackInSlot(i).getCount();
                    if (count < inventory.getStackInSlot(i).getMaxStackSize()) {
                        inventory.getStackInSlot(i).setCount(count + 1);
                        break;
                    }
                }
            }
        }
    }

    private void exportItems() {
        if (level == null) return;

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);

            if (neighbor != null && canExportTo(neighbor)) {
                IItemHandler targetInventory = level.getCapability(Capabilities.ItemHandler.BLOCK, neighborPos, direction.getOpposite());
                if (targetInventory != null) {
                    exportToInventory(targetInventory);
                }
            }
        }
    }

    private boolean canExportTo(BlockEntity neighbor) {
        String blockName = neighbor.getType().builtInRegistryHolder().key().location().toString();
        return !blockName.contains("furnace") &&
                !blockName.contains("smoker") &&
                !blockName.contains("blast_furnace") &&
                !blockName.contains("campfire");
    }

    private void exportToInventory(IItemHandler targetInventory) {
        int itemsToTransfer = Math.min(ConfigManager.transferRate, inventory.getSlots());

        for (int i = 0; i < inventory.getSlots() && itemsToTransfer > 0; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                ItemStack extracted = inventory.extractItem(i, itemsToTransfer, false);
                if (!extracted.isEmpty()) {
                    for (int j = 0; j < targetInventory.getSlots(); j++) {
                        ItemStack remaining = targetInventory.insertItem(j, extracted, false);
                        if (remaining.isEmpty()) {
                            itemsToTransfer -= extracted.getCount();
                            break;
                        } else {
                            extracted = remaining;
                        }
                    }

                    if (!extracted.isEmpty()) {
                        inventory.insertItem(i, extracted, false);
                    }
                }
            }
        }
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
        return 0;
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
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    public IItemHandler getUpgradeSlots() {
        return upgradeSlots;
    }
}