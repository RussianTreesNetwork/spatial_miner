package com.rtn.spatialminer.block.mechanic;

import com.mojang.serialization.MapCodec;
import com.rtn.spatialminer.SpatialMiner;
import com.rtn.spatialminer.tileentity.BatteryTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BatteryBlock extends BaseEntityBlock {
    public BatteryBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BatteryBlock> codec() {
        return simpleCodec(BatteryBlock::new);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BatteryTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == SpatialMiner.BATTERY_TILE.get() && !level.isClientSide) {
            return (lvl, pos, st, entity) -> ((BatteryTileEntity) entity).tick();
        }
        return null;
    }
}