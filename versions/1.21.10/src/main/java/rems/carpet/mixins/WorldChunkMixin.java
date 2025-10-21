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

package rems.carpet.mixins;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.tick.ChunkTickScheduler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;


@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin extends Chunk {

    @Shadow public abstract World getWorld();


    public WorldChunkMixin(World world, ChunkPos pos, UpgradeData upgradeData, ChunkTickScheduler<Block> blockTickScheduler, ChunkTickScheduler<Fluid> fluidTickScheduler, long inhabitedTime, @Nullable ChunkSection[] sectionArrayInitializer, @Nullable WorldChunk.EntityLoader entityLoader, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, world, world.getPalettesFactory(), inhabitedTime, sectionArrayInitializer, blendingData);
    }

    @Inject(
            method = "setBlockState",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/WorldChunk;removeBlockEntity(Lnet/minecraft/util/math/BlockPos;)V"
            )
    )
    private void setBlockState(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<BlockState> ci){
        if ((state.isOf(Blocks.CALIBRATED_SCULK_SENSOR) && REMSSettings.soundsuppression)||((state.isOf(Blocks.LECTERN) && REMSSettings.magicBox))) {
                ci.cancel();
        }else {
            this.removeBlockEntity(pos);
        }
    }
}
