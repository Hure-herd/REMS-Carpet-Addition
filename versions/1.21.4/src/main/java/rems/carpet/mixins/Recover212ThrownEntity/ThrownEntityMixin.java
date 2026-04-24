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

package rems.carpet.mixins.Recover212ThrownEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.particle.ParticleTypes;
//#if MC>=12105
//$$ import net.minecraft.entity.EntityCollisionHandler;
//#endif
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

@Mixin(ThrownEntity.class)
public class ThrownEntityMixin extends ProjectileEntity {

    protected ThrownEntityMixin(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Unique
    private void tickInitialBubbleColumnCollision() {
        if (this.firstUpdate) {
            for (BlockPos blockPos : BlockPos.iterate(this.getBoundingBox())) {
                BlockState blockState = this.getWorld().getBlockState(blockPos);
                if (blockState.isOf(Blocks.BUBBLE_COLUMN)) {
                    //#if MC<12105
                    blockState.onEntityCollision(this.getWorld(), blockPos, this);
                    //#elseif MC<12109
                    //$$ blockState.onEntityCollision(this.getWorld(), blockPos, this, EntityCollisionHandler.DUMMY);
                    //#else
                    //$$ blockState.onEntityCollision(this.getEntityWorld(), blockPos, this, EntityCollisionHandler.DUMMY, true);
                    //#endif
                }
            }
        }
    }

    @Override
    protected double getGravity() {
        return 0.03;
    }

    @Unique
    private void applyDrag() {
        Vec3d vec3d = this.getVelocity();
        Vec3d vec3d2 = this.getPos();
        float g;
        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; i++) {
                float f = 0.25F;
                this.getWorld()
                        .addParticle(
                                ParticleTypes.BUBBLE, vec3d2.x - vec3d.x * 0.25, vec3d2.y - vec3d.y * 0.25, vec3d2.z - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z
                        );
            }

            g = 0.8F;
        } else {
            g = 0.99F;
        }

        this.setVelocity(vec3d.multiply(g));
    }


    @Inject(method = "tick",at = @At(value = "HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        if (REMSSettings.pre21ThrowableEntityMovement){
            super.tick();
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
                this.hitOrDeflect(hitResult);
            }
            this.tickBlockCollision();
            Vec3d vec3d = this.getVelocity();
            double d = this.getX() + vec3d.x;
            double e = this.getY() + vec3d.y;
            double f = this.getZ() + vec3d.z;
            this.updateRotation();
            float h;
            if (this.isTouchingWater()) {
                for(int i = 0; i < 4; ++i) {
                    float g = 0.25F;
                    this.getWorld().addParticle(ParticleTypes.BUBBLE, d - vec3d.x * (double)0.25F, e - vec3d.y * (double)0.25F, f - vec3d.z * (double)0.25F, vec3d.x, vec3d.y, vec3d.z);
                }

                h = 0.8F;
            } else {
                h = 0.99F;
            }

            this.setVelocity(vec3d.multiply((double)h));
            Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, vec3d2.y - this.getGravity(), vec3d2.z);
            this.setPosition(d, e, f);
            ci.cancel();
        }
    }
}
