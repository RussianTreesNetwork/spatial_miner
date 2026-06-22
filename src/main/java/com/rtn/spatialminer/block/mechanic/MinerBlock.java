package com.rtn.spatialminer.block.mechanic;

import com.mojang.serialization.MapCodec;
import com.rtn.spatialminer.SpatialMiner;
import com.rtn.spatialminer.tileentity.MinerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MinerBlock extends BaseEntityBlock {
    public MinerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends MinerBlock> codec() {
        return simpleCodec(MinerBlock::new);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MinerTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == SpatialMiner.MINER_TILE.get() && !level.isClientSide) {
            return (lvl, pos, st, entity) -> ((MinerTileEntity) entity).tick();
        }
        return null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof MinerTileEntity miner) {
                player.openMenu(miner);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}