package com.rtn.spatialminer.tileentity;

import com.rtn.spatialminer.registry.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.UUID;

public class ProtectionTileEntity extends BlockEntity {
    private UUID ownerUUID;
    private String ownerName;

    public ProtectionTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.PROTECTION_TILE.get(), pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.hasUUID("owner")) {
            this.ownerUUID = tag.getUUID("owner");
        }
        this.ownerName = tag.getString("ownerName");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (this.ownerUUID != null) {
            tag.putUUID("owner", this.ownerUUID);
        }
        tag.putString("ownerName", this.ownerName != null ? this.ownerName : "");
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ProtectionTileEntity protection) {
        if (level.isClientSide()) return;

        // Устанавливаем владельца при первом размещении
        if (protection.ownerUUID == null) {
            // Найти игрока, который поставил блок
            // Это будет сделано через событие
        }
    }

    public void setOwner(Player player) {
        this.ownerUUID = player.getUUID();
        this.ownerName = player.getName().getString();
        setChanged();
    }

    public boolean isOwner(Player player) {
        return this.ownerUUID != null && this.ownerUUID.equals(player.getUUID());
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public String getOwnerName() {
        return ownerName;
    }
}