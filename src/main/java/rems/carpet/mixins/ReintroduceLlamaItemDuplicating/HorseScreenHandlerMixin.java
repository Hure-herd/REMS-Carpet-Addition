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

package rems.carpet.mixins.ReintroduceLlamaItemDuplicating;

import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;
//#if MC<12111
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.entity.passive.AbstractHorseEntity;
//#else
//$$ import net.minecraft.screen.MountScreenHandler;
//$$ import net.minecraft.entity.LivingEntity;
//#endif

//#if MC<12111
@Mixin(HorseScreenHandler.class)
//#else
//$$ @Mixin(MountScreenHandler.class)
//#endif
public class HorseScreenHandlerMixin {

    //#if MC<12111
    @Shadow @Final private AbstractHorseEntity entity;
    //#else
    //$$ @Shadow @Final protected LivingEntity mount;
    //#endif

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void forceKeepInventoryOpen(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        //#if MC<12111
        if(!(entity instanceof LlamaEntity))return;
        //#else
        //$$ if(!(mount instanceof LlamaEntity))return;
        //#endif
        if(!REMSSettings.reintroduceLlamaItemDuplicating)return;
        cir.setReturnValue(true);
    }
}
