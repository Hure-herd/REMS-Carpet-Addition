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

package rems.carpet.mixins.TeleportToPoiWithoutPortals;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.Overwrite;
import rems.carpet.REMSSettings;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.poi.PointOfInterest;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Comparator;
import java.util.Optional;

@Mixin(value = PortalForcer.class, priority = 2147483647)
public class PortalForcerVanillaMixin {
    @Shadow @Final private ServerWorld world;

    /**
     * @author Hureherd
     * @reason Restore POI gateless teleportation in Vanilla.
     */
    @WrapMethod(method = "getPortalRect")
    public Optional<BlockPos> getPortalRect(BlockPos centerPos, boolean dstIsNether, WorldBorder worldBorder, Operation<Optional<BlockLocating.Rectangle>> original) {
        int searchRadius = dstIsNether ? 16 : 128;
        PointOfInterestStorage poiStorage = this.world.getPointOfInterestStorage();
        poiStorage.preloadChunks(this.world, centerPos, searchRadius);
        Optional<PointOfInterest> optional = poiStorage.getInSquare(
                        poiType -> poiType.matchesKey(PointOfInterestTypes.NETHER_PORTAL), centerPos, searchRadius, PointOfInterestStorage.OccupationStatus.ANY
                ).filter(PointOfInterest -> worldBorder.contains(PointOfInterest.getPos()))
                .filter(PointOfInterest -> {
                    if (REMSSettings.teleportToPoiWithoutPortals)return true;
                    return this.world.getBlockState(PointOfInterest.getPos()).contains(Properties.HORIZONTAL_AXIS);
                })
                .min(Comparator.comparingDouble((PointOfInterest PointOfInterest) -> PointOfInterest.getPos().getSquaredDistance(centerPos))
                        .thenComparingInt(PointOfInterest -> PointOfInterest.getPos().getY()));
        return optional.map(PointOfInterest::getPos);
    }
}
