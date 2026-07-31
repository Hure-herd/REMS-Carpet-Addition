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

package rems.carpet.utils.ChunkLoader;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.registry.RegistryKey;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkLoaderState {

    public static void init() {}

    //#if MC<12105
    public static final ChunkTicketType<ChunkPos> ENDER_PEARLS = ChunkTicketType.create("ender_pearls", Comparator.comparingLong(ChunkPos::toLong), 20);
    //#elseif MC<12109
    //$$ public static final ChunkTicketType ENDER_PEARLS = ChunkTicketType.register("ender_pearls", 20,true,ChunkTicketType.Use.LOADING_AND_SIMULATION);
    //#else
    //$$ public static final ChunkTicketType ENDER_PEARLS = ChunkTicketType.register("ender_pearls", 20,15);
    //#endif

    //#if MC<12105
    public static final ChunkTicketType<ChunkPos> CHEST_MINECART_TICKET = ChunkTicketType.create("chest_minecart_loader", Comparator.comparingLong(ChunkPos::toLong), 40);
    //#elseif MC<12109
    //$$ public static final ChunkTicketType CHEST_MINECART_TICKET = ChunkTicketType.register("chest_minecart_loader", 40,true,ChunkTicketType.Use.LOADING_AND_SIMULATION);
    //#else
    //$$ public static final ChunkTicketType CHEST_MINECART_TICKET = ChunkTicketType.register("chest_minecart_loader", 40,15);
    //#endif

    //#if MC<12105
    public static final ChunkTicketType<ChunkPos> PISTON_BLOCK_TICKET = ChunkTicketType.create("piston_block", Comparator.comparingLong(ChunkPos::toLong), 60);
    //#elseif MC<12109
    //$$ public static final ChunkTicketType PISTON_BLOCK_TICKET = ChunkTicketType.register("piston_block",60,true,ChunkTicketType.Use.LOADING_AND_SIMULATION);
    //#else
    //$$ public static final ChunkTicketType PISTON_BLOCK_TICKET = ChunkTicketType.register("piston_block",60,15);
    //#endif

    private static final Map<RegistryKey<World>, Long2LongMap> LAZY_CHUNKS_BY_DIM = new HashMap<>();

    public static void addLazyChunk(ServerWorld world, ChunkPos pos) {
        long expireTick = world.getTime() + 10L;
        LAZY_CHUNKS_BY_DIM.computeIfAbsent(world.getRegistryKey(), k -> new Long2LongOpenHashMap()).put(pos.toLong(), expireTick);
    }

    public static boolean isLazyChunk(ServerWorld world, long chunkPosLong) {
        Long2LongMap map = LAZY_CHUNKS_BY_DIM.get(world.getRegistryKey());
        if (map == null) return false;
        long expireTick = map.getOrDefault(chunkPosLong, -1L);
        return expireTick != -1L && world.getTime() <= expireTick;
    }
}
