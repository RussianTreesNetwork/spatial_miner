package com.rtn.spatialminer.block;

import com.mojang.serialization.MapCodec;
import com.rtn.spatialminer.tileentity.CableTileEntity;
import com.rtn.spatialminer.registry.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class CableBlock extends BaseEntityBlock {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    // ХИТБОКСЫ
    protected static final VoxelShape CENTER_SHAPE = Shapes.box(0.375, 0.375, 0.375, 0.625, 0.625, 0.625);
    protected static final VoxelShape CONNECT_NORTH = Shapes.box(0.375, 0.375, 0.0, 0.625, 0.625, 0.375);
    protected static final VoxelShape CONNECT_SOUTH = Shapes.box(0.375, 0.375, 0.625, 0.625, 0.625, 1.0);
    protected static final VoxelShape CONNECT_EAST  = Shapes.box(0.625, 0.375, 0.375, 1.0, 0.625, 0.625);
    protected static final VoxelShape CONNECT_WEST  = Shapes.box(0.0, 0.375, 0.375, 0.375, 0.625, 0.625);
    protected static final VoxelShape CONNECT_UP    = Shapes.box(0.375, 0.625, 0.375, 0.625, 1.0, 0.625);
    protected static final VoxelShape CONNECT_DOWN  = Shapes.box(0.375, 0.0, 0.375, 0.625, 0.375, 0.625);

    public CableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(EAST, false).setValue(WEST, false)
                .setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    protected MapCodec<? extends CableBlock> codec() {
        return simpleCodec(CableBlock::new);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CableTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == ModTileEntities.CABLE_TILE.get()) {
            return (lvl, pos, st, entity) -> CableTileEntity.tick(lvl, pos, st, (CableTileEntity) entity);
        }
        return null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CENTER_SHAPE;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, CONNECT_NORTH);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, CONNECT_SOUTH);
        if (state.getValue(EAST))  shape = Shapes.or(shape, CONNECT_EAST);
        if (state.getValue(WEST))  shape = Shapes.or(shape, CONNECT_WEST);
        if (state.getValue(UP))    shape = Shapes.or(shape, CONNECT_UP);
        if (state.getValue(DOWN))  shape = Shapes.or(shape, CONNECT_DOWN);
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (neighborState.isAir() || level.isClientSide()) {
            return state.setValue(getPropertyForDirection(direction), false);
        }

        boolean isCable = neighborState.getBlock() instanceof CableBlock;
        boolean hasEnergy = false;
        if (level instanceof Level realLevel) {
            IEnergyStorage storage = realLevel.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, neighborState, null, direction.getOpposite());
            hasEnergy = storage != null;
        }

        boolean connected = isCable || hasEnergy;
        return state.setValue(getPropertyForDirection(direction), connected);
    }

    private BooleanProperty getPropertyForDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }
}