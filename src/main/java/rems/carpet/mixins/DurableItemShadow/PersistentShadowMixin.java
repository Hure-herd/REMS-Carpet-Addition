/*
 * This file is part of the Carpet REMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 A Minecraft Server and contributors
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

package rems.carpet.mixins.DurableItemShadow;

import org.spongepowered.asm.mixin.Unique;
import rems.carpet.REMSSettings;
import rems.carpet.utils.DurableItemShadow.ShadowCacheManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ItemStack.class)
public class PersistentShadowMixin{

    @Inject(method = "fromNbt", at = @At("RETURN"), cancellable = true)
    private static void restoreShadowLink(NbtCompound nbt, CallbackInfoReturnable<ItemStack> cir){

        ItemStack optionalStack = cir.getReturnValue();
        if (optionalStack.isEmpty()) return;
        ItemStack newStack = optionalStack;

        NbtCompound customData = newStack.getNbt();
        if (customData == null || !customData.contains("ShadowID"))return;

        try{
            NbtCompound dataNbt = customData.copy();
            UUID shadowId = dataNbt.getUuid("ShadowID");
            if(ShadowCacheManager.SHADOW_CACHE.containsKey(shadowId)) {
                ItemStack existingStack = ShadowCacheManager.SHADOW_CACHE.get(shadowId);
                if(newStack.getCount() > existingStack.getCount()) {
                    existingStack.setCount(newStack.getCount());
                }
                cir.setReturnValue(existingStack);
            }else{
                ShadowCacheManager.SHADOW_CACHE.put(shadowId, newStack);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "areNbtEqual", at = @At("RETURN"), cancellable = true)
    private static void allowRefillingShadow(ItemStack stack, ItemStack otherStack, CallbackInfoReturnable<Boolean> cir){

        if(!REMSSettings.durableItemShadow)return;
        if(cir.getReturnValue() || stack.isEmpty() || otherStack.isEmpty()) return;
        if(!stack.isOf(otherStack.getItem()))return;

        NbtCompound dataA = stack.getNbt();
        NbtCompound dataB = otherStack.getNbt();
        boolean hasShadowA = dataA != null && dataA.contains("ShadowID");
        boolean hasShadowB = dataB != null && dataB.contains("ShadowID");
        if(!hasShadowA && !hasShadowB)return;

        if(isActuallyTheSameItem(stack, otherStack)){
            cir.setReturnValue(true);
        }
    }

    @Unique
    private static boolean isActuallyTheSameItem(ItemStack a, ItemStack b){
        try{
            ItemStack copyA = a.copy();
            ItemStack copyB = b.copy();

            removeShadowId(copyA);
            removeShadowId(copyB);

            return ItemStack.canCombine(copyA, copyB);
        }catch (Exception e) {
            return false;
        }
    }

    @Unique
    private static void removeShadowId(ItemStack stack){
        NbtCompound component = stack.getNbt();
        if(component != null && component.contains("ShadowID")) {
            NbtCompound nbt = component.copy();
            nbt.remove("ShadowID");
            if(nbt.isEmpty()) {
                stack.setNbt(null);
            }else{
                stack.setNbt(nbt);
            }
        }
    }
}