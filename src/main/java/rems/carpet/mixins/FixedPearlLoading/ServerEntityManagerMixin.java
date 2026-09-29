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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.world.entity.EntityIndex;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import rems.carpet.REMSSettings;
import rems.carpet.utils.FixedPearlLoading.RetainedPearlIndex;

@Mixin(ServerEntityManager.class)
public abstract class ServerEntityManagerMixin<T extends EntityLike> {
    @WrapOperation(
            method = "stopTracking",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityIndex;remove(Lnet/minecraft/world/entity/EntityLike;)V"
            )
    )
    private void keepWeaklyLoadedPearlQueryable(EntityIndex<T> index, T entity, Operation<Void> original) {
        original.call(index, entity);
        if (REMSSettings.fixedpearlloading
                && entity instanceof EnderPearlEntity pearl
                && !((Entity) entity).isRemoved()) {
            RetainedPearlIndex.retain(pearl);
        }
    }

    @WrapOperation(
            method = "startTracking",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityIndex;add(Lnet/minecraft/world/entity/EntityLike;)V"
            )
    )
    private void reuseRetainedPearlIndex(EntityIndex<T> index, T entity, Operation<Void> original) {
        if (entity instanceof EnderPearlEntity pearl) {
            RetainedPearlIndex.forget(pearl);
        }
        original.call(index, entity);
    }
}
