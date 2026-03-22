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

package rems.carpet.mixins.MergeTNTMax;

import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.network.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

@Mixin(EntityTrackerEntry.class)
public abstract class EntityTrackerEntryMixin {
    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "syncEntityData", at = @At("HEAD"), cancellable = true)
    private void optimizeTntMetadata(CallbackInfo ci) {

        if (!REMSSettings.mergeTNTMax || !(entity instanceof TntEntity tnt)) return;
        if (tnt.getFuse() > 1) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void optimizeTntTick(CallbackInfo ci) {
        if (!REMSSettings.mergeTNTMax || !(entity instanceof TntEntity)) return;

        long time = entity.getWorld().getTime();

        if ((time + entity.getId()) % 20 != 0) {
            ci.cancel();
        }
    }
}
