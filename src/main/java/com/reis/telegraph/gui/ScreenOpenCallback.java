package com.reis.telegraph.gui;

import net.minecraft.core.BlockPos;

/**
 * Common-side functional interface for opening the telegraph GUI screen.
 * Implemented on the client only via TelegraphClientSetup.
 * Having this interface in a common package avoids any client-only class
 * in the constant pool of OpenGuiPacket, which is loaded on both sides.
 */
@FunctionalInterface
public interface ScreenOpenCallback {
    void open(BlockPos pos, int channel, String stationName, int quality);
}
