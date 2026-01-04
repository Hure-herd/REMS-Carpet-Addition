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

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Unique;
import rems.carpet.utils.BlockEntityReplace.SuppressionManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.NeighborUpdater;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import rems.carpet.REMSSettings;

@Mixin(NeighborUpdater.class)
public interface NeighborUpdaterMixin {
    @Inject(
            method = "tryNeighborUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/crash/CrashReport;create(Ljava/lang/Throwable;Ljava/lang/String;)Lnet/minecraft/util/crash/CrashReport;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void tryNeighborUpdate(
            World world, BlockState state, BlockPos pos, Block sourceBlock, WireOrientation orientation, boolean notify, CallbackInfo ci, Throwable throwable) {

        if (REMSSettings.blockentityreplacement) {

            int[] offsets = {-3, -2, -1, 0, 1, 2, 3};
            int[] yOffsets = {-1, 0};

            for (int dy : yOffsets) {
                for (int d : offsets) {
                    handleMarkedPos(world, pos.add(d, dy, 0));
                    handleMarkedPos(world, pos.add(0, dy, d));
                }
            }
        }
    }

    @Unique
    private static void handleMarkedPos(World world, BlockPos blockPos) {
        if (!SuppressionManager.isMarked2(blockPos)) return;
        SuppressionManager.mark(blockPos);
        if (!world.isClient && world.getServer() != null) {
            if (SuppressionManager.isMarked(SuppressionManager.getMarkedPos()) && SuppressionManager.isRestorable()) {

                BlockPos markedPos = SuppressionManager.getMarkedPos();
                if (SuppressionManager.getMarkedPos() == null)return;
                Chunk chunk = world.getChunk(markedPos);

                if (SuppressionManager.getMarkedOldState() != null) {
                    SuppressionManager.forceSetBlockState(chunk, markedPos, SuppressionManager.getMarkedOldState());
                }
                ((BlockEntityAccessor) SuppressionManager.getMarkedCapturedBE()).setRemoved(false);
                world.removeBlockEntity(markedPos);
                world.addBlockEntity(SuppressionManager.getMarkedCapturedBE());
                SuppressionManager.forceSetBlockState(chunk, markedPos, SuppressionManager.getMarkedState());
            } else {
                if (SuppressionManager.getMarkedPos() == null)return;
                world.removeBlockEntity(SuppressionManager.getMarkedPos());
            }

            SuppressionManager.clear();
            SuppressionManager.clear2();
            SuppressionManager.clears();
            SuppressionManager.setRestorable(false);
        }
    }
}