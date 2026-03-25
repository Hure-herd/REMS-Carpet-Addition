/*
 * This file is part of the Carpet REMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 A Minecraft Server and contributors
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

package rems.carpet.mixins.EnderPearlChunkLoader;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;

import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {
    //#if MC<12001
    //$$ @WrapOperation(method = "getCollision",
    //$$         at = @At(value = "INVOKE",
    //$$                 target = "Lnet/minecraft/util/math/Vec3d;add(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"))
    //#elseif MC<12004
    @WrapOperation(method = "getCollision(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/Entity;Ljava/util/function/Predicate;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/world/World;)Lnet/minecraft/util/hit/HitResult;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;add(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"))
    //#elseif MC<12006
    //$$ @WrapOperation(method = "getCollision(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/Entity;Ljava/util/function/Predicate;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/world/World;FLnet/minecraft/world/RaycastContext$ShapeType;)Lnet/minecraft/util/hit/HitResult;",
    //$$         at = @At(value = "INVOKE",
    //$$                 target = "Lnet/minecraft/util/math/Vec3d;add(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"))
    //#elseif MC<12102
    //$$ @WrapOperation(method = "getCollision(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/Entity;Ljava/util/function/Predicate;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/world/World;FLnet/minecraft/world/RaycastContext$ShapeType;)Lnet/minecraft/util/hit/HitResult;",
    //$$         at = @At(value = "INVOKE",
    //$$                 target = "Lnet/minecraft/util/math/Vec3d;add(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"))
    //#else
    //$$ @WrapOperation(method = "getCollision(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/Entity;Ljava/util/function/Predicate;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/world/World;FLnet/minecraft/world/RaycastContext$ShapeType;)Lnet/minecraft/util/hit/HitResult;",
    //$$         at = @At(value = "INVOKE",
    //$$                 target = "Lnet/minecraft/util/math/Vec3d;add(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"))
    //#endif
    private static Vec3d changeRaycastLength(Vec3d vec3d, Vec3d vec, Operation<Vec3d> original){
        if(REMSSettings.projectileRaycastLength > 0 && vec.length() > REMSSettings.projectileRaycastLength){
            vec = vec.normalize();
            vec = vec.multiply(REMSSettings.projectileRaycastLength);
            return vec3d.add(vec);
        }
        return vec3d.add(vec);
    }

    @Inject(method = "getCollision(Lnet/minecraft/entity/Entity;Ljava/util/function/Predicate;)Lnet/minecraft/util/hit/HitResult;", at = @At("HEAD"), cancellable = true)
    private static void onGetCollision(Entity entity, Predicate<Entity> predicate, CallbackInfoReturnable<HitResult> cir) {

        if(!REMSSettings.noSensationPearlLoad)return;

        if (entity instanceof EnderPearlEntity pearl) {
            Vec3d velocity = pearl.getVelocity();

            if (Math.abs(velocity.x) > 300.0D || Math.abs(velocity.z) > 300.0D) {
                Vec3d start = pearl.getPos();
                Vec3d end = start.add(velocity);
                World world = pearl.getWorld();

                double minY = Math.min(start.y, end.y);

                int worldTopLimit = world.getBottomY() + world.getHeight();

                if (minY >= worldTopLimit) {
                    cir.setReturnValue(BlockHitResult.createMissed(end, Direction.UP, BlockPos.ofFloored(end)));
                    return;
                }

                double distance = Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(end.z - start.z, 2));
                int steps = (int) Math.ceil(distance);

                if (steps > 0) {
                    double dx = (end.x - start.x) / steps;
                    double dz = (end.z - start.z) / steps;
                    boolean isSafe = true;

                    for (int i = 0; i <= steps; i++) {
                        int blockX = MathHelper.floor(start.x + dx * i);
                        int blockZ = MathHelper.floor(start.z + dz * i);

                        int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, blockX, blockZ);

                        if (minY <= topY) {
                            isSafe = false;
                            break;
                        }
                    }

                    if (isSafe) {
                        cir.setReturnValue(BlockHitResult.createMissed(end, Direction.UP, BlockPos.ofFloored(end)));
                    }
                }
            }
        }
    }
}
