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

package rems.carpet.mixins.Reloadrefreshirongolem;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LargeEntitySpawnHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

import java.util.List;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends PassiveEntity {

    @Unique
    private boolean previousChunkLoaded = false;

    protected VillagerEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "writeCustomData", at = @At("HEAD"))
    private void onwriteNbt(WriteView view, CallbackInfo ci) {
        this.previousChunkLoaded = false;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void summonGolem(CallbackInfo ci) {
        if (!REMSSettings.reloadrefreshirongolem) return;
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;
        if (serverWorld.getRegistryKey() != World.END) return;
        int chunkX = (int) this.getX() >> 4;
        int chunkZ = (int) this.getZ() >> 4;
        boolean currentChunkLoaded = this.getWorld().isChunkLoaded(chunkX, chunkZ);
        if (currentChunkLoaded && !this.previousChunkLoaded) {
            Box checkBox = this.getBoundingBox().expand(3.0);
            List<VillagerEntity> nearbyVillagers = serverWorld.getEntitiesByClass(VillagerEntity.class, checkBox, v -> true);

            if (nearbyVillagers.size() >= 3) {
                Box raidBox = new Box(
                        this.getX() - 12, this.getY() - 12, this.getZ() - 12,
                        this.getX() + 12, this.getY() + 12, this.getZ() + 12
                );
                List<LivingEntity> raiders = serverWorld.getEntitiesByClass(LivingEntity.class, raidBox, entity -> {
                    return entity instanceof PillagerEntity || entity instanceof RavagerEntity || entity instanceof EvokerEntity;
                });
                VillagerEntity firstVillager = nearbyVillagers.get(0);
                if (!raiders.isEmpty()) {
                    if (firstVillager.getUuid().equals(this.getUuid())) {
                        LargeEntitySpawnHelper.trySpawnAt(EntityType.IRON_GOLEM,
                                SpawnReason.MOB_SUMMONED,
                                serverWorld, this.getBlockPos(),
                                10, 8, 6,
                                LargeEntitySpawnHelper.Requirements.IRON_GOLEM,
                                false);
                    }
                }
            }
        }
        this.previousChunkLoaded = currentChunkLoaded;
    }
}