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

            BlockPos blockPos;

            int[] Offsets = {-3, -2, -1, 0, 1, 2, 3};
            int[] yOffsets = {-1, 0};
            for (int dy : yOffsets) {
                for (int dx : Offsets) {
                    blockPos = pos.add(dx, dy, 0);
                    if(SuppressionManager.isMarked2(blockPos)){
                        SuppressionManager.mark(blockPos);
                        if (!world.isClient) {
                            net.minecraft.server.MinecraftServer server = world.getServer();
                            if (server != null) {
                            //    server.getPlayerManager().broadcast(
                            //            net.minecraft.text.Text.literal("§a[标记系统] 标记 " + blockPos),false);
                            }
                        }
                    }
                }
                for (int dz : Offsets) {
                    blockPos = pos.add(0, dy, dz);
                    if(SuppressionManager.isMarked2(blockPos)){
                        SuppressionManager.mark(blockPos);
                        if (!world.isClient) {
                            net.minecraft.server.MinecraftServer server = world.getServer();
                            if (server != null) {
                            //    server.getPlayerManager().broadcast(
                            //           net.minecraft.text.Text.literal("§a[标记系统] 标记 " + blockPos),false);
                            }
                        }
                    }
                }
            }
            SuppressionManager.clear2();
            if (!world.isClient && world.getServer() != null) {
                if(SuppressionManager.getMarkedPos() == null)return;
                if (SuppressionManager.isMarked(SuppressionManager.getMarkedPos()) && SuppressionManager.isRestorable()) {
                    Chunk chunk = world.getChunk(SuppressionManager.getMarkedPos());
                    if (SuppressionManager.getMarkedOldState() != null) {
                        SuppressionManager.forceSetBlockState(chunk,SuppressionManager.getMarkedPos(), SuppressionManager.getMarkedOldState());
                    }
                    ((BlockEntityAccessor) SuppressionManager.getMarkedCapturedBE()).setRemoved(false);
                    world.removeBlockEntity(SuppressionManager.getMarkedPos());
                    world.addBlockEntity(SuppressionManager.getMarkedCapturedBE());
                    SuppressionManager.forceSetBlockState(chunk,SuppressionManager.getMarkedPos(),SuppressionManager.getMarkedState());
                    String beName = SuppressionManager.getMarkedOldState().getBlock().getName().getString();
                    //world.getServer().getPlayerManager().broadcast(
                    //        Text.literal("§a[延迟恢复] 成功复活 BE @ " + SuppressionManager2.getMarkedPos().toShortString()+ "§a BE @ " + beName), false);
                    SuppressionManager.clear();
                }else {
                    world.removeBlockEntity(SuppressionManager.getMarkedPos());
                }
                SuppressionManager.clears();
                SuppressionManager.setRestorable(false);
            }
        }
    }
}