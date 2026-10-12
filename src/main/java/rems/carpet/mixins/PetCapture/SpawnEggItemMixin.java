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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.utils.PetCapture.PetCapture;
//#if MC<12104
import net.minecraft.util.TypedActionResult;
//#endif

@Mixin(SpawnEggItem.class)
public abstract class SpawnEggItemMixin {
    @Inject(method = "spawnBaby", at = @At("HEAD"), cancellable = true)
    //#if MC>=260100
    //$$ private static void preventPetEggBreeding(PlayerEntity player, MobEntity mob, EntityType<? extends MobEntity> type, ServerWorld world, Vec3d position, ItemStack stack, CallbackInfoReturnable<Optional<MobEntity>> cir) {
    //#else
    private void preventPetEggBreeding(PlayerEntity player, MobEntity mob, EntityType<? extends MobEntity> type, ServerWorld world, Vec3d position, ItemStack stack, CallbackInfoReturnable<Optional<MobEntity>> cir) {
    //#endif
        if (PetCapture.isEgg(stack)) cir.setReturnValue(Optional.empty());
    }

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void releaseCapturedPet(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!PetCapture.isEgg(context.getStack())) return;
        PlayerEntity player = context.getPlayer();
        Vec3d position = Vec3d.ofBottomCenter(context.getBlockPos().offset(context.getSide()));
        boolean success = player != null && (context.getWorld().isClient() || PetCapture.release(context.getStack(), player, position));
        cir.setReturnValue(success ? ActionResult.SUCCESS : ActionResult.FAIL);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    //#if MC>=12104
    //$$ private void blockVanillaRelease(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
    //$$     if (PetCapture.isEgg(player.getStackInHand(hand))) cir.setReturnValue(ActionResult.FAIL);
    //$$ }
    //#else
    private void blockVanillaRelease(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (PetCapture.isEgg(player.getStackInHand(hand))) cir.setReturnValue(TypedActionResult.fail(player.getStackInHand(hand)));
    }
    //#endif
}
