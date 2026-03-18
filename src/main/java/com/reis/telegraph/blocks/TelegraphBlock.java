package com.reis.telegraph.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TelegraphBlock extends Block implements EntityBlock {
    public TelegraphBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // RIGHT CLICK: Handle reading messages
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TelegraphBlockEntity telegraph) {
                player.sendSystemMessage(Component.literal("§e[Telegraph] Incoming Message: §f" + telegraph.getMessage()));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // BLOCK ENTITY: Required for message memory
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TelegraphBlockEntity(pos, state);
    }

    // CONNECTION SUPPORT: Allows fences/cables to connect to this block
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }
}