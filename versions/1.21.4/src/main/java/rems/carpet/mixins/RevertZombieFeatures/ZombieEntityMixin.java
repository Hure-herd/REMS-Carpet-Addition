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

package rems.carpet.mixins.RevertZombieFeatures;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import rems.carpet.REMSSettings;
//#if MC>260102
//$$ import net.minecraft.entity.EntityTypes;
//#endif

@Mixin(ZombieEntity.class)
public class ZombieEntityMixin {

    @WrapOperation(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityType;create(Lnet/minecraft/world/World;Lnet/minecraft/entity/SpawnReason;)Lnet/minecraft/entity/Entity;"
            )
    )
    private Entity forceOnlyZombieReinforcements(EntityType<? extends ZombieEntity> type, World world, SpawnReason reason, Operation<Entity> original) {
        if (REMSSettings.revertZombieFeatures && reason == SpawnReason.REINFORCEMENT) {
            //#if MC<260200
            return EntityType.ZOMBIE.create(world, reason);
            //#else
            //$$ return EntityTypes.ZOMBIE.create(world, reason);
            //#endif
        }
        return original.call(type, world, reason);
    }
}
