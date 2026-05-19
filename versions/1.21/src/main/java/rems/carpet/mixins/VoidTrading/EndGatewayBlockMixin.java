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

package rems.carpet.mixins.VoidTrading;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.EndGatewayBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import rems.carpet.REMSSettings;
import rems.carpet.utils.VoidTrading.NoEndGatewayTicket;

@Mixin(EndGatewayBlock.class)
public class EndGatewayBlockMixin {
    @ModifyExpressionValue(
            method = "createTeleportTarget",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/TeleportTarget;ADD_PORTAL_CHUNK_TICKET:Lnet/minecraft/world/TeleportTarget$PostDimensionTransition;"
            )
    )
    private TeleportTarget.PostDimensionTransition NoEndGatewayTicket(TeleportTarget.PostDimensionTransition original, ServerWorld world, Entity entity, BlockPos pos) {
        if (REMSSettings.voidTrading && NoEndGatewayTicket.isMarked(pos)) {
            return e -> {};
        }
        else {
            return original;
        }
    }
}
