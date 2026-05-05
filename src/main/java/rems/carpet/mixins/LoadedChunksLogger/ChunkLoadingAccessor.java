/*
 * This file is part of the REMS-Carpet-Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 A Minecraft Server and contributors
 *
 * REMS-Carpet-Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * REMS-Carpet-Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with REMS-Carpet-Addition. If not, see <https://www.gnu.org/licenses/>.
 */

package rems.carpet.mixins.LoadedChunksLogger;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.world.ChunkHolder;
//#if MC<12100
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
//#else
//$$ import net.minecraft.server.world.ServerChunkLoadingManager;
//#endif
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

//#if MC<12100
@Mixin(ThreadedAnvilChunkStorage.class)
//#else
//$$ @Mixin(ServerChunkLoadingManager.class)
//#endif
public interface ChunkLoadingAccessor {
    @Accessor("chunkHolders")
    Long2ObjectLinkedOpenHashMap<ChunkHolder> getChunkHolders();
    @Invoker("shouldTick")
    boolean invokeShouldTick(ChunkPos pos);
}
