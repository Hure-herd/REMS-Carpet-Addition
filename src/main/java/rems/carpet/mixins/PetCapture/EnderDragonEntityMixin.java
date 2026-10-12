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

package rems.carpet.mixins.PetCapture;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.utils.PetCapture.CapturedPet;
import rems.carpet.utils.PetCapture.PetCapture;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends MobEntity {
    protected EnderDragonEntityMixin(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void peacefulPetDragon(CallbackInfo ci) {
        EnderDragonEntity dragon = (EnderDragonEntity) (Object) this;
        CapturedPet pet = (CapturedPet) dragon;
        if (pet.rems$getPetOwner() == null || !dragon.isAlive()) return;
        dragon.prevWingPosition = dragon.wingPosition;
        dragon.wingPosition += 0.05F;
        dragon.setVelocity(Vec3d.ZERO);
        if (PetCapture.world(dragon).isClient()) {
            //#if MC>=260300
            //$$ dragon.getInterpolator().tick();
            //#elseif MC>=12108
            //$$ dragon.getInterpolator().tick();
            //#elseif MC>=12100
            //$$ dragon.setPosition(dragon.getLerpTargetX(), dragon.getLerpTargetY(), dragon.getLerpTargetZ());
            //$$ dragon.setYaw(dragon.getLerpTargetYaw());
            //$$ dragon.setPitch(dragon.getLerpTargetPitch());
            //#else
            if (bodyTrackingIncrements > 0) {
                dragon.setPosition(serverX, serverY, serverZ);
                dragon.setYaw((float) serverYaw);
                dragon.setPitch((float) serverPitch);
                bodyTrackingIncrements = 0;
            }
            //#endif
        }
        PlayerEntity owner = PetCapture.world(dragon).getPlayerByUuid(pet.rems$getPetOwner());
        if (!PetCapture.world(dragon).isClient() && !pet.rems$isPetSitting() && owner != null && owner.isAlive()) {
            Vec3d movement = new Vec3d(owner.getX() - dragon.getX(), owner.getY() + 10.0 - dragon.getY(), owner.getZ() - dragon.getZ());
            if (movement.lengthSquared() > 16.0 && movement.lengthSquared() < 16384.0) {
                movement = movement.normalize().multiply(0.3);
                if (PetCapture.world(dragon).isSpaceEmpty(dragon, dragon.getBoundingBox().offset(movement))) {
                    dragon.setYaw((float) Math.toDegrees(Math.atan2(movement.x, -movement.z)));
                    dragon.setPosition(dragon.getX() + movement.x, dragon.getY() + movement.y, dragon.getZ() + movement.z);
                }
            }
        }
        //#if MC>=12104
        //$$ dragon.frameTracker.tick(dragon.getY(), dragon.getYaw());
        //#else
        if (dragon.latestSegment < 0) {
            for (double[] segment : dragon.segmentCircularBuffer) {
                segment[0] = dragon.getYaw();
                segment[1] = dragon.getY();
            }
        }
        dragon.latestSegment = (dragon.latestSegment + 1) % dragon.segmentCircularBuffer.length;
        dragon.segmentCircularBuffer[dragon.latestSegment][0] = dragon.getYaw();
        dragon.segmentCircularBuffer[dragon.latestSegment][1] = dragon.getY();
        //#endif
        net.minecraft.entity.boss.dragon.EnderDragonPart[] parts = dragon.getBodyParts();
        double sine = Math.sin(Math.toRadians(dragon.getYaw()));
        double cosine = Math.cos(Math.toRadians(dragon.getYaw()));
        for (int index = 0; index < parts.length; index++) {
            double forward = switch (index) { case 0 -> -6.0; case 1 -> -3.0; case 3 -> 4.0; case 4 -> 7.0; case 5 -> 10.0; default -> 0.0; };
            double sideways = index == 6 ? 4.0 : index == 7 ? -4.0 : 0.0;
            parts[index].setPosition(dragon.getX() - sine * forward + cosine * sideways, dragon.getY(), dragon.getZ() + cosine * forward + sine * sideways);
        }
        ci.cancel();
    }
}
