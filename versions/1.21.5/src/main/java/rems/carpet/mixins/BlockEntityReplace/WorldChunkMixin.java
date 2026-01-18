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

import rems.carpet.utils.BlockEntityReplace.SuppressionManager;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {

    @Shadow public abstract BlockEntity getBlockEntity(BlockPos pos);
    @Shadow @Final World world;

    @Shadow public abstract void removeBlockEntity(BlockPos pos);

    @Inject(
            method = "setBlockState",
            at = @At("HEAD")
    )
    private void captureBE(BlockPos pos, BlockState newState, int flags, CallbackInfoReturnable<BlockState> cir,
                           @Share("capturedBE") LocalRef<BlockEntity> capturedBE,
                           @Share("oldState") LocalRef<BlockState> oldStateRef) {

        if (!REMSSettings.blockentityreplacement) return;

        BlockEntity existingBe = this.getBlockEntity(pos);

        if (!this.world.isClient && this.world.getServer() != null) {
            if (existingBe != null && !(existingBe instanceof ComparatorBlockEntity)) {
                BlockState trueOldState = existingBe.getCachedState();
                SuppressionManager.mark2(pos);
                capturedBE.set(existingBe);
                oldStateRef.set(trueOldState);
            }
        }
        BlockEntity savedBe = capturedBE.get();
        BlockState originalState = oldStateRef.get();
        if (savedBe == null) return;
        if (savedBe instanceof ComparatorBlockEntity) return;
        if (!newState.hasBlockEntity()) {
            if (!newState.isAir()) return;
            SuppressionManager.setRestorable(true);
            SuppressionManager.posmark(pos);
            SuppressionManager.capturedBEmark(savedBe);
            SuppressionManager.statemark(newState);
            SuppressionManager.oldStatemark(originalState);
        }
    }


    @Redirect(
            method = "setBlockState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/BlockEntity;supports(Lnet/minecraft/block/BlockState;)Z"
            )
    )
    private boolean redirectSupports(BlockEntity existingBe, BlockState newState) {
        if (REMSSettings.blockentityreplacement) {
            SuppressionManager.clear();
            if (newState.hasBlockEntity()) {
                return true;
            }
        }

        return existingBe.supports(newState);
    }
    @Redirect(
            method = "loadBlockEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;hasBlockEntity()Z")
    )
    private boolean bypassValidation(BlockState instance) {
        if (REMSSettings.blockentityreplacement) {
            return true;
        }
        return instance.hasBlockEntity();
    }
}
