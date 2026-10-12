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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.utils.PetCapture.CapturedPet;
import rems.carpet.utils.PetCapture.PetCapture;
import java.util.UUID;
import net.minecraft.util.math.Vec3d;
//#if MC>=12108
//$$ import net.minecraft.storage.ReadView;
//$$ import net.minecraft.storage.WriteView;
//#endif

@Mixin(MobEntity.class)
public abstract class MobEntityMixin implements CapturedPet {
    @Unique private static final TrackedData<String> rems$PET_OWNER = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.STRING);
    @Unique private static final TrackedData<Boolean> rems$PET_SITTING = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public UUID rems$getPetOwner() { return PetCapture.uuid(((MobEntity) (Object) this).getDataTracker().get(rems$PET_OWNER)); }
    public void rems$setPetOwner(UUID owner) { ((MobEntity) (Object) this).getDataTracker().set(rems$PET_OWNER, owner == null ? "" : owner.toString()); }
    public boolean rems$isPetSitting() { return ((MobEntity) (Object) this).getDataTracker().get(rems$PET_SITTING); }
    public void rems$setPetSitting(boolean sitting) {
        MobEntity mob = (MobEntity) (Object) this;
        mob.getDataTracker().set(rems$PET_SITTING, sitting);
        if (mob instanceof net.minecraft.entity.passive.TameableEntity tameable) {
            tameable.setSitting(sitting);
            tameable.setInSittingPose(sitting);
        }
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    //#if MC>=12100
    //$$ private void trackPet(DataTracker.Builder builder, CallbackInfo ci) {
    //$$     builder.add(rems$PET_OWNER, "");
    //$$     builder.add(rems$PET_SITTING, false);
    //$$ }
    //#else
    private void trackPet(CallbackInfo ci) {
        ((MobEntity) (Object) this).getDataTracker().startTracking(rems$PET_OWNER, "");
        ((MobEntity) (Object) this).getDataTracker().startTracking(rems$PET_SITTING, false);
    }
    //#endif

    //#if MC>=12108
    //$$ @Inject(method = "writeCustomData", at = @At("TAIL"))
    //$$ private void savePet(WriteView data, CallbackInfo ci) {
    //#else
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void savePet(NbtCompound data, CallbackInfo ci) {
    //#endif
        if (rems$getPetOwner() != null) {
            data.putString(PetCapture.OWNER, rems$getPetOwner().toString());
            data.putBoolean(PetCapture.SITTING, rems$isPetSitting());
        }
    }

    //#if MC>=12108
    //$$ @Inject(method = "readCustomData", at = @At("TAIL"))
    //$$ private void loadPet(ReadView data, CallbackInfo ci) {
    //$$     rems$setPetOwner(PetCapture.uuid(data.getString(PetCapture.OWNER, "")));
    //$$     rems$setPetSitting(data.getBoolean(PetCapture.SITTING, false));
    //$$ }
    //#else
    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void loadPet(NbtCompound data, CallbackInfo ci) {
        rems$setPetOwner(PetCapture.uuid(data.getString(PetCapture.OWNER)));
        rems$setPetSitting(data.getBoolean(PetCapture.SITTING));
    }
    //#endif

    @Inject(method = "tickNewAi", at = @At("HEAD"), cancellable = true)
    private void loyalPetAi(CallbackInfo ci) {
        if (rems$getPetOwner() != null) {
            PetCapture.tickPet((MobEntity) (Object) this);
            ci.cancel();
        }
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void preventPetAggression(LivingEntity target, CallbackInfo ci) {
        if (rems$getPetOwner() != null && target != null) ci.cancel();
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    //#if MC>=260102
    //$$ private void togglePetStay(PlayerEntity player, Hand hand, Vec3d hitPos, CallbackInfoReturnable<ActionResult> cir) {
    //#else
    private void togglePetStay(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
    //#endif
        MobEntity mob = (MobEntity) (Object) this;
        if (rems$getPetOwner() != null && rems$getPetOwner().equals(player.getUuid())
                && player.getStackInHand(hand).isEmpty() && player.isSneaking()
                && PetCapture.reclaim(mob, player)) {
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }
        if (rems$getPetOwner() != null && rems$getPetOwner().equals(player.getUuid()) && player.getStackInHand(hand).isEmpty()) {
            if (!PetCapture.world(player).isClient()) rems$setPetSitting(!rems$isPetSitting());
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
