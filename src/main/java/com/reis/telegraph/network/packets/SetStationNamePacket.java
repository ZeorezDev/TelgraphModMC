package com.reis.telegraph.network.packets;

import com.mojang.logging.LogUtils;
import com.reis.telegraph.blocks.TelegraphBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class SetStationNamePacket {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final BlockPos pos;
    private final String name;

    public SetStationNamePacket(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    public static void encode(SetStationNamePacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeUtf(pkt.name, 32);
    }

    public static SetStationNamePacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        String name = buf.readUtf(32);
        return new SetStationNamePacket(pos, name);
    }

    public static void handle(SetStationNamePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (player.blockPosition().distSqr(pkt.pos) > 64.0) return;

            BlockEntity be = player.serverLevel().getBlockEntity(pkt.pos);
            if (!(be instanceof TelegraphBlockEntity tbe)) return;

            LOGGER.debug("[Telegraph] SetStationNamePacket: {} renamed {} to '{}'",
                    player.getName().getString(), pkt.pos, pkt.name);
            tbe.setStationName(pkt.name);
        });
        ctx.get().setPacketHandled(true);
    }
}
