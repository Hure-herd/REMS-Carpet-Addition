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
//#if MC<12101
import me.jellysquid.mods.lithium.common.util.POIRegistryEntries;
import me.jellysquid.mods.lithium.common.world.interests.PointOfInterestStorageExtended;
//#else
//$$ import net.caffeinemc.mods.lithium.common.util.POIRegistryEntries;
//$$ import net.caffeinemc.mods.lithium.common.world.interests.PointOfInterestStorageExtended;
//#endif
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.poi.PointOfInterestStorage;
import rems.carpet.REMSSettings;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.poi.PointOfInterest;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(value = PortalForcer.class, priority = 2147483647)
public class PortalForcerLithiumMixin {

    @Shadow @Final private ServerWorld world;

    /**
     * @author Hureherd
     * @reason Inheriting Lithium's underlying search optimizations and restoring POI gateless teleportation in Lithium.
     */
    @WrapMethod(method = "getPortalRect")
    public Optional<BlockPos> getPortalRect(BlockPos centerPos, boolean dstIsNether, WorldBorder worldBorder, Operation<Optional<BlockLocating.Rectangle>> original) {
        int searchRadius = dstIsNether ? 16 : 128;

        PointOfInterestStorage poiStorage = this.world.getPointOfInterestStorage();
        poiStorage.preloadChunks(this.world, centerPos, searchRadius);
        //#if MC<12100
        Optional<PointOfInterest> ret = ((PointOfInterestStorageExtended) poiStorage).findNearestForPortalLogic(
        //#else
        //$$ Optional<PointOfInterest> ret = ((PointOfInterestStorageExtended) poiStorage).lithium$findNearestForPortalLogic(
        //#endif
                centerPos, searchRadius, POIRegistryEntries.NETHER_PORTAL_ENTRY, PointOfInterestStorage.OccupationStatus.ANY,
                (poi) -> {
                    if (REMSSettings.teleportToPoiWithoutPortals)return true;
                    return this.world.getBlockState(poi.getPos()).contains(Properties.HORIZONTAL_AXIS);
                },
                worldBorder
        );
        return ret.map(PointOfInterest::getPos);
    }
}
