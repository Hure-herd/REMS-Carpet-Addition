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

package rems.carpet.mixins.PortalPearlWarp;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Blocks;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.injection.Redirect;
import rems.carpet.REMSSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.utils.PortalPearlWarpUtil;


import java.util.Objects;

import static net.minecraft.particle.ParticleTypes.PORTAL;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlTeleportMixin {
    //#if MC>12006
    //$$  @WrapOperation(
    //$$         method = "canTeleportEntityTo",
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/entity/Entity;canUsePortals(Z)Z"
    //$$         )
    //$$ )
    //$$ private static boolean canTeleportEntityTo(Entity entity, boolean allowVehicles, Operation<Boolean> original) {
    //$$      if (REMSSettings.PortalPearlWarp && entity instanceof EnderPearlEntity) {
    //$$         if (PortalPearlWarpUtil.isInRange(entity.getX(), entity.getZ())) {
    //$$             return false;
    //$$         } else {
    //$$             return original.call(entity, allowVehicles);
    //$$         }
    //$$     } else {
    //$$        return original.call(entity, allowVehicles);
    //$$    }
    //$$ }
    //#endif
    @Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
    private void onPearlHit(HitResult hitResult, CallbackInfo ci) {
        if(REMSSettings.PortalPearlWarp){
        ThrownItemEntity pearl = (ThrownItemEntity) (Object) this;
        World world = pearl.getWorld();

        if (world.isClient() || pearl.getStack().getItem() != Items.ENDER_PEARL) return;

        if (hitResult.getType() != HitResult.Type.BLOCK) return;
        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos hitPos = blockHit.getBlockPos();

        if (!world.getBlockState(hitPos).isOf(Blocks.OBSIDIAN)) return;

        if (PortalPearlWarpUtil.isInRange(pearl.getX(), pearl.getZ())) {

        Entity owner = pearl.getOwner();
        if (!(owner instanceof ServerPlayerEntity player)) return;

        ci.cancel();

        double scale = 1.0;
        if (world.getRegistryKey() == World.OVERWORLD) {
            scale = 0.125; // 主世界坐标缩小8倍
        } else if (world.getRegistryKey() == World.NETHER) {
            scale = 8.0; // 地狱坐标扩大8倍
        }

        double tx = hitPos.getX() * scale;
        double ty = hitPos.getY();
        double tz = hitPos.getZ() * scale;

        pearl.discard();

        player.teleport(tx, ty, tz, PORTAL.shouldAlwaysSpawn());
        player.setPosition(tx,ty,tz);
        player.networkHandler.requestTeleport(
              tx,
              ty,
              tz,
              player.getYaw(),
              player.getPitch()
            );
        }
    }
    }
}