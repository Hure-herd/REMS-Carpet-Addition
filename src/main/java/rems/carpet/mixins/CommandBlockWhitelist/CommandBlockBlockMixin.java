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

package rems.carpet.mixins.CommandBlockWhitelist;

import rems.carpet.utils.CommandBlockWhitelist.CbWhitelist;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.CommandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import rems.carpet.utils.ComponentTranslate;

@Mixin(CommandBlock.class)
public class CommandBlockBlockMixin {
    @WrapOperation(
            method = "onUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;isCreativeLevelTwoOp()Z"
            )
    )
    private boolean forceCloseGuiIfNotInWhitelist(PlayerEntity player, Operation<Boolean> original) {
        if(CbWhitelist.isOpen()){
            if(player instanceof  ServerPlayerEntity serverPlayer){
                //#if MC<12110
                String playerName = player.getGameProfile().getName();
                //#else
                //$$ String playerName = player.getGameProfile().name();
                //#endif
                if (!CbWhitelist.WHITELIST.contains(playerName)) {
                    //#if MC<12111
                    serverPlayer.sendMessage(ComponentTranslate.error("CommandBlockWhitelist.4"), false);
                    //#else
                    //$$ serverPlayer.sendMessage(ComponentTranslate.error("CommandBlockWhitelist.4"));
                    //#endif
                    serverPlayer.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket(player.currentScreenHandler.syncId));
                    return false;
                }else {
                    return true;
                }
            }
        }
        return true;
    }
}
