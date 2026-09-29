/*
 * This file is part of the REMS-Carpet-Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 Hureherd and contributors
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

package rems.carpet.utils.FixedPearlLoading;

import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Set;

public final class RetainedPearlIndex {
    private static final Set<EnderPearlEntity> PEARLS =
            java.util.Collections.newSetFromMap(new IdentityHashMap<>());

    private RetainedPearlIndex() {
    }

    public static void retain(EnderPearlEntity pearl) {
        PEARLS.add(pearl);
    }

    public static void forget(EnderPearlEntity pearl) {
        PEARLS.remove(pearl);
    }

    public static Collection<EnderPearlEntity> get(ServerWorld world) {
        Collection<EnderPearlEntity> result = new ArrayList<>();
        for (EnderPearlEntity pearl : PEARLS) {
            if (!pearl.isRemoved() && pearl.getWorld() == world) {
                result.add(pearl);
            }
        }
        return result;
    }
}
