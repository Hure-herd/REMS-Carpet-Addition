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

package rems.carpet.mixins.Introduce212ThrownEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

@Mixin(ThrownEntity.class)
public abstract class ThrownEntityMixin extends ProjectileEntity {

    protected ThrownEntityMixin(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    //#if MC<12006
    protected abstract float getGravity();
    //#else
    //$$ protected abstract double getGravity();
    //#endif

    @Inject(method = "tick",at = @At(value = "HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        if (REMSSettings.introduceHighVersionThrowableEntityMovement){
            super.tick();
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            //#if MC<12100
            boolean bl = false;
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
                BlockState blockState = this.world.getBlockState(blockPos);
                if (blockState.isOf(Blocks.NETHER_PORTAL)) {
                    this.setInNetherPortal(blockPos);
                    bl = true;
                } else if (blockState.isOf(Blocks.END_GATEWAY)) {
                    BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
                    if (blockEntity instanceof EndGatewayBlockEntity && EndGatewayBlockEntity.canTeleport(this)) {
                        EndGatewayBlockEntity.tryTeleportingEntity(this.world, blockPos, blockState, this, (EndGatewayBlockEntity)blockEntity);
                    }

                    bl = true;
                }
            }
            if (hitResult.getType() != HitResult.Type.MISS && !bl) {
                this.onCollision(hitResult);
            }
            //#else
            //$$ if (hitResult.getType() != HitResult.Type.MISS) {
            //$$     this.hitOrDeflect(hitResult);
            //$$ }
            //#endif

            this.checkBlockCollision();
            Vec3d vec3d = this.getVelocity();
            Vec3d vec3d2 = this.getPos();
            if (!this.hasNoGravity()) {
                this.setVelocity(vec3d.x, vec3d.y - (double)this.getGravity(), vec3d.z);
            }
            this.updateRotation();
            float h;
            if (this.isTouchingWater()) {
                for(int i = 0; i < 4; ++i) {
                    float g = 0.25F;
                    this.world.addParticle(ParticleTypes.BUBBLE, vec3d2.x - vec3d.x * (double)0.25F, vec3d2.y - vec3d.y * (double)0.25F, vec3d2.z - vec3d.z * (double)0.25F, vec3d.x, vec3d.y, vec3d.z);
                }
                h = 0.8F;
            } else {
                h = 0.99F;
            }

            this.setVelocity(vec3d.multiply((double)h));

            double d = this.getX() + vec3d.x;
            double e = this.getY() + vec3d.y;
            double f = this.getZ() + vec3d.z;

            this.setPosition(d, e, f);

            ci.cancel();
        }
    }
}
