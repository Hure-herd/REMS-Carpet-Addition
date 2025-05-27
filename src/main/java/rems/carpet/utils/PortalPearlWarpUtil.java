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

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;

public class PortalPearlWarpUtil {
    private static final ImmutableRangeSet<Double> RANGE_SET = ImmutableRangeSet.<Double>builder()
            .add(Range.open(914.0d, 916.0d))
            .add(Range.open(7323.0d, 7325.0d))
            .add(Range.open(58591.0d, 58593.0d))
            .add(Range.open(468742.0d, 468744.0d)) //地狱的地狱门位置
            .add(Range.open(29999599.0d, 29999601.0d))
            .add(Range.open(3749941.0d, 3749943.0d))
            .add(Range.open(468734.0d, 468736.0d))
            .add(Range.open(58584.0d, 58586.0d))
            .add(Range.open(7314.0d, 7316.0d)) //主世界的地狱门位置
            .build();

    public static boolean isInRange(double x, double z) {
        if (!sameSign(x, z)) {
            return false;
        }
        double absX = Math.abs(x);
        double absZ = Math.abs(z);
        if (Math.abs(absX - absZ) > 2.0d) {
            return false;
        }
        return RANGE_SET.contains(absX) && RANGE_SET.contains(absZ);
    }


    private static boolean sameSign(double a, double b) {
        return (Double.doubleToRawLongBits(a) ^ Double.doubleToRawLongBits(b)) >= 0;
    }
}
