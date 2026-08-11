/*
 * This file is part of the REMS-Carpet-Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 Hureherd and contributors
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

package rems.carpet.mixins.PistonBlockChunkLoader;

import rems.carpet.REMSSettings;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.utils.ChunkLoader.ChunkLoaderState;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin  {

    @Inject(method = "onSyncedBlockEvent", at = @At("HEAD"))
    private void load(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable info) {
        if(REMSSettings.pistonBlockChunkloader && !world.isClient()) {
            Direction direction = state.get(FacingBlock.FACING);

            BlockState pistonBlock = world.getBlockState(pos.up(1));

            BlockState pistonBlock1 = world.getBlockState(pos.down(1));

            BlockPos nbp2 = pos.offset(direction.getOpposite()).up();
            BlockState pistonBlock2 = world.getBlockState(nbp2);

            //#if MC<26000
            if (pistonBlock.isOf(Blocks.REDSTONE_ORE)) {
                //#else
                //$$ if (pistonBlock.is(Blocks.DIAMOND_ORE)) {
                //#endif
                int x = pos.getX() + direction.getOffsetX();
                int z = pos.getZ() + direction.getOffsetZ();

                ChunkPos cp = new ChunkPos(x >> 4, z >> 4);
                //#if MC<12108
                ((ServerWorld) world).getChunkManager().addTicket(ChunkLoaderState.PISTON_BLOCK_TICKET, cp, 2, cp);
                //#else
                //$$ ((ServerWorld) world).getChunkManager().addTicket(ChunkLoaderState.PISTON_BLOCK_TICKET, cp, 2);
                //#endif
            }
            //#if MC<26000
            if (pistonBlock.isOf(Blocks.DIAMOND_ORE)) {
            //#else
            //$$ if (pistonBlock.is(Blocks.DIAMOND_ORE)) {
            //#endif
                ServerWorld serverWorld = (ServerWorld) world;
                int x = pos.getX() + direction.getOffsetX();
                int z = pos.getZ() + direction.getOffsetZ();
                ChunkPos cp = new ChunkPos(x >> 4, z >> 4);
                //#if MC<12108
                ((ServerWorld) world).getChunkManager().addTicket(ChunkLoaderState.PISTON_BLOCK_TICKET, cp, 1, cp);
                //#else
                //$$ ((ServerWorld) world).getChunkManager().addTicket(ChunkLoaderState.PISTON_BLOCK_TICKET, cp, 1);
                //#endif
                ChunkLoaderState.addLazyChunk(serverWorld, cp);
                boolean allLazy = true;
                int cpX = cp.x;
                int cpZ = cp.z;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        long targetPos = ChunkPos.toLong(cpX + dx, cpZ + dz);
                        if (!ChunkLoaderState.isLazyChunk(serverWorld, targetPos)) {
                            allLazy = false;
                            break;
                        }
                    }
                    if (!allLazy) break;
                }
                if (allLazy) {
                    //#if MC<12108
                    ((ServerWorld) world).getChunkManager().addTicket(ChunkLoaderState.PISTON_BLOCK_TICKET, cp, 2, cp);
                    //#else
                    //$$ ((ServerWorld) world).getChunkManager().addTicket(ChunkLoaderState.PISTON_BLOCK_TICKET, cp, 2);
                    //#endif
                }
            }
            //#if MC<26000
            if (pistonBlock1.isOf(Blocks.BEDROCK) && pistonBlock2.isOf(Blocks.REDSTONE_TORCH) &&
                    world.getRegistryKey() == World.NETHER) {
            //#else
            //$$ if (pistonBlock1.is(Blocks.BEDROCK) && pistonBlock2.is(Blocks.REDSTONE_TORCH) &&
            //$$         world.getRegistryKey() == World.NETHER) {
            //#endif
                ServerWorld serverWorld = (ServerWorld) world;
                int x = pos.getX() + direction.getOffsetX();
                int z = pos.getZ() + direction.getOffsetZ();
                ChunkPos cp = new ChunkPos(x >> 4, z >> 4);
                //#if MC<12108
                ((ServerWorld) world).getChunkManager().addTicket(ChunkLoaderState.PISTON_BLOCK_TICKET, cp, 1, cp);
                //#else
                //$$ ((ServerWorld) world).getChunkManager().addTicket(ChunkLoaderState.PISTON_BLOCK_TICKET, cp, 1);
                //#endif
                ChunkLoaderState.addLazyChunk(serverWorld, cp);
                boolean allLazy = true;
                int cpX = cp.x;
                int cpZ = cp.z;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        long targetPos = ChunkPos.toLong(cpX + dx, cpZ + dz);
                        if (!ChunkLoaderState.isLazyChunk(serverWorld, targetPos)) {
                            allLazy = false;
                            break;
                        }
                    }
                    if (!allLazy) break;
                }
                if (allLazy) {
                    //#if MC<12108
                    ((ServerWorld) world).getChunkManager().addTicket(ChunkLoaderState.PISTON_BLOCK_TICKET, cp, 2, cp);
                    //#else
                    //$$ ((ServerWorld) world).getChunkManager().addTicket(ChunkLoaderState.PISTON_BLOCK_TICKET, cp, 2);
                    //#endif
                }
            }
        }
    }
}
