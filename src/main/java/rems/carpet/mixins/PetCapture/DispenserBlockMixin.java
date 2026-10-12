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

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.utils.PetCapture.PetCapture;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {
    //#if MC>=12100
    //$$ @Inject(method = "getBehaviorForItem(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/block/dispenser/DispenserBehavior;", at = @At("HEAD"), cancellable = true)
    //$$ private void dispensePetEggAsItem(World world, ItemStack stack, CallbackInfoReturnable<DispenserBehavior> cir) {
    //#else
    @Inject(method = "getBehaviorForItem", at = @At("HEAD"), cancellable = true)
    private void dispensePetEggAsItem(ItemStack stack, CallbackInfoReturnable<DispenserBehavior> cir) {
    //#endif
        if (PetCapture.isEgg(stack)) cir.setReturnValue(new ItemDispenserBehavior());
    }
}
