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

package rems.carpet.mixins.ChestMinecartEntityChunkLoader;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

@Mixin(Entity.class)
public class ChestMinecartEntityMixin {
    private static final ChunkTicketType CHEST_MINECART_TICKET = ChunkTicketType.register("chest_minecart_loader", 40,true,ChunkTicketType.Use.LOADING_AND_SIMULATION);

    @Inject(method = "move", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if ((Object) this instanceof ChestMinecartEntity) {
            ChestMinecartEntity self = (ChestMinecartEntity) (Object) this;
            //#if MC<12106
            World world = self.getEntityWorld();
            //#else
            //$$ World world = self.getWorld();
            //#endif
            if (REMSSettings.chestMinecartChunkLoader && self.hasCustomName() && !world.isClient && "Load".equals(self.getCustomName().getString())) {
                int chunkX = (int) Math.floor(self.getX()) >> 4;
                int chunkZ = (int) Math.floor(self.getZ()) >> 4;
                ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
                ((ServerWorld) world).getChunkManager().addTicket(CHEST_MINECART_TICKET, chunkPos, 2);
            }
        }
    }
}
