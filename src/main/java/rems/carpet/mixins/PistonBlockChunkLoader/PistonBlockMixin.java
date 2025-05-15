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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import rems.carpet.REMSSettings;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;


@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin
{

    private static final ChunkTicketType<ChunkPos> PISTON_BLOCK_TICKET = ChunkTicketType.create("piston_block", Comparator.comparingLong(ChunkPos::toLong), 60);

    private static final ChunkTicketType<ChunkPos> PISTON_LAZY_TICKET = ChunkTicketType.create("piston_block", Comparator.comparingLong(ChunkPos::toLong), 10);

    private static final int DiamondOreHash = new Identifier("minecraft", "diamond_ore").hashCode();

    private static final int GoldOreHash = new Identifier("minecraft", "gold_ore").hashCode();

    private static final int RedStoneOreHash = new Identifier("minecraft", "redstone_ore").hashCode();

    private static final int BedrockHash = new Identifier("minecraft", "bedrock").hashCode();

    private static final int RedStoneTorchHash = new Identifier("minecraft", "redstone_torch").hashCode();

    @Inject(method = "onSyncedBlockEvent", at = @At("HEAD"))
    private void load(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable info)
    {
        if(REMSSettings.pistonBlockChunkLoader && !world.isClient)
        {
            Direction direction = state.get(FacingBlock.FACING);

            BlockPos nbp = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
            Block block = world.getBlockState(nbp).getBlock();

            BlockPos nbp1 = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
            Block block1 = world.getBlockState(nbp1).getBlock();

            BlockPos nbp2 = pos.offset(direction.getOpposite()).up();
            Block block2 = world.getBlockState(nbp2).getBlock();

            if (Registries.BLOCK.getId(block).hashCode() == DiamondOreHash)
            {
                int x = pos.getX() + direction.getOffsetX();
                int z = pos.getZ() + direction.getOffsetZ();

                ChunkPos cp = new ChunkPos(x >> 4, z >> 4);
                ((ServerWorld) world).getChunkManager().addTicket(PISTON_LAZY_TICKET, cp, 1, cp);
            }
            if (Registries.BLOCK.getId(block).hashCode() == RedStoneOreHash)
            {
                int x = pos.getX() + direction.getOffsetX();
                int z = pos.getZ() + direction.getOffsetZ();

                ChunkPos cp = new ChunkPos(x >> 4, z >> 4);
                ((ServerWorld) world).getChunkManager().addTicket(PISTON_BLOCK_TICKET, cp, 3, cp);
            }
            if (Registries.BLOCK.getId(block).hashCode() == GoldOreHash)
            {
                int x = pos.getX() + direction.getOffsetX();
                int z = pos.getZ() + direction.getOffsetZ();

                ChunkPos cp = new ChunkPos(x >> 4, z >> 4);
                ((ServerWorld) world).getChunkManager().addTicket(PISTON_BLOCK_TICKET, cp, 2, cp);
            }
            if (Registries.BLOCK.getId(block1).hashCode() == BedrockHash && Registries.BLOCK.getId(block2).hashCode() == RedStoneTorchHash &&
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
