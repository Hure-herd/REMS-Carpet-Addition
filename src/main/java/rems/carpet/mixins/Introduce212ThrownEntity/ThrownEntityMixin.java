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
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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


    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        if (REMSSettings.introduceHighVersionThrowableEntityMovement) {

            // 1. 1.21.2 新增：初始气泡柱碰撞 (修复了 iterate(Box) 报错)
            if (this.firstUpdate) {
                Box box = this.getBoundingBox();
                int minX = MathHelper.floor(box.minX);
                int minY = MathHelper.floor(box.minY);
                int minZ = MathHelper.floor(box.minZ);
                int maxX = MathHelper.floor(box.maxX);
                int maxY = MathHelper.floor(box.maxY);
                int maxZ = MathHelper.floor(box.maxZ);

                for (BlockPos blockPos : BlockPos.iterate(minX, minY, minZ, maxX, maxY, maxZ)) {
                    BlockState blockState = this.getWorld().getBlockState(blockPos);
                    if (blockState.isOf(Blocks.BUBBLE_COLUMN)) {
                        blockState.onEntityCollision(this.getWorld(), blockPos, this);
                    }
                }
            }

            // 2. 1.21.2 物理重构核心：【先应用重力】
            Vec3d velocity = this.getVelocity();
            if (!this.hasNoGravity()) {
                velocity = new Vec3d(velocity.x, velocity.y - (double)this.getGravity(), velocity.z);
                this.setVelocity(velocity);
            }

            // 3. 1.21.2 物理重构核心：【再应用阻力】
            float drag;
            if (this.isTouchingWater()) {
                Vec3d pos = this.getPos();
                for (int i = 0; i < 4; ++i) {
                    this.getWorld().addParticle(ParticleTypes.BUBBLE,
                            pos.x - velocity.x * 0.25D, pos.y - velocity.y * 0.25D, pos.z - velocity.z * 0.25D,
                            velocity.x, velocity.y, velocity.z);
                }
                drag = 0.8F;
            } else {
                drag = 0.99F;
            }
            velocity = velocity.multiply((double)drag);
            this.setVelocity(velocity);

            // 4. 用扣除了重力和阻力的【最终速度】进行碰撞预测
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);

            // 5. 更新位置：如果预判会撞到，就把抛射物精准吸附到碰撞点；否则按速度前进
            Vec3d nextPos;
            if (hitResult.getType() != HitResult.Type.MISS) {
                nextPos = hitResult.getPos();
            } else {
                nextPos = this.getPos().add(velocity);
            }
            this.setPosition(nextPos.x, nextPos.y, nextPos.z);
            this.updateRotation();

            // 6. 1.21.2 时序重构核心：移动位置后，【立刻进行方块碰撞检测】 (解决 1gt 延迟踩压力板的问题)
            this.checkBlockCollision();

            // 7. 调用父类 Entity.tick() 处理实体的基础生命周期 (如着火、下落时间等)
            super.tick();

            // 8. 最后处理击中逻辑：实体/方块击碎、反弹、或是低版本兼容的传送门逻辑
            if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
                //#if MC<12100
                boolean inPortal = false;
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
                    BlockState blockState = this.getWorld().getBlockState(blockPos);

                    if (blockState.isOf(Blocks.NETHER_PORTAL)) {
                        this.setInNetherPortal(blockPos);
                        inPortal = true;
                    } else if (blockState.isOf(Blocks.END_GATEWAY)) {
                        BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
                        if (blockEntity instanceof EndGatewayBlockEntity && EndGatewayBlockEntity.canTeleport(this)) {
                            EndGatewayBlockEntity.tryTeleportingEntity(this.getWorld(), blockPos, blockState, this, (EndGatewayBlockEntity)blockEntity);
                        }
                        inPortal = true;
                    }
                }
                // 如果不是传送门，就正常触发原版的撞击碎裂逻辑
                if (!inPortal) {
                    this.onCollision(hitResult);
                }
                //#else
                //$$ // 高版本直接调用 hitOrDeflect (整合了偏转和传送门逻辑)
                //$$ this.hitOrDeflect(hitResult);
                //#endif
            }

            // 拦截掉原版的 tick 逻辑
            ci.cancel();
        }
    }
}
