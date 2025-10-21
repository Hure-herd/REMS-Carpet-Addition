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

package rems.carpet.utils;

import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class Soundsuppressionutils {
    public static final Set<BlockPos> suppressedPositions = new HashSet<>();

    public static final Set<BlockPos> issuppressedPositions = new HashSet<>();

    public static void mark(BlockPos pos) {suppressedPositions.add(pos.toImmutable());}

    public static boolean isSuppressed(BlockPos pos) {
        return suppressedPositions.contains(pos);
    }

    public static void clear() {
        suppressedPositions.clear();
    }

    public static void ismark(BlockPos pos) {
        issuppressedPositions.add(pos.toImmutable());
    }

    public static boolean isisSuppressed(BlockPos pos) {
        return issuppressedPositions.contains(pos);
    }

    public static void isclaer() {
        issuppressedPositions.clear();
    }
}
