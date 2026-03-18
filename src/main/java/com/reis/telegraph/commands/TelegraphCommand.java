package com.reis.telegraph.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.reis.telegraph.blocks.TelegraphBlock;
import com.reis.telegraph.blocks.TelegraphBlockEntity;
import com.reis.telegraph.network.NetworkManager;
import com.reis.telegraph.registration.ModSounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import org.joml.Vector3f;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TelegraphCommand {
    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();
    private static final int COOLDOWN_TIME = 30000;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("telegraph")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            UUID playerUUID = player.getUUID();
                            long currentTime = System.currentTimeMillis();

                            if (COOLDOWNS.containsKey(playerUUID)) {
                                long lastTime = COOLDOWNS.get(playerUUID);
                                if (currentTime - lastTime < COOLDOWN_TIME) {
                                    long secondsLeft = (COOLDOWN_TIME - (currentTime - lastTime)) / 1000;
                                    player.sendSystemMessage(Component.literal("§c[Telegraph] Wait " + secondsLeft + " seconds!"));
                                    return 0;
                                }
                            }

                            BlockPos playerPos = player.blockPosition();
                            BlockPos senderMachine = null;

                            for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-3,-3,-3), playerPos.offset(3,3,3))) {
                                if (player.level().getBlockState(pos).getBlock() instanceof TelegraphBlock) {
                                    senderMachine = pos.immutable();
                                    break;
                                }
                            }

                            if (senderMachine != null) {
                                String msg = StringArgumentType.getString(context, "message");
                                Set<BlockPos> targets = NetworkManager.findConnectedMachines(player.level(), senderMachine);
                                ServerLevel serverWorld = player.serverLevel();

                                // 1. GÖNDEREN MAKİNE: KIRMIZI PARÇACIKLAR
                                // Y konumunu +1.5 yaparak bloğun üstünde havada görünmesini sağladık.
                                serverWorld.sendParticles(new DustParticleOptions(new Vector3f(1.0F, 0.0F, 0.0F), 1.5F),
                                        senderMachine.getX() + 0.5, senderMachine.getY() + 1.2, senderMachine.getZ() + 0.5,
                                        30, 0.3, 0.3, 0.3, 0.1);

                                for (BlockPos target : targets) {
                                    if (player.level().getBlockEntity(target) instanceof TelegraphBlockEntity be) {
                                        be.setMessage(player.getName().getString() + ": " + msg);

                                        // 2. ALICI MAKİNE: YEŞİL PARÇACIKLAR VE SES
                                        // Renk: Yeşil (0, 1, 0), Boyut: 1.5F, Miktar: 30
                                        serverWorld.sendParticles(new DustParticleOptions(new Vector3f(0.0F, 1.0F, 0.0F), 1.5F),
                                                target.getX() + 0.5, target.getY() + 1.2, target.getZ() + 0.5,
                                                30, 0.3, 0.3, 0.3, 0.1);

                                        serverWorld.playSound(null, target, ModSounds.TELEGRAPH_BEEP.get(),
                                                SoundSource.BLOCKS, 0.3f, 0.7f);
                                    }
                                }

                                COOLDOWNS.put(playerUUID, currentTime);
                                player.sendSystemMessage(Component.literal("§a[Telegraph] Message sent!"));
                            } else {
                                player.sendSystemMessage(Component.literal("§c[Telegraph] No machine nearby!"));
                            }
                            return 1;
                        })));
    }
}