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

package rems.carpet.mixins.SpearHitDetection;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.component.type.PiercingWeaponComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import rems.carpet.REMSSettings;

@Mixin(PiercingWeaponComponent.class)
public class PiercingWeaponComponentMixin {
    @WrapOperation(
            method = "stab",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;pierce(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/entity/Entity;FZZZ)Z"
            )
    )
    private boolean synchronizePlayerKnockback(
            LivingEntity attacker,
            EquipmentSlot slot,
            Entity target,
            float damage,
            boolean dealsDamage,
            boolean dealsKnockback,
            boolean dismounts,
            Operation<Boolean> original
    ) {
        Vec3d oldVelocity = target.getVelocity();
        boolean hit = original.call(attacker, slot, target, damage, dealsDamage, dealsKnockback, dismounts);
        if (REMSSettings.fixSpearHitDetection
                && dealsKnockback
                && target instanceof ServerPlayerEntity player
                && player.knockedBack) {
            player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
            player.knockedBack = false;
            player.setVelocity(oldVelocity);
        }
        return hit;
    }
}
