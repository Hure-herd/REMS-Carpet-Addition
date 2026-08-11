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

package rems.carpet.mixins.OpInSurvivalCommandBlocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.CommandBlock;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import rems.carpet.REMSSettings;
//#if MC>12110
//$$ import net.minecraft.command.permission.Permission;
//$$ import net.minecraft.command.permission.PermissionLevel;
//#endif

@Mixin(CommandBlock.class)
public class CommandBlockBlockMixin {

    @WrapOperation(
            method = "onUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;isCreativeLevelTwoOp()Z"
            )
    )
    private boolean allowSurvivalOpToOpen(PlayerEntity instance, Operation<Boolean> original) {
        if(!REMSSettings.opInSurvivalCommandBlocks){
            return instance.isCreativeLevelTwoOp();
        }else{
            //#if MC<12111
            return instance.hasPermissionLevel(2);
            //#else
            //$$ return instance.getPermissions().hasPermission(new Permission.Level(PermissionLevel.fromLevel(2)));
            //#endif
        }
    }
}
