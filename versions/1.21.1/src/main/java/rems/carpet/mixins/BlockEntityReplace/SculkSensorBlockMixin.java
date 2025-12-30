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

package rems.carpet.mixins.BlockEntityReplace;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

@Mixin(SculkSensorBlock.class)
public abstract class SculkSensorBlockMixin extends BlockWithEntity {
    protected SculkSensorBlockMixin(Settings settings) {
        super(settings);
    }

    @WrapWithCondition(
            method = "onStateReplaced",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockWithEntity;onStateReplaced" +
                            "(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;" +
                            "Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V"
            )
    )
    private boolean removeOnStateReplacedMixin(
            BlockWithEntity instance,
            BlockState blockState,
            World world,
            BlockPos blockPos,
            BlockState newState,
            boolean b
    ) {
        return !REMSSettings.blockentityreplacement;
    }

    @Inject(
            method = "onStateReplaced",
            at = @At("TAIL")
    )
    private void onStateReplacedMixin(
            BlockState state,
            World world,
            BlockPos pos,
            BlockState newState,
            boolean moved,
            CallbackInfo ci
    ) {
        if (REMSSettings.blockentityreplacement) {
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
