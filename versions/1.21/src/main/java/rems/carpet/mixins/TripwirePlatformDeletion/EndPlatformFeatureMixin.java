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

package rems.carpet.mixins.TripwirePlatformDeletion;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.feature.EndPlatformFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

@Mixin(EndPlatformFeature.class)
public class EndPlatformFeatureMixin {

    @Inject(
            method = "generate(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockPos;Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void restoreClassicGeneration(ServerWorldAccess world, BlockPos pos, boolean breakBlocks, CallbackInfo ci) {
        if(!REMSSettings.allowTripwirePlatformDeletion)return;
        int i = pos.getX();
        int j = pos.getY() - 1;
        int k = pos.getZ();
        BlockPos.iterate(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2)
                .forEach(p -> world.setBlockState(p, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL));
        BlockPos.iterate(i - 2, j, k - 2, i + 2, j, k + 2)
                .forEach(p -> world.setBlockState(p, Blocks.OBSIDIAN.getDefaultState(), Block.NOTIFY_ALL));
        ci.cancel();
    }
}
