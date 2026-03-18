package com.reis.telegraph.blocks;

import com.reis.telegraph.registration.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TelegraphBlockEntity extends BlockEntity {
    private String lastMessage = "No messages..."; // Varsayilan mesaj ingilizce

    public TelegraphBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TELEGRAPH_ENTITY.get(), pos, state);
    }

    public void setMessage(String msg) {
        this.lastMessage = msg;
        setChanged();
    }

    public String getMessage() { return this.lastMessage; }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putString("lastMessage", lastMessage);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.lastMessage = tag.getString("lastMessage");
    }
}