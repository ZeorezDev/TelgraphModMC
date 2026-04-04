package com.reis.telegraph.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenGuiPacket {

    private final BlockPos pos;
    private final int currentChannel;

    public OpenGuiPacket(BlockPos pos, int currentChannel) {
        this.pos = pos;
        this.currentChannel = currentChannel;
    }

    public static void encode(OpenGuiPacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeVarInt(pkt.currentChannel);
    }

    public static OpenGuiPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int channel = buf.readVarInt();
        return new OpenGuiPacket(pos, channel);
    }

    public static void handle(OpenGuiPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> openScreen(pkt.pos, pkt.currentChannel));
        ctx.get().setPacketHandled(true);
    }

    // Separated to avoid client class loading on server
    private static void openScreen(BlockPos pos, int channel) {
        net.minecraft.client.Minecraft.getInstance()
                .setScreen(new com.reis.telegraph.gui.TelegraphScreen(pos, channel));
    }
}
