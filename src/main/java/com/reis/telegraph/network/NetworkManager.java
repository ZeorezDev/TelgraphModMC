package com.reis.telegraph.network;

import com.reis.telegraph.blocks.CableBlock;
import com.reis.telegraph.blocks.TelegraphBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class NetworkManager {

    /**
     * BFS from start position, returning all reachable TelegraphBlock positions
     * mapped to their cable-hop distance from the start.
     */
    public static Map<BlockPos, Integer> findConnectedMachines(Level level, BlockPos start) {
        Map<BlockPos, Integer> machines = new HashMap<>();
        Set<BlockPos> visited = new HashSet<>();
        // Queue entries: [pos, distance]
        Queue<int[]> queue = new LinkedList<>();

        // Each entry: index 0-2 = pos coords, index 3 = distance
        visited.add(start);
        queue.add(new int[]{start.getX(), start.getY(), start.getZ(), 0});

        while (!queue.isEmpty()) {
            int[] entry = queue.poll();
            BlockPos current = new BlockPos(entry[0], entry[1], entry[2]);
            int distance = entry[3];

            Block currentBlock = level.getBlockState(current).getBlock();
            if (currentBlock instanceof TelegraphBlock) {
                machines.put(current, distance);
            }

            for (Direction dir : Direction.values()) {
                BlockPos next = current.relative(dir);
                if (!visited.contains(next)) {
                    Block nextBlock = level.getBlockState(next).getBlock();
                    if (nextBlock instanceof CableBlock || nextBlock instanceof TelegraphBlock) {
                        visited.add(next);
                        queue.add(new int[]{next.getX(), next.getY(), next.getZ(), distance + 1});
                    }
                }
            }
        }
        return machines;
    }
}
