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

package rems.carpet.mixins.MergeTNTMax;

import carpet.CarpetSettings;
import carpet.logging.LoggerRegistry;
import carpet.logging.logHelpers.TNTLogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;
import rems.carpet.interfaces.TntEntityInterface;


@Mixin(TntEntity.class)
public abstract class TntEntityMixin extends Entity implements TntEntityInterface {
    @Shadow public abstract int getFuse();
    @Unique
    private TNTLogHelper logHelper;
    @Unique
    private int mergedTNTMax = 1;
    @Unique
    private long creationTick;

    public TntEntityMixin(EntityType<?> entityType_1, World world_1)
    {
        super(entityType_1, world_1);
        this.creationTick = world_1.getTime();
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/entity/LivingEntity;)V",
            at = @At("RETURN"))
    private void modifyTNTAngle(World world, double x, double y, double z, LivingEntity entity, CallbackInfo ci)
    {
        if (CarpetSettings.hardcodeTNTangle != -1.0D){
            setVelocity(-Math.sin(CarpetSettings.hardcodeTNTangle) * 0.02, 0.2, -Math.cos(CarpetSettings.hardcodeTNTangle) * 0.02);
        }
        this.creationTick = world.getTime();
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("RETURN"))
    private void initTNTLoggerPrime(EntityType<? extends TntEntity> entityType_1, World world_1, CallbackInfo ci)
    {
        if (LoggerRegistry.__tnt && !world_1.isClient())
        {
            logHelper = new TNTLogHelper();
        }
        this.creationTick = world_1.getTime();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void initTracker(CallbackInfo ci)
    {
        if (LoggerRegistry.__tnt && logHelper != null && !logHelper.initialized)
        {
            logHelper.onPrimed(getX(), getY(), getZ(), getVelocity());
        }
    }


    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/entity/LivingEntity;)V",
            at = @At(value = "RETURN"))
    private void initTNTLogger(World world_1, double double_1, double double_2, double double_3,
                               LivingEntity livingEntity_1, CallbackInfo ci)
    {
        if(CarpetSettings.tntPrimerMomentumRemoved){
            this.setVelocity(new Vec3d(0.0, 0.20000000298023224D, 0.0));
        }
        this.creationTick = world_1.getTime();
    }

    @Inject(method = "explode", at = @At(value = "HEAD"))
    private void onExplode(CallbackInfo ci) {
        if (LoggerRegistry.__tnt && logHelper != null){
            logHelper.onExploded(getX(), getY(), getZ(), this.getEntityWorld().getTime());
        }

        if (mergedTNTMax > 1)
            for (int i = 0; i < mergedTNTMax - 1; i++){
                this.getEntityWorld().createExplosion(this, this.getX(), this.getBodyY(0.0625),
                        this.getZ(), 4.0F, World.ExplosionSourceType.TNT);
            }
    }


    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tryMergeTnT(CallbackInfo ci)
    {
        if (!REMSSettings.mergeTNTMax) return;
        if (this.isRemoved() || getEntityWorld().isClient()) return;

        Vec3d velocity = this.getVelocity();
        int currentFuse = this.getFuse();

        for(Entity entity : getEntityWorld().getOtherEntities(this, this.getBoundingBox())){
            if(entity instanceof TntEntity && !entity.isRemoved()){
                TntEntity entityTNTPrimed = (TntEntity)entity;
                Vec3d tntVelocity = entityTNTPrimed.getVelocity();
                int targetFuse = entityTNTPrimed.getFuse();
                long targetCreationTick = ((TntEntityMixin) (Object) entityTNTPrimed).creationTick;

                if(this.squaredDistanceTo(entityTNTPrimed) < 0.0001
                        && velocity.squaredDistanceTo(tntVelocity) < 0.0001
                        && (REMSSettings.mergeTNTFuseThreshold == 0 || currentFuse <= REMSSettings.mergeTNTFuseThreshold)
                        && currentFuse == targetFuse){

                    mergedTNTMax += ((TntEntityInterface) entityTNTPrimed).getMergedTNTMax();
                    entityTNTPrimed.remove(RemovalReason.DISCARDED);
                }
            }
        }
    }

    @Override
    public int getMergedTNTMax() {
        return mergedTNTMax;
    }
}
