package com.reis.telegraph.client;

import com.reis.telegraph.gui.TelegraphReadScreen;
import com.reis.telegraph.gui.TelegraphScreen;
import com.reis.telegraph.items.TelegraphMessageItem;
import com.reis.telegraph.network.packets.OpenGuiPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Client-only setup. Invoked via DistExecutor.safeRunWhenOn so the entire
 * class reference is stripped from the server bytecode by runtimedistcleaner.
 * This ensures no client-only Screen subclass appears in the constant pool of
 * any class that is loaded on the dedicated server.
 */
@OnlyIn(Dist.CLIENT)
public class TelegraphClientSetup {

    public static void init() {
        OpenGuiPacket.clientScreenOpener = (pos, channel, stationName, quality) ->
                net.minecraft.client.Minecraft.getInstance()
                        .setScreen(new TelegraphScreen(pos, channel, stationName, quality));

        TelegraphMessageItem.clientScreenOpener = stack ->
                net.minecraft.client.Minecraft.getInstance()
                        .setScreen(new TelegraphReadScreen(stack));
    }
}
