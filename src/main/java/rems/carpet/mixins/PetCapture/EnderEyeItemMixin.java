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
import net.minecraft.item.EnderEyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;
import rems.carpet.utils.PetCapture.PetCapture;
//#if MC<12104
import net.minecraft.util.TypedActionResult;
//#endif

@Mixin(EnderEyeItem.class)
public abstract class EnderEyeItemMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void keepCaptureEye(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (REMSSettings.petCapture && PetCapture.isEye(context.getStack())) cir.setReturnValue(ActionResult.PASS);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    //#if MC>=12104
    //$$ private void throwCaptureEye(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
    //#else
    private void throwCaptureEye(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
    //#endif
        ItemStack stack = player.getStackInHand(hand);
        if (!REMSSettings.petCapture || !PetCapture.isEye(stack)) return;
        boolean success = world.isClient() || PetCapture.throwEye(player, stack);
        //#if MC>=12104
        //$$ cir.setReturnValue(success ? ActionResult.SUCCESS : ActionResult.FAIL);
        //#else
        cir.setReturnValue(success ? TypedActionResult.success(stack, world.isClient()) : TypedActionResult.fail(stack));
        //#endif
    }
}
