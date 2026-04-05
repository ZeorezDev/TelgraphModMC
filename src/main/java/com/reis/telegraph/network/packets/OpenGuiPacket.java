package com.reis.telegraph.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenGuiPacket {

    private final BlockPos pos;
    private final int currentChannel;
    private final String stationName;
    private final int lastSignalQuality;

    public OpenGuiPacket(BlockPos pos, int currentChannel, String stationName, int lastSignalQuality) {
        this.pos = pos;
        this.currentChannel = currentChannel;
        this.stationName = stationName == null ? "" : stationName;
        this.lastSignalQuality = lastSignalQuality;
    }

    public static void encode(OpenGuiPacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeVarInt(pkt.currentChannel);
        buf.writeUtf(pkt.stationName, 32);
        buf.writeVarInt(pkt.lastSignalQuality);
    }

    public static OpenGuiPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int channel = buf.readVarInt();
        String stationName = buf.readUtf(32);
        int quality = buf.readVarInt();
        return new OpenGuiPacket(pos, channel, stationName, quality);
    }

    public static void handle(OpenGuiPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> openScreen(pkt.pos, pkt.currentChannel, pkt.stationName, pkt.lastSignalQuality));
        ctx.get().setPacketHandled(true);
    }

    // Separated to avoid client class loading on server
    private static void openScreen(BlockPos pos, int channel, String stationName, int quality) {
        net.minecraft.client.Minecraft.getInstance()
                .setScreen(new com.reis.telegraph.gui.TelegraphScreen(pos, channel, stationName, quality));
    }
}
