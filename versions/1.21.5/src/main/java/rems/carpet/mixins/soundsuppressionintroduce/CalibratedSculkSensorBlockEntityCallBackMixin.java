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

package rems.carpet.mixins.soundsuppressionintroduce;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.utils.Soundsuppressionutils;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import rems.carpet.REMSSettings;
import rems.carpet.utils.magicboxutils;

@Mixin(CalibratedSculkSensorBlockEntity.Callback.class)
public abstract class CalibratedSculkSensorBlockEntityCallBackMixin
{

    BlockPos sourcePos = null;

    @WrapOperation(
            method = "getCalibrationFrequency",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;"
            )
    )
    private <T extends Comparable<T>> T yeetUpdateSuppressionCrash_wrapSoundSuppression(
            BlockState instance, Property<T> property, Operation<T> original,
            @Local(argsOnly = true) World world,
            @Local(argsOnly = true) BlockPos pos
    ){
        if (REMSSettings.soundsuppression)
        {
            BlockPos blockPos;

            int[] Offsets = {-1,0,1};
            for (int dx : Offsets) {
                for (int dy : Offsets) {
                    for (int dz : Offsets) {
                        blockPos = sourcePos.add(dx, dy, dz);
                        Soundsuppressionutils.mark(blockPos);
                        magicboxutils.mark(blockPos);
                        //MinecraftServer server = world.getServer();
                        // Text message1 = Text.literal("方块在 " + blockPos.toShortString() + " 标记成功")
                        //         .styled(style -> style.withColor(Formatting.RED));
                        // server.getPlayerManager().broadcast(message1, false);
                    }
                }
            }
                return original.call(instance, property);
        }else
        {
            return original.call(instance, property);
        }
    }
    @Inject(method = "accepts", at = @At("HEAD"))
    private void onAcceptVibration(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter, CallbackInfoReturnable<Boolean> cir) {
            sourcePos = pos;
    }
}
