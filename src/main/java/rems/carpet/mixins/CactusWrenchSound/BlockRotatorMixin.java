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

package rems.carpet.mixins.CactusWrenchSound;

import carpet.helpers.BlockRotator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;

@Mixin(BlockRotator.class)
public class BlockRotatorMixin {
    @Inject(
            method = "flipBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
            )
    )
    private static void flip_block(BlockState state, World world, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<Boolean> cir) {
        if (REMSSettings.cactusWrenchSound) {
            //#if MC<12100
            player.playSound(SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.AMBIENT,1.0F, 1.0F);
            //#else
            //$$ player.playSoundToPlayer(SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.AMBIENT,1.0F, 1.0F);
            //#endif
        }
    }
}