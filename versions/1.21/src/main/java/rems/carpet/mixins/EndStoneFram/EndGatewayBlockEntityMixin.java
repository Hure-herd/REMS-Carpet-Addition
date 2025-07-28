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

package rems.carpet.mixins.EndStoneFram;

import com.mojang.logging.LogUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.EndConfiguredFeatures;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;


@Mixin(EndGatewayBlockEntity.class)
public class EndGatewayBlockEntityMixin extends EndPortalBlockEntity {

    public EndGatewayBlockEntityMixin(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityType.END_GATEWAY, blockPos, blockState);
    }

    @Shadow
    private static final Logger LOGGER = LogUtils.getLogger();
    @Shadow
    private static WorldChunk getChunk(World world, Vec3d pos) {
        return world.getChunk(MathHelper.floor(pos.x / (double)16.0F), MathHelper.floor(pos.z / (double)16.0F));
    }

    @Inject(method = "setupExitPortalLocation",at= @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/EndGatewayBlockEntity;findPortalPosition(Lnet/minecraft/world/chunk/WorldChunk;)Lnet/minecraft/util/math/BlockPos;"))
    private static void setupExitPortalLocation(ServerWorld world, BlockPos pos, CallbackInfoReturnable<BlockPos> ci) {
        Vec3d vec3d = EndGatewayBlockEntity.findTeleportLocation(world, pos);
        WorldChunk worldChunk = getChunk(world, vec3d);
        BlockPos blockPos = EndGatewayBlockEntity.findPortalPosition(worldChunk);
        if(REMSSettings.endstonefram){
            if(pos.getZ() >751 && pos.getZ() <768 && pos.getX() < 176 && pos.getX() > 159){
                BlockPos blockPos2 = BlockPos.ofFloored(vec3d.x + (double)0.5F, (double)75.0F, vec3d.z + (double)0.5F);
                LOGGER.debug("Failed to find a suitable block to teleport to, spawning an island on {}", blockPos2);
                world.getRegistryManager().getOptional(RegistryKeys.CONFIGURED_FEATURE).flatMap((registry) -> registry.getEntry(EndConfiguredFeatures.END_ISLAND)).ifPresent((reference) -> ((ConfiguredFeature)reference.value()).generate(world, world.getChunkManager().getChunkGenerator(), Random.create(blockPos2.asLong()), blockPos2));
                blockPos = blockPos2;
            }
        }
    }

    @Redirect(
            method = "getOrCreateExitPortalPos",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/EndGatewayBlockEntity;createPortal(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/EndGatewayFeatureConfig;)V"
            ))
    private void onCreatePortal(
            ServerWorld world, BlockPos pos, EndGatewayFeatureConfig config
    ) {
        if (REMSSettings.endstonefram) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(!(blockEntity == null)){
                int absX = Math.abs(pos.getX());
                int absZ = Math.abs(pos.getZ());
                EndGatewayBlockEntity.createPortal(world, pos, config);
                if(absZ <810 && absX < 810){
                    world.removeBlockEntity(pos);
                }
            }else{
                EndGatewayBlockEntity.createPortal(world, pos, config);
            }
        } else {
            EndGatewayBlockEntity.createPortal(world, pos, config);
        }
    }
}


