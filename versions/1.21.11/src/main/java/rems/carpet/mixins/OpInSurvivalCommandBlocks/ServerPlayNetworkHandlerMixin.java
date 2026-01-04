/*
 * This file is part of the Carpet REMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 A Minecraft Server and contributors
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

package rems.carpet.mixins.OpInSurvivalCommandBlocks;

import net.minecraft.command.DefaultPermissions;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import rems.carpet.REMSSettings;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Redirect(
            method = "onUpdateCommandBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;isCreativeLevelTwoOp()Z"
            )
    )
    private boolean allowSurvivalOpToSave(ServerPlayerEntity instance) {
        if(!REMSSettings.opInSurvivalCommandBlocks){
            return instance.isCreativeLevelTwoOp();
        }else{
        return instance.getPermissions().hasPermission(DefaultPermissions.GAMEMASTERS);
        }
    }

    @Redirect(
            method = "onUpdateCommandBlockMinecart",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;isCreativeLevelTwoOp()Z"
            )
    )
    private boolean allowSurvivalOpToSaveMinecart(ServerPlayerEntity instance) {
        if(!REMSSettings.opInSurvivalCommandBlocks){
            return instance.isCreativeLevelTwoOp();
        }else{
        return instance.getPermissions().hasPermission(DefaultPermissions.GAMEMASTERS);
        }
    }
}
