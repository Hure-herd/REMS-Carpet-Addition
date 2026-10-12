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

package rems.carpet.mixins.NegativeItemStacks;

import org.spongepowered.asm.mixin.Mixin;
//#if MC>=12100
//$$ import net.minecraft.item.ItemStack;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//$$ import rems.carpet.utils.NegativeItemStacks.NegativeItemStacks;
//#else
import rems.carpet.utils.compat.DummyClass;
//#endif

//#if MC>=12100
//$$ @Mixin(targets = "net.minecraft.item.ItemStack$1")
//#else
@Mixin(DummyClass.class)
//#endif
public abstract class ItemStackPacketCodecMixin {
    //#if MC>=12100
    //$$ @Redirect(
    //$$         method = "encode(Lnet/minecraft/network/RegistryByteBuf;Lnet/minecraft/item/ItemStack;)V",
    //$$         at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I")
    //$$ )
    //$$ private int encodeSignedCount(ItemStack stack) {
    //$$     return NegativeItemStacks.isNegativeStack(stack) ? 1 : stack.getCount();
    //$$ }
    //#endif
}
