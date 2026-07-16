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

package rems.carpet.mixins.FragileBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    /**
     * Makes blocks in the fragileBlocks list break much faster by multiplying
     * the player's block breaking speed when targeting those blocks.
     */
    @Inject(
            method = "getBlockBreakingSpeed",
            at = @At("RETURN"),
            cancellable = true
    )
    private void fragileBlocks(BlockState state, CallbackInfoReturnable<Float> cir) {
        if (!REMSSettings.fragileBlocks.equals("false")) {
            Block block = state.getBlock();
            if (REMSSettings.FRAGILE_BLOCKS.contains(block)) {
                cir.setReturnValue(cir.getReturnValue() * 1000.0F);
            }
        }
    }
}
