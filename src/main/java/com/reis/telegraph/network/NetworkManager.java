package com.reis.telegraph.network;

import com.reis.telegraph.blocks.CableBlock;
import com.reis.telegraph.blocks.InsulatorBlock;
import com.reis.telegraph.blocks.RelayStationBlock;
import com.reis.telegraph.blocks.TelegraphBlock;
import com.reis.telegraph.blocks.TelegraphPoleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class NetworkManager {

    /** Path metadata returned by the quality-aware BFS. */
    public record NetworkPath(int distance, int relayCount, int poleCount) {}

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

    /**
     * Quality-aware BFS: returns NetworkPath per reachable TelegraphBlock, tracking
     * relay station count and pole/insulator count along each path.
     * Recognizes CableBlock, RelayStationBlock, TelegraphPoleBlock, InsulatorBlock as traversable.
     */
    public static Map<BlockPos, NetworkPath> findConnectedMachinesWithPaths(Level level, BlockPos start) {
        Map<BlockPos, NetworkPath> machines = new HashMap<>();
        Set<BlockPos> visited = new HashSet<>();
        // Queue entries: {x, y, z, distance, relayCount, poleCount}
        Queue<int[]> queue = new LinkedList<>();

        visited.add(start);
        queue.add(new int[]{start.getX(), start.getY(), start.getZ(), 0, 0, 0});

        while (!queue.isEmpty()) {
            int[] entry = queue.poll();
            BlockPos current = new BlockPos(entry[0], entry[1], entry[2]);
            int distance   = entry[3];
            int relayCount = entry[4];
            int poleCount  = entry[5];

            Block currentBlock = level.getBlockState(current).getBlock();
            if (currentBlock instanceof TelegraphBlock) {
                machines.put(current, new NetworkPath(distance, relayCount, poleCount));
            }

            for (Direction dir : Direction.values()) {
                BlockPos next = current.relative(dir);
                if (!visited.contains(next)) {
                    Block nextBlock = level.getBlockState(next).getBlock();
                    boolean traversable = nextBlock instanceof CableBlock
                            || nextBlock instanceof TelegraphBlock
                            || nextBlock instanceof RelayStationBlock
                            || nextBlock instanceof TelegraphPoleBlock
                            || nextBlock instanceof InsulatorBlock;

                    if (traversable) {
                        visited.add(next);
                        int nextRelay = relayCount + (nextBlock instanceof RelayStationBlock ? 1 : 0);
                        int nextPole  = poleCount  + ((nextBlock instanceof TelegraphPoleBlock
                                                    || nextBlock instanceof InsulatorBlock) ? 1 : 0);
                        queue.add(new int[]{next.getX(), next.getY(), next.getZ(),
                                            distance + 1, nextRelay, nextPole});
                    }
                }
            }
        }
        return machines;
    }
}
