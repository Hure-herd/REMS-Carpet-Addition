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

package rems.carpet.mixins.SpearHitDetection;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
//#if MC<260200
import net.minecraft.entity.projectile.ProjectileUtil;
//#else
//$$ import net.minecraft.util.hit.Raycasts;
//#endif
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import rems.carpet.REMSSettings;

//#if MC<260200
@Mixin(ProjectileUtil.class)
//#else
//$$ @Mixin(Raycasts.class)
//#endif
public class ProjectileUtilMixin {
    @WrapOperation(
            method = "collectPiercingCollisions(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;FLnet/minecraft/world/RaycastContext$ShapeType;Z)Ljava/util/Collection;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getBoundingBox()Lnet/minecraft/util/math/Box;")
    )
    private static Box includeClientRenderedPosition(Entity entity, Operation<Box> original) {
        Box box = original.call(entity);
        if (!REMSSettings.fixSpearHitDetection) return box;
        return box.stretch(entity.getMovement().multiply(-3.0D));
    }
}
