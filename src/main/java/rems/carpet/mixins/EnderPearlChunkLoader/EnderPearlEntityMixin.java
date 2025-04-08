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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;
import top.byteeeee.annotationtoolbox.annotation.GameVersion;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@GameVersion(version = "Minecraft < 1.21.3")
@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlEntityMixin extends ThrownItemEntity {

    @Shadow public abstract void tick();

    private boolean sync = true;
    private Vec3d realPos = null;
    private Vec3d realVelocity = null;
    private int tick = 1;

    protected EnderPearlEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    private static final ChunkTicketType<ChunkPos> ENDER_PEARL_TICKET =
            ChunkTicketType.create("ender_pearl", Comparator.comparingLong(ChunkPos::toLong), 2);

    private static final ChunkTicketType<ChunkPos> ENDER_PEARL_TICKETS =
            ChunkTicketType.create("ender_pearl", Comparator.comparingLong(ChunkPos::toLong), 10);

    private static boolean isEntityTickingChunk(WorldChunk chunk) {
        //#if MC<12001
        return (chunk != null && chunk.getLevelType() == ChunkHolder.LevelType.ENTITY_TICKING);
        //#else
        //$$ return (chunk != null && chunk.getLevelType() == ChunkLevelType.ENTITY_TICKING);
        //#endif
    }

    private static int getHighestMotionBlockingY(NbtCompound nbtCompound) {
        int highestY = Integer.MIN_VALUE;
        if (REMSSettings.pearlTickets && nbtCompound != null) {
            for (long element : nbtCompound.getCompound("Heightmaps").getLongArray("MOTION_BLOCKING")) {
                for (int i = 0; i < 7; i++) {
                    //#if MC<12101
                    int y = (int)(element & 0b111111111) - 1;
                    //#else
                    //$$ int y = (int)(element & 0b111111111);
                    //#endif
                    if (y > highestY) highestY = y;
                    element = element >> 9;
                }
            }
        }
        return highestY;
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void skippyChunkLoading(CallbackInfo ci) {
        World world = this.getEntityWorld();

        if (REMSSettings.pearlTickets && world instanceof ServerWorld) {
            Vec3d currPos = this.getPos().add(Vec3d.ZERO);
            Vec3d currVelocity = this.getVelocity().add(Vec3d.ZERO);

            if (this.sync) {
                this.realPos = currPos;
                this.realVelocity = currVelocity;
            }
            //#if MC<12101
            Vec3d nextPos = this.realPos.add(this.realVelocity);
            Vec3d nextVelocity = this.realVelocity.multiply(0.99F).subtract(0, this.getGravity(), 0);
            //#else
            //$$ Vec3d nextVelocity = this.realVelocity.multiply(0.99F).subtract(0, 0.0297, 0);
            //$$ Vec3d nextPos = this.realPos.add(nextVelocity);
            //#endif
            ChunkPos currChunkPos = new ChunkPos(new BlockPos((int)currPos.x, (int)currPos.y, (int)currPos.z));
            ChunkPos nextChunkPos = new ChunkPos(new BlockPos((int)nextPos.x, (int)nextPos.y, (int)nextPos.z));
            ServerChunkManager serverChunkManager = ((ServerWorld) world).getChunkManager();

            if (!this.sync || !isEntityTickingChunk(serverChunkManager.getWorldChunk(nextChunkPos.x, nextChunkPos.z))) {
                NbtCompound nbtCompound1;
                NbtCompound nbtCompound2;
                try {
                    //#if MC<12101
                    nbtCompound1 = serverChunkManager.threadedAnvilChunkStorage.getNbt(currChunkPos).get().orElse(null);
                    nbtCompound2 = serverChunkManager.threadedAnvilChunkStorage.getNbt(nextChunkPos).get().orElse(null);
                    //#else
                    //$$ nbtCompound1 = serverChunkManager.chunkLoadingManager.getNbt(currChunkPos).get().orElse(null);
                    //$$ nbtCompound2 = serverChunkManager.chunkLoadingManager.getNbt(nextChunkPos).get().orElse(null);
                    //#endif
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException("NbtCompound exception");
                }

                int highestMotionBlockingY = Integer.max(getHighestMotionBlockingY(nbtCompound1), getHighestMotionBlockingY(nbtCompound2));
                DimensionType worldDimension = world.getDimension();
                highestMotionBlockingY += worldDimension.minY();
                PlayerEntity owner = (PlayerEntity) this.getOwner();
                if(this.tick == 1){
                    serverChunkManager.addTicket(ENDER_PEARL_TICKETS, nextChunkPos, 2, currChunkPos);
                    serverChunkManager.addTicket(ENDER_PEARL_TICKETS, currChunkPos, 2, currChunkPos);
                    this.tick =2;
                }

                if (this.realPos.y > highestMotionBlockingY
                        && nextPos.y > highestMotionBlockingY
                        && nextPos.y + nextVelocity.y > highestMotionBlockingY) {
                        serverChunkManager.addTicket(ENDER_PEARL_TICKETS, currChunkPos, 2, currChunkPos);
                        this.setVelocity(Vec3d.ZERO);
                        this.setPosition(currPos);
                        this.sync = false;
                        if(REMSSettings.pearlPosVelocity){
                        owner.sendMessage(Text.of("EnderPearlY" + realPos), false);
                        owner.sendMessage(Text.of("EnderPearlV" + realVelocity), false);}
                } else {
                    serverChunkManager.addTicket(ENDER_PEARL_TICKET, nextChunkPos, 2, nextChunkPos);
                    this.setVelocity(this.realVelocity);
                    this.setPosition(this.realPos);
                    this.sync = true;
                    this.tick =1;
                }
                this.realPos = nextPos;
                this.realVelocity = nextVelocity;
            }
        }
    }
}

