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

package rems.carpet.mixins.MagicBox;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import rems.carpet.REMSSettings;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {
    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    @ModifyExpressionValue(
            method = "setBlockState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/BlockEntity;supports(Lnet/minecraft/block/BlockState;)Z"
            )
    )
    private boolean setBlockStateMixin(boolean original, @Local(ordinal = 0) BlockEntity blockEntity) {
        if (!REMSSettings.magicBox) {
            return original;
        }

        if (
                blockEntity instanceof LecternBlockEntity
                        && this.getBlockState(blockEntity.getPos()).getBlock() instanceof ShulkerBoxBlock
        ) {
            return true;
        } else {
            return original;
        }
    }
}
