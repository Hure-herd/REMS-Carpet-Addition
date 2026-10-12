/*
 * This file is part of the REMS-Carpet-Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 Hureherd and contributors
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

package rems.carpet.mixins.ReintroduceFallingBlockSliding;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {

    @Inject(method = "tick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/FallingBlockEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
                    //#if MC>=12100
                    //$$ ordinal = 0),
                    //#else
                    ordinal = 1),
                    //#endif
            cancellable = true)
    private void reintroduceFallingBlockSliding(CallbackInfo ci,
        @Local(ordinal = 1) boolean touchingWater){
        if(!REMSSettings.reintroduceFallingBlockSliding)return;
        FallingBlockEntity entity = (FallingBlockEntity) (Object) this;
        BlockPos below = BlockPos.ofFloored(entity.getX(), entity.getY() - 0.01F, entity.getZ());
        if (!touchingWater && FallingBlock.canFallThrough(entity.getWorld().getBlockState(below))) {
            entity.setOnGround(false);
            ci.cancel();
        }
    }
}
