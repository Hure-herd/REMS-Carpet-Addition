/*
 * This file is part of the Carpet AMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 A Minecraft Server and contributors
 *
 * Carpet AMS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet AMS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet AMS Addition. If not, see <https://www.gnu.org/licenses/>.
 */

package rems.carpet.mixins.PistonBlockChunkLoader;


import net.minecraft.block.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ChunkTicketType;
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

import java.util.Comparator;
import java.util.Objects;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin
{
    private static final ChunkTicketType<ChunkPos> PISTON_BLOCK_TICKET = ChunkTicketType.create("piston_block", Comparator.comparingLong(ChunkPos::toLong), 60);

    private static final ChunkTicketType<ChunkPos> PISTON_LAZY_TICKET = ChunkTicketType.create("piston_block", Comparator.comparingLong(ChunkPos::toLong), 10);

    @Inject(method = "onSyncedBlockEvent", at = @At("HEAD"))
    private void load(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable info)
    {
        if(REMSSettings.pistonBlockChunkLoader && !world.isClient)
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
                ((ServerWorld) world).getChunkManager().addTicket(PISTON_LAZY_TICKET, cp, 1, cp);
            }
            if (pistonBlock.isOf(Blocks.REDSTONE_ORE))
            {
                int x = pos.getX() + direction.getOffsetX();
                int z = pos.getZ() + direction.getOffsetZ();

                ChunkPos cp = new ChunkPos(x >> 4, z >> 4);
                ((ServerWorld) world).getChunkManager().addTicket(PISTON_BLOCK_TICKET, cp, 3, cp);
            }
            if (pistonBlock.isOf(Blocks.GOLD_ORE))
            {
                int x = pos.getX() + direction.getOffsetX();
                int z = pos.getZ() + direction.getOffsetZ();

                ChunkPos cp = new ChunkPos(x >> 4, z >> 4);
                ((ServerWorld) world).getChunkManager().addTicket(PISTON_BLOCK_TICKET, cp, 2, cp);
            }
            if (pistonBlock1.isOf(Blocks.BEDROCK) && pistonBlock2.isOf(Blocks.REDSTONE_TORCH) &&
                    world.getRegistryKey() == World.NETHER)
            {
                int x = pos.getX() + direction.getOffsetX();
                int z = pos.getZ() + direction.getOffsetZ();

                ChunkPos cp = new ChunkPos(x >> 4, z >> 4);
                ((ServerWorld) world).getChunkManager().addTicket(PISTON_LAZY_TICKET, cp, 1, cp);
            }
        }
    }
}
