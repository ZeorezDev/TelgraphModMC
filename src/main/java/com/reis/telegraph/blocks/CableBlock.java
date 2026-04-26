package com.reis.telegraph.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class CableBlock extends FenceBlock {

    public static final BooleanProperty UP   = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public CableBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UP, DOWN);
    }

    private boolean isTelegraphMachine(BlockState state) {
        return state.getBlock() instanceof TelegraphBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return super.getStateForPlacement(context)
                .setValue(UP,   isTelegraphMachine(level.getBlockState(pos.above())))
                .setValue(DOWN, isTelegraphMachine(level.getBlockState(pos.below())));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        state = super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
        if (direction == Direction.UP) {
            return state.setValue(UP, isTelegraphMachine(neighborState));
        }
        if (direction == Direction.DOWN) {
            return state.setValue(DOWN, isTelegraphMachine(neighborState));
        }
        return state;
    }
}
