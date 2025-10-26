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

package rems.carpet.mixins.EnderGatewayChunkLoader;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSServer;
import rems.carpet.REMSSettings;

import java.util.Comparator;
import java.util.Objects;

@Mixin(EndGatewayBlockEntity.class)
public class EndGatewayBlockEntityMixin {
    //#if MC<12100
    private static final ChunkTicketType<BlockPos> TheEndGateway =
            ChunkTicketType.create("TheEndGateway", Comparator.comparingLong(ChunkPos::toLong), 60);
    @Inject(method ="tryTeleportingEntity",at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;teleport(DDD)V", shift = At.Shift.AFTER))
    private static void load(World world, BlockPos pos, BlockState state, Entity entity, EndGatewayBlockEntity blockEntity, CallbackInfo ci){
        if(REMSSettings.endGatewayChunkLoader){
            BlockPos blockPos = new BlockPos(entity.getBlockPos());
            REMSServer.getServer().getWorld(World.END).getChunkManager().addTicket(TheEndGateway, new ChunkPos(pos), 3, blockPos);
        }
    }
    //#endif
}
