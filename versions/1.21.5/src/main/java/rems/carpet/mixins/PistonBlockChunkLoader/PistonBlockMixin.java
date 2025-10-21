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

package rems.carpet.mixins.PistonBlockChunkLoader;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;
import rems.carpet.utils.ChunkLoaderState;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin
{
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Inject(method = "onSyncedBlockEvent", at = @At("HEAD"))
    private void load(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable info)
    {
        if(REMSSettings.pistonBlockChunkLoader && !world.isClient())
        {
            Direction direction = state.get(FacingBlock.FACING);

            BlockState pistonBlock = world.getBlockState(pos.up(1));

            BlockState pistonBlock1 = world.getBlockState(pos.down(1));

            BlockPos nbp2 = pos.offset(direction.getOpposite()).up();
            BlockState pistonBlock2 = world.getBlockState(nbp2);

            if (pistonBlock.isOf(Blocks.DIAMOND_ORE))
            {
                int x = pos.getX() + direction.getOffsetX();
                int z = pos.getZ() + direction.getOffsetZ();

                ChunkPos cp = new ChunkPos(x >> 4, z >> 4);
                ((ServerWorld) world).getChunkManager().addTicket(ChunkLoaderState.PISTON_BLOCK_TICKET, cp, 1);

                ChunkLoaderState.addLazyChunk(((ServerWorld) world), cp);

                int[] xOffsets = {-1, 0, 1};
                int[] zOffsets = {-1, 0, 1};
                boolean allLazy = true;

                for (int dx : xOffsets) {
                    for (int dz : zOffsets) {
                        ChunkPos target = new ChunkPos(cp.x + dx, cp.z + dz);
                        if (!ChunkLoaderState.isLazyChunk(((ServerWorld) world), target)) {
                            allLazy = false;
                            break;
                        }
                    }
                    if (!allLazy) break;
                }

                if (allLazy) {
                    ((ServerWorld) world).getChunkManager().addTicket(ChunkLoaderState.PISTON_BLOCK_TICKET, cp, 2);
                }
                scheduler.schedule(() -> {
                    world.getServer().execute(() -> {
                        ChunkLoaderState.removeLazyChunk(((ServerWorld) world), cp);
                    });
                }, 1000, TimeUnit.MILLISECONDS);
            }

            if (pistonBlock1.isOf(Blocks.BEDROCK) && pistonBlock2.isOf(Blocks.REDSTONE_TORCH) &&
                    world.getRegistryKey() == World.NETHER)
            {
                int x = pos.getX() + direction.getOffsetX();
                int z = pos.getZ() + direction.getOffsetZ();

                ChunkPos cp = new ChunkPos(x >> 4, z >> 4);
                ((ServerWorld) world).getChunkManager().addTicket(ChunkLoaderState.PISTON_BLOCK_TICKET, cp, 1);

                ChunkLoaderState.addLazyChunk(((ServerWorld) world), cp);

                int[] xOffsets = {-1, 0, 1};
                int[] zOffsets = {-1, 0, 1};
                boolean allLazy = true;

                for (int dx : xOffsets) {
                    for (int dz : zOffsets) {
                        ChunkPos target = new ChunkPos(cp.x + dx, cp.z + dz);
                        if (!ChunkLoaderState.isLazyChunk(((ServerWorld) world), target)) {
                            allLazy = false;
                            break;
                        }
                    }
                    if (!allLazy) break;
                }

                if (allLazy) {
                    ((ServerWorld) world).getChunkManager().addTicket(ChunkLoaderState.PISTON_BLOCK_TICKET, cp, 2);
                }
                scheduler.schedule(() -> {
                    world.getServer().execute(() -> {
                        ChunkLoaderState.removeLazyChunk(((ServerWorld) world), cp);
                    });
                }, 1000, TimeUnit.MILLISECONDS);
            }
        }
    }
}
