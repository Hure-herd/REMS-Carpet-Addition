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

package rems.carpet.mixins.FixedPearlLoading;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.utils.FixedPearlLoading.RetainedPearlIndex;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void removeRetainedPearlIndex(RemovalReason reason, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof EnderPearlEntity pearl) {
            RetainedPearlIndex.forget(pearl);
        }
    }
}
