package com.reis.telegraph.system;

import com.reis.telegraph.config.TelegraphConfig;
import com.reis.telegraph.network.NetworkManager;

public class SignalQualityCalculator {

    /**
     * Returns a quality score 0–100 based on path metrics.
     * Returns 100 if quality effects are disabled in config.
     */
    public static int calculateQuality(NetworkManager.NetworkPath path) {
        if (!TelegraphConfig.ENABLE_QUALITY_EFFECTS.get()) return 100;

        int maxRange     = TelegraphConfig.MAX_SIGNAL_RANGE.get();
        int relayBoost   = TelegraphConfig.RELAY_BOOST_DISTANCE.get();
        double poleBonus = TelegraphConfig.POLE_QUALITY_BONUS.get();

        double effectiveDist = path.distance()
                - ((double) path.relayCount() * relayBoost)
                - (path.poleCount() * poleBonus * path.distance());

        effectiveDist = Math.max(0.0, effectiveDist);
        int quality = (int) Math.max(0.0, 100.0 - (effectiveDist * 100.0 / maxRange));
        return Math.min(100, quality);
    }
}
