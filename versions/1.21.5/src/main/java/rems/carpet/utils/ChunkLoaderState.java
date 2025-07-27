/*
 * This file is part of the Carpet REMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 A Minecraft Server and contributors
 *
 * Carpet REMS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet REMS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet REMS Addition. If not, see <https://www.gnu.org/licenses/>.
 */

package rems.carpet.utils;

import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.registry.RegistryKey;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkLoaderState {

    public static final ChunkTicketType PISTON_BLOCK_TICKET = ChunkTicketType.register("piston_block",60,true,ChunkTicketType.Use.LOADING_AND_SIMULATION);

    private static final Map<RegistryKey<World>, Set<ChunkPos>> LAZY_CHUNKS_BY_DIM = new ConcurrentHashMap<>();

    public static void addLazyChunk(ServerWorld world, ChunkPos pos) {
        LAZY_CHUNKS_BY_DIM
                .computeIfAbsent(world.getRegistryKey(), k -> ConcurrentHashMap.newKeySet())
                .add(pos);
    }

    public static boolean isLazyChunk(ServerWorld world, ChunkPos pos) {
        Set<ChunkPos> chunks = LAZY_CHUNKS_BY_DIM.get(world.getRegistryKey());
        return chunks != null && chunks.contains(pos);
    }

    public static void removeLazyChunk(ServerWorld world, ChunkPos pos) {
        LAZY_CHUNKS_BY_DIM
                .computeIfAbsent(world.getRegistryKey(), k -> ConcurrentHashMap.newKeySet())
                .remove(pos);
    }
}
