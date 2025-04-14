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

package rems.carpet.mixins.TeleportToPoiWithoutPortals;

import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.Overwrite;
import rems.carpet.REMSSettings;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.poi.PointOfInterest;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

@Mixin(value = PortalForcer.class,priority = 200000)
public class PortalForcerMixin
{
    @Shadow
    @Final
    private ServerWorld world;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Optional<BlockLocating.Rectangle> getPortalRect(BlockPos pos, boolean destIsNether, WorldBorder worldBorder) {
        PointOfInterestStorage poiStorage = this.world.getPointOfInterestStorage();
        int searchRadius = destIsNether ? 16 : 128;
        poiStorage.preloadChunks(this.world, pos, searchRadius);
        Stream<PointOfInterest> poiStream = poiStorage.getInSquare(
                poiType -> poiType.matchesKey(PointOfInterestTypes.NETHER_PORTAL),
                pos,
                searchRadius,
                PointOfInterestStorage.OccupationStatus.ANY
        );
        if (!REMSSettings.teleportToPoiWithoutPortals) {
            poiStream = poiStream.filter(poi ->
                    this.world.getBlockState(poi.getPos()).contains(Properties.HORIZONTAL_AXIS)
            );
        }
        Optional<PointOfInterest> optionalPoi = poiStream
                .filter(poi -> worldBorder.contains(poi.getPos()))
                .sorted(Comparator.<PointOfInterest>comparingDouble(poi ->
                        poi.getPos().getSquaredDistance(pos)
                ).thenComparingInt(poi ->
                        poi.getPos().getY()
                ))
                .findFirst();

        if (!optionalPoi.isPresent()) {
            return Optional.empty();
        }
        PointOfInterest poi = optionalPoi.get();
        BlockPos poiPos = poi.getPos();
        this.world.getChunkManager().addTicket(
                ChunkTicketType.PORTAL,
                new ChunkPos(poiPos),
                3,
                poiPos
        );
        if (REMSSettings.teleportToPoiWithoutPortals) {
            return Optional.of(new BlockLocating.Rectangle(poiPos, 1, 1));
        } else {
            BlockState blockState = this.world.getBlockState(poiPos);
            Direction.Axis axis = blockState.get(Properties.HORIZONTAL_AXIS);
            return Optional.of(BlockLocating.getLargestRectangle(
                    poiPos,
                    axis,
                    21,
                    Direction.Axis.Y,
                    21,
                    testPos -> this.world.getBlockState(testPos) == blockState
            ));
        }
    }
}
