/*
 * This file is part of the Carpet REMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 A Minecraft Server and contributors
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

package rems.carpet.utils.NoSensationPearlLoad;

import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClearPearTrail {
    public static final Map<String, List<Vec3d>> PATH_CACHE = new ConcurrentHashMap<>();
    public static final Map<String, List<Vec3d>> VELOCITY_CACHE = new ConcurrentHashMap<>();
    public static final Map<String, HitResult> HIT_CACHE = new ConcurrentHashMap<>();

    public static void clearAll() {
        PATH_CACHE.clear();
        VELOCITY_CACHE.clear();
        HIT_CACHE.clear();
    }
}