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

package rems.carpet.mixins.PetCapture;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.utils.PetCapture.CapturedPet;
import rems.carpet.utils.PetCapture.PetCaptureRegistry;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "onDeath", at = @At("TAIL"))
    private void releasePetSlot(DamageSource source, CallbackInfo ci) {
        if ((Object) this instanceof MobEntity mob) PetCaptureRegistry.onDeath(mob);
    }

    //#if MC>=12104
    //$$ @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    //$$ private void protectPetOwner(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    //#else
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void protectPetOwner(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    //#endif
        LivingEntity victim = (LivingEntity) (Object) this;
        if (source.getAttacker() instanceof MobEntity mob
                && victim.getUuid().equals(((CapturedPet) mob).rems$getPetOwner())) cir.setReturnValue(false);
    }
}
