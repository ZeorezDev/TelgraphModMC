package com.reis.telegraph.network.packets;

import com.mojang.logging.LogUtils;
import com.reis.telegraph.blocks.TelegraphBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class SetChannelPacket {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final BlockPos pos;
    private final int channel;

    public SetChannelPacket(BlockPos pos, int channel) {
        this.pos = pos;
        this.channel = channel;
    }

    public static void encode(SetChannelPacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeVarInt(pkt.channel);
    }

    public static SetChannelPacket decode(FriendlyByteBuf buf) {
        return new SetChannelPacket(buf.readBlockPos(), buf.readVarInt());
    }

    public static void handle(SetChannelPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                LOGGER.debug("[Telegraph] SetChannelPacket: sender is null");
                return;
            }

            if (player.blockPosition().distSqr(pkt.pos) > 64.0) {
                LOGGER.debug("[Telegraph] SetChannelPacket: player too far from {} (player at {})",
                        pkt.pos, player.blockPosition());
                return;
            }

            if (pkt.channel < 0 || pkt.channel > 99) {
                LOGGER.debug("[Telegraph] SetChannelPacket: invalid channel {}", pkt.channel);
                return;
            }

            ServerLevel level = player.serverLevel();
            BlockEntity be = level.getBlockEntity(pkt.pos);
            if (!(be instanceof TelegraphBlockEntity tbe)) {
                LOGGER.debug("[Telegraph] SetChannelPacket: no TelegraphBlockEntity at {}", pkt.pos);
                return;
            }

            int oldChannel = tbe.getChannel();
            tbe.setChannel(pkt.channel);
            LOGGER.debug("[Telegraph] SetChannelPacket: {} set channel {} -> {} at {}",
                    player.getName().getString(), oldChannel, pkt.channel, pkt.pos);
        });
        ctx.get().setPacketHandled(true);
    }
}
