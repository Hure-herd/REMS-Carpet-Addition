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

package rems.carpet.mixins.GlobalSoundSuppression;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.block.NeighborUpdater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;
import rems.carpet.utils.GlobalSoundSuppression.UpdateSuppressionContext;

@Mixin(NeighborUpdater.class)
public interface NeighborUpdaterMixin {
    @Inject(method = "tryNeighborUpdate", at = @At("HEAD"))
    private static void enterNeighborUpdate(CallbackInfo ci) {
        if (!REMSSettings.globalSoundSuppression.equals("false")) {
            UpdateSuppressionContext.enterNeighborUpdate();
        }
    }

    @Inject(method = "tryNeighborUpdate", at = @At("RETURN"))
    private static void leaveNeighborUpdate(CallbackInfo ci) {
        UpdateSuppressionContext.leaveNeighborUpdate();
    }

    @Inject(
            method = "tryNeighborUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/crash/CrashReport;create(Ljava/lang/Throwable;Ljava/lang/String;)Lnet/minecraft/util/crash/CrashReport;"
            )
    )
    private static void propagateSoundTriggeredSuppression(
            CallbackInfo ci,
            @Local Throwable throwable
    ) {
        if (throwable instanceof UpdateSuppressionContext.SoundUpdateSuppressionException) {
            UpdateSuppressionContext.leaveNeighborUpdate();
            throw (UpdateSuppressionContext.SoundUpdateSuppressionException) throwable;
        }
    }
}
