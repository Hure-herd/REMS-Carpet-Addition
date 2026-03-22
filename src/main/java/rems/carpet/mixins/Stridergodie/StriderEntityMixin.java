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

package rems.carpet.mixins.Stridergodie;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;

@Mixin(StriderEntity.class)
public class StriderEntityMixin {

    @Unique
    private static final int MAX_SPAWN_Y = 170;

    @Inject(method = "canSpawn*", at = @At("HEAD"), cancellable = true)
    private static void checkSpawnHeight(EntityType<StriderEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        if(REMSSettings.stridergodie){
            if (pos.getY() > MAX_SPAWN_Y) {
                cir.setReturnValue(false);
            }
        }
    }
}
