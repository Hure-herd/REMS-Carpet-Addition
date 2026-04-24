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

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;
import rems.carpet.utils.ChunkLoader.ChunkLoaderState;
import rems.carpet.utils.NoSensationPearlLoad.ClearPearTrail;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlEntityMixin extends ThrownItemEntity {

    protected EnderPearlEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }



    @Inject(method = "tick",at = @At(value = "TAIL"))
    private void loadingChunksfix(
            CallbackInfo ci,
            @Local(ordinal = 0) Entity entity
    ){
        World world = this.getEntityWorld();
        if(!REMSSettings.fixedpearlloading) return;
//        if(entity instanceof ServerPlayerEntity serverPlayerEntity){
//            if (!serverPlayerEntity.getServerWorld().entityList.has(this)) {
//                serverPlayerEntity.getServerWorld().entityList.add(this);
//            }
//        }
        if(!(world instanceof ServerWorld))return;
        Vec3d realPos = this.getEntityPos().add(Vec3d.ZERO);
        Vec3d realVelocity = this.getVelocity().add(Vec3d.ZERO);
        Vec3d nextPos = realPos.add(realVelocity);
        Vec3d nextVelocity = realVelocity.multiply(0.99F).subtract(0, 0.0297, 0);
        Vec3d nextnextPos = nextPos.add(nextVelocity);
        Vec3d nextnextVelocity = nextVelocity.multiply(0.99F).subtract(0, 0.0297, 0);
        Vec3d nextnextnextPos = nextnextPos.add(nextnextVelocity);
        ChunkPos nextChunkPos = new ChunkPos((int)nextPos.x, (int)nextPos.z);
        ChunkPos nextnextChunkPos = new ChunkPos((int)nextnextPos.x, (int)nextnextPos.z);
        ServerChunkManager serverChunkManager = ((ServerWorld) world).getChunkManager();
        if(realVelocity.x > 200 ||realVelocity.z > 200)return;
        //#if MC<12105
        //$$ serverChunkManager.addTicket(ChunkLoaderState.ENDER_PEARLS, nextChunkPos, 2, nextChunkPos);
        //$$ serverChunkManager.addTicket(ChunkLoaderState.ENDER_PEARLS, nextnextChunkPos, 2, nextnextChunkPos);
        //#else
        serverChunkManager.addTicket(ChunkLoaderState.ENDER_PEARLS, nextChunkPos, 2);
        serverChunkManager.addTicket(ChunkLoaderState.ENDER_PEARLS, nextnextChunkPos, 2);
        //#endif
    }
    //#if MC>12101
    @Inject(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/thrown/EnderPearlEntity;isAlive()Z"),cancellable = true)
    private void noloadingchunk(
            CallbackInfo ci
    ){
        if(REMSSettings.pearlnotloadingchunk){
            ci.cancel();
        }
    }
    //#endif

    @Shadow protected abstract void onCollision(HitResult hitResult);
    @Unique private String cacheKey = null;
    @Unique private boolean isReplaying = false;
    @Unique private boolean isRecording = false;
    @Unique private int replayTick = 0;
    @Unique private boolean initialized = false;
    @Unique private List<Vec3d> currentRecordingPath = null;
    @Unique private List<Vec3d> currentRecordingVelocities = null;

    @Unique
    private String generateKey(EnderPearlEntity pearl) {
        Vec3d pos = pearl.getEntityPos();
        Vec3d vel = pearl.getVelocity();
        return String.format(Locale.US, "%.3f,%.3f,%.3f|%.3f,%.3f,%.3f",
                pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTickHead(CallbackInfo ci) {
        if(!REMSSettings.noSensationPearlLoad)return;
        EnderPearlEntity pearl = (EnderPearlEntity) (Object) this;

        if (!this.initialized) {
            this.initialized = true;
            Vec3d velocity = pearl.getVelocity();

            if (Math.abs(velocity.x) > 300.0D || Math.abs(velocity.z) > 300.0D) {
                this.cacheKey = this.generateKey(pearl);

                if (ClearPearTrail.PATH_CACHE.containsKey(this.cacheKey) && ClearPearTrail.HIT_CACHE.containsKey(this.cacheKey)) {
                    this.isReplaying = true;
                } else {
                    this.isRecording = true;
                    this.currentRecordingPath = new ArrayList<>();
                    this.currentRecordingVelocities = new ArrayList<>();

                    this.currentRecordingPath.add(pearl.getEntityPos());
                    this.currentRecordingVelocities.add(velocity);
                }
            }
        }

        if (this.isReplaying) {
            List<Vec3d> cachedPath = ClearPearTrail.PATH_CACHE.get(this.cacheKey);
            List<Vec3d> cachedVels = ClearPearTrail.VELOCITY_CACHE.get(this.cacheKey);

            if (this.replayTick < cachedPath.size()) {
                Vec3d currentPos = pearl.getEntityPos();
                Vec3d nextPos = cachedPath.get(this.replayTick);

                HitResult hitCheck = pearl.getEntityWorld().raycast(new RaycastContext(
                        currentPos, nextPos,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        pearl
                ));

                if (hitCheck.getType() != HitResult.Type.MISS) {
                    ClearPearTrail.PATH_CACHE.remove(this.cacheKey);
                    ClearPearTrail.VELOCITY_CACHE.remove(this.cacheKey);
                    ClearPearTrail.HIT_CACHE.remove(this.cacheKey);

                    this.onCollision(hitCheck);
                    pearl.discard();
                    ci.cancel();
                    return;
                }

                pearl.setPosition(nextPos);
                pearl.setVelocity(cachedVels.get(this.replayTick));
                pearl.knockedBack = true;
                this.replayTick++;
                ci.cancel();
            } else {
                HitResult cachedHit = ClearPearTrail.HIT_CACHE.get(this.cacheKey);
                if (cachedHit != null) {
                    this.onCollision(cachedHit);
                }
                pearl.discard();
                ci.cancel();
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickTail(CallbackInfo ci) {
        if(!REMSSettings.noSensationPearlLoad)return;
        EnderPearlEntity pearl = (EnderPearlEntity) (Object) this;

        if (this.isRecording && !pearl.isRemoved() && this.currentRecordingPath != null) {
            this.currentRecordingPath.add(pearl.getEntityPos());
            this.currentRecordingVelocities.add(pearl.getVelocity());
        }
    }

    @Inject(method = "onCollision", at = @At("HEAD"))
    private void onCollisionHead(HitResult hitResult, CallbackInfo ci) {
        if(!REMSSettings.noSensationPearlLoad)return;
        if (this.isRecording && this.cacheKey != null && this.currentRecordingPath != null) {
            ClearPearTrail.PATH_CACHE.put(this.cacheKey, this.currentRecordingPath);
            ClearPearTrail.VELOCITY_CACHE.put(this.cacheKey, this.currentRecordingVelocities);
            ClearPearTrail.HIT_CACHE.put(this.cacheKey, hitResult);
            this.isRecording = false;
        }
    }

    //#if MC<12102
    //$$ @Unique
    //$$ private long chunkTicketExpiryTicks = 0L;
    //$$
    //$$ @Unique
    //$$ private int highSpeedAge = 0;
    //$$
    //$$ @Inject(
    //$$         method = "tick",
    //$$         at = @At("HEAD")
    //$$ )
    //$$ private void getVector(CallbackInfo ci, @Share("i") LocalIntRef i, @Share("j") LocalIntRef j) {
    //$$     i.set(getSectionCoordFloored(this.getPos().getX()));
    //$$     j.set(getSectionCoordFloored(this.getPos().getZ()));
    //$$ }
    //$$
    //$$ @Inject(
    //$$         method = "tick",
    //$$         at = @At("TAIL")
    //$$ )
    //$$ private void loadingChunks(
    //$$         CallbackInfo ci,
    //$$         @Local(ordinal = 0) Entity entity,
    //$$         @Share("i") LocalIntRef i,
    //$$         @Share("j") LocalIntRef j
    //$$ ) {
    //$$     if (!REMSSettings.enderpearlloadchunk) return;
    //$$
    //$$     if (this.isHighSpeed()) {
    //$$         ++this.highSpeedAge;
    //$$     } else {
    //$$         this.highSpeedAge = 0;
    //$$     }
    //$$     if (this.isAlive() && this.highSpeedAge > REMSSettings.Pearltime) {
    //$$         REMSServer.LOGGER.warn(
    //$$                 "The pearl(own: {}) has been in high speed for a long time and has been removed",
    //$$                 entity instanceof ServerPlayerEntity ? entity.getName().getString() : "unknown"
    //$$         );
    //$$         this.discard();
    //$$     }
    //$$
    //$$     if (this.isAlive()) {
    //$$         BlockPos blockPos = BlockPos.ofFloored(this.getPos());
    //$$         if (
    //$$                 (
    //$$                         --this.chunkTicketExpiryTicks <= 0L
    //$$                                 || i.get() != getSectionCoord(blockPos.getX())
    //$$                                 || j.get() != getSectionCoord(blockPos.getZ())
    //$$                 )
    //$$                         && entity instanceof ServerPlayerEntity serverPlayerEntity
    //$$         ) {
    //$$             this.chunkTicketExpiryTicks = this.handleThrownEnderPearl();
    //$$         }
    //$$     }
    //$$ }
    //$$
    //$$ @Unique
    //$$ private boolean isHighSpeed() {
    //$$     return Math.abs(this.getVelocity().getX()) > 20.0d || Math.abs(this.getVelocity().getZ()) > 20.0d;
    //$$ }
    //$$
    //$$ @Unique
    //$$ private long handleThrownEnderPearl() {
    //$$     if (this.getWorld() instanceof ServerWorld serverWorld) {
    //$$         ChunkPos chunkPos = this.getChunkPos();
    //$$         serverWorld.resetIdleTimeout();
    //$$         return addEnderPearlTicket(serverWorld, chunkPos) - 1L;
    //$$     } else {
    //$$         return 0L;
    //$$     }
    //$$ }
    //$$
    //$$ @Unique
    //$$ private static int getSectionCoordFloored(double coord) {
    //$$     return MathHelper.floor(coord) >> 4;
    //$$ }
    //$$
    //$$ @Unique
    //$$ private static int getSectionCoord(int coord) {
    //$$     return coord >> 4;
    //$$ }
    //$$
    //$$ @Unique
    //$$ private static long addEnderPearlTicket(ServerWorld ServerWolrd, ChunkPos chunkPos) {
    //$$     ServerWolrd.getChunkManager().addTicket(ChunkLoaderState.ENDER_PEARLS, chunkPos, 2, chunkPos);
    //$$     return ChunkLoaderState.ENDER_PEARLS.getExpiryTicks();
    //$$ }
    //#endif
}

