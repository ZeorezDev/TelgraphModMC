package com.reis.telegraph.network.packets;

import com.mojang.logging.LogUtils;
import com.reis.telegraph.blocks.CableBlock;
import com.reis.telegraph.blocks.TelegraphBlockEntity;
import com.reis.telegraph.registration.ModSounds;
import com.reis.telegraph.system.MessageDeliverySystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SendMessagePacket {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();
    private static final long COOLDOWN_TICKS = 600L; // 30 seconds

    private final BlockPos pos;
    private final String message;
    private final int channel;

    public SendMessagePacket(BlockPos pos, String message, int channel) {
        this.pos = pos;
        this.message = message;
        this.channel = channel;
    }

    public static void encode(SendMessagePacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeUtf(pkt.message, 256);
        buf.writeVarInt(pkt.channel);
    }

    public static SendMessagePacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        String message = buf.readUtf(256);
        int channel = buf.readVarInt();
        return new SendMessagePacket(pos, message, channel);
    }

    public static void handle(SendMessagePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            LOGGER.debug("[Telegraph] SendMessagePacket received: player={} pos={} channel={} msg='{}'",
                    player.getName().getString(), pkt.pos, pkt.channel, pkt.message);

            // Range check
            if (player.blockPosition().distSqr(pkt.pos) > 64.0) {
                LOGGER.debug("[Telegraph] SendMessagePacket: player too far from {}", pkt.pos);
                return;
            }

            // Validate inputs
            if (pkt.message.isBlank() || pkt.message.length() > 256) {
                LOGGER.debug("[Telegraph] SendMessagePacket: invalid message length");
                return;
            }
            if (pkt.channel < 0 || pkt.channel > 99) {
                LOGGER.debug("[Telegraph] SendMessagePacket: invalid channel {}", pkt.channel);
                return;
            }

            ServerLevel level = player.serverLevel();
            long currentTick = level.getGameTime();

            // Cooldown check
            UUID uuid = player.getUUID();
            Long lastSend = COOLDOWNS.get(uuid);
            if (lastSend != null && currentTick - lastSend < COOLDOWN_TICKS) {
                long secsLeft = (COOLDOWN_TICKS - (currentTick - lastSend)) / 20L;
                LOGGER.debug("[Telegraph] SendMessagePacket: cooldown active for {}, {}s remaining",
                        player.getName().getString(), secsLeft);
                player.sendSystemMessage(Component.translatable("message.telegraph.cooldown", secsLeft));
                return;
            }

            // Validate block entity
            BlockEntity be = level.getBlockEntity(pkt.pos);
            if (!(be instanceof TelegraphBlockEntity tbe)) {
                LOGGER.debug("[Telegraph] SendMessagePacket: no TelegraphBlockEntity at {}", pkt.pos);
                return;
            }

            // Check that the machine has at least one cable connected
            boolean hasCable = false;
            for (Direction dir : Direction.values()) {
                if (level.getBlockState(pkt.pos.relative(dir)).getBlock() instanceof CableBlock) {
                    hasCable = true;
                    break;
                }
            }
            if (!hasCable) {
                LOGGER.debug("[Telegraph] SendMessagePacket: no cable adjacent to {}", pkt.pos);
                player.sendSystemMessage(Component.literal(
                        "You did not create a telegraph line, therefore the message could not be delivered."));
                return;
            }

            // Update the machine's channel
            tbe.setChannel(pkt.channel);

            LOGGER.debug("[Telegraph] SendMessagePacket: scheduling delivery from {} on ch {}", pkt.pos, pkt.channel);

            // Schedule delivery to connected machines
            MessageDeliverySystem.schedule(level, pkt.pos, pkt.message,
                    player.getName().getString(), pkt.channel, currentTick);

            // Sender feedback: sound + chat
            level.playSound(null, pkt.pos, ModSounds.TELEGRAPH_BEEP.get(),
                    SoundSource.BLOCKS, 0.5f, 1.2f);
            player.sendSystemMessage(Component.translatable("message.telegraph.sent"));

            COOLDOWNS.put(uuid, currentTick);
        });
        ctx.get().setPacketHandled(true);
    }
}
