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

package rems.carpet;


import static rems.carpet.utils.REMSRuleCategory.*;
import carpet.api.settings.Rule;


public class REMSSettings
{

    @Rule(
            categories = {REMS,EXPERIMENTAL,OPTIMIZATION},
            options = {"0","200"},
            strict = false
    )
    public static int projectileRaycastLength = 0;

    @Rule(
            categories = {REMS, FEATURE}
    )
    public static boolean chestMinecartChunkLoader = false;

    //#if MC<12102
    @Rule(
           categories = {REMS, FEATURE}
    )
    public static boolean PortalPearlWarp = false;
    //#endif

    @Rule(
            categories = {REMS, SURVIVAL}
    )
    public static boolean pistonBlockChunkLoader =  false;

    @Rule(
            categories = {REMS, SURVIVAL}
    )
    public static boolean pearlTickets =  false;

    @Rule(
            categories = {REMS, SURVIVAL}
    )
    public static boolean pearlPosVelocity = false;

    @Rule(
            categories = {REMS, FEATURE}
    )
    public static boolean keepWorldTickUpdate = false;

    @Rule(
            categories = {REMS, FEATURE, SURVIVAL}
    )
    public static boolean scheduledRandomTickPlants = false;

    @Rule(
            categories = {REMS, FEATURE, SURVIVAL,TNT}
    )
    public static boolean mergeTNTPro = false;

    @Rule(
            categories = {REMS, FEATURE}
    )
    public static boolean disableBatCanSpawn = false;

    @Rule(
            categories = {REMS, CREATIVE, SURVIVAL}
    )
    public static boolean cactusWrenchSound = false;

    @Rule(
            categories = {REMS, CREATIVE}
    )
    public static boolean disablePortalUpdate = false;
    //#if MC<121
    @Rule(
            categories = {REMS, SURVIVAL}
    )
    public static boolean endGatewayChunkLoader = false;
    //#endif

    @Rule(
            categories = {REMS, EXPERIMENTAL}
    )
    public static boolean itemShadowing = false;

    @Rule(
            categories = {REMS, FEATURE, SURVIVAL}
    )
    public static boolean sharedVillagerDiscounts = false;

    // @Rule(
    //         categories = {REMS, EXPERIMENTAL}
    // )
    // public static boolean teleportToPoiWithoutPortals = false;

    @Rule(
            categories = {REMS, SURVIVAL}
    )
    public static boolean SignCommand = false;

    //#if MC<12102
    @Rule(
            categories = {REMS,FEATURE},
            options = {"0","40"},
            strict = false
    )
    public static int Pearltime = 40;

    @Rule(
            categories = {REMS, FEATURE}
    )
    public static boolean enderpearlloadchunk = false;
    //#endif

    //#if MC>=12006
    //$$ @Rule(
    //$$        categories = {REMS, FEATURE}
    //$$ )
    //$$ public static boolean ComparatorIgnoresStateUpdatesFromBelow = false;
    //#endif

    //#if MC>=12101
    //$$ @Rule(
    //$$        categories = {REMS, FEATURE}
    //$$ )
    //$$ public static boolean soundsuppression = false;
    //#endif

    //#if MC>=12102
    //$$ @Rule(
    //$$ categories = {REMS, FEATURE, SURVIVAL}
    //$$ )
    //$$ public static boolean stringDupeReintroduced = false;
    //#endif

    //#if MC>=12006
    //$$ @Rule(
    //$$ categories = {REMS,EXPERIMENTAL}
    //$$ )
    //$$ public static boolean magicBox = false;
    //#endif
}
