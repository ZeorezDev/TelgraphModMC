package com.reis.telegraph.network;

import com.reis.telegraph.blocks.CableBlock;
import com.reis.telegraph.blocks.TelegraphBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import java.util.*;

public class NetworkManager {
    public static Set<BlockPos> findConnectedMachines(Level level, BlockPos start) {
        Set<BlockPos> machines = new HashSet<>();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (current == null) continue;

            Block currentBlock = level.getBlockState(current).getBlock();
            if (currentBlock instanceof TelegraphBlock) {
                machines.add(current);
            }

            for (Direction dir : Direction.values()) {
                BlockPos next = current.relative(dir);
                if (!visited.contains(next)) {
                    Block nextBlock = level.getBlockState(next).getBlock();
                    if (nextBlock instanceof CableBlock || nextBlock instanceof TelegraphBlock) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }
        }
        return machines;
    }
}