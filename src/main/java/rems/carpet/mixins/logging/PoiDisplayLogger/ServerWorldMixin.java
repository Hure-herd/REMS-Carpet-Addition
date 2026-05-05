/*
 * This file is part of the REMS-Carpet-Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 A Minecraft Server and contributors
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

package rems.carpet.mixins.logging.PoiDisplayLogger;

import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Unique
    private static final Map<UUID, Map<BlockPos, Integer>> FAKE_MARKERS = new HashMap<>();

    @Unique
    private EntitySpawnS2CPacket createFakeSpawnPacket(DisplayEntity.BlockDisplayEntity entity) {
        return new EntitySpawnS2CPacket(
                entity.getId(), entity.getUuid(),
                entity.getX(), entity.getY(), entity.getZ(),
                entity.getPitch(), entity.getYaw(),
                entity.getType(), 0, entity.getVelocity(), entity.getHeadYaw()
        );
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void renderFakePurpleGlass(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {

        ServerWorld world = (ServerWorld) (Object) this;
        FAKE_MARKERS.keySet().removeIf(uuid -> world.getServer().getPlayerManager().getPlayer(uuid) == null);
        if (world.getServer().getTicks() % 3 != 0) return;

        for (ServerPlayerEntity player : world.getPlayers()) {
            UUID playerId = player.getUuid();
            Map<BlockPos, Integer> activeMarkers = FAKE_MARKERS.computeIfAbsent(playerId, k -> new HashMap<>());
            Logger poiLogger = LoggerRegistry.getLogger("poi");
            poiLogger.log((option) -> {
                boolean isFull = "full".equals(option);
                Map<BlockPos, BlockState> targetMarkers = new HashMap<>();

                if (isFull || "portal".equals(option)) {
                    world.getPointOfInterestStorage().getInSquare(
                            poiType -> poiType.matchesKey(PointOfInterestTypes.NETHER_PORTAL),
                            player.getBlockPos(),
                            40,
                            PointOfInterestStorage.OccupationStatus.ANY
                    ).forEach(poi -> targetMarkers.put(poi.getPos(), Blocks.PURPLE_STAINED_GLASS.getDefaultState()));
                }

                if (isFull || "village".equals(option)) {
                    world.getPointOfInterestStorage().getInSquare(
                            poiType -> poiType.isIn(PointOfInterestTypeTags.VILLAGE),
                            player.getBlockPos(),
                            64,
                            PointOfInterestStorage.OccupationStatus.IS_OCCUPIED
                    ).forEach(poi -> {
                        targetMarkers.put(poi.getPos(), Blocks.RED_STAINED_GLASS.getDefaultState());
                    });
                }

                if (isFull || "bee_home".equals(option)) {
                    world.getPointOfInterestStorage().getInSquare(
                            poiType -> poiType.isIn(PointOfInterestTypeTags.BEE_HOME),
                            player.getBlockPos(),
                            40,
                            PointOfInterestStorage.OccupationStatus.ANY
                    ).forEach(poi -> targetMarkers.put(poi.getPos(), Blocks.YELLOW_STAINED_GLASS.getDefaultState()));
                }
                List<Integer> entitiesToRemove = new ArrayList<>();
                activeMarkers.entrySet().removeIf(entry -> {
                    if (!targetMarkers.containsKey(entry.getKey())) {
                        entitiesToRemove.add(entry.getValue());
                        return true;
                    }
                    return false;
                });
                if (!entitiesToRemove.isEmpty()) {
                    player.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(
                            entitiesToRemove.stream().mapToInt(i -> i).toArray()
                    ));
                }
                for (Map.Entry<BlockPos, BlockState> entry : targetMarkers.entrySet()) {
                    BlockPos pos = entry.getKey();
                    BlockState renderState = entry.getValue();

                    if (!activeMarkers.containsKey(pos)) {
                        DisplayEntity.BlockDisplayEntity fakeDisplay = new DisplayEntity.BlockDisplayEntity(EntityType.BLOCK_DISPLAY, world);
                        fakeDisplay.setPos(pos.getX(), pos.getY(), pos.getZ());
                        fakeDisplay.setBlockState(renderState);
                        fakeDisplay.setGlowing(true);
                        fakeDisplay.setTransformation(new AffineTransformation(
                                new Vector3f(0.25f, 0.25f, 0.25f),
                                new Quaternionf(),
                                new Vector3f(0.5f, 0.5f, 0.5f),
                                new Quaternionf()
                        ));

                        player.networkHandler.sendPacket(createFakeSpawnPacket(fakeDisplay));
                        var entityData = fakeDisplay.getDataTracker().getChangedEntries();
                        if (entityData != null) {
                            player.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(fakeDisplay.getId(), entityData));
                        }

                        activeMarkers.put(pos, fakeDisplay.getId());
                    }
                }

                return null;
            });
        }
    }
}