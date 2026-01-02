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

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Uuids;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;
import rems.carpet.utils.DurableItemShadow.ShadowCacheManager;

import java.util.UUID;
import java.util.function.Function;

@Mixin(ItemStack.class)
public class PersistentShadowMixin {

    @Shadow @Final @Mutable public static MapCodec<ItemStack> MAP_CODEC;
    @Shadow @Final @Mutable public static Codec<ItemStack> CODEC;
    @Shadow @Final @Mutable public static Codec<ItemStack> VALIDATED_CODEC;
    @Shadow @Final @Mutable public static Codec<ItemStack> UNCOUNTED_CODEC;
    @Shadow @Final @Mutable public static Codec<ItemStack> VALIDATED_UNCOUNTED_CODEC;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void hijackCodecs(CallbackInfo ci){

        MAP_CODEC = MAP_CODEC.xmap(PersistentShadowMixin::rems$handleSingleton,Function.identity());
        CODEC = Codec.lazyInitialized(MAP_CODEC::codec);
        VALIDATED_CODEC = CODEC.validate(ItemStack::validate);
        UNCOUNTED_CODEC = Codec.lazyInitialized(() -> MAP_CODEC.codec());
        VALIDATED_UNCOUNTED_CODEC = UNCOUNTED_CODEC.validate(ItemStack::validate);
    }

    @Unique
    private static ItemStack rems$handleSingleton(ItemStack stack) {

        if(stack.isEmpty())return stack;
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if(customData == null)return stack;
        UUID shadowId = null;
        try{
            NbtCompound nbt = customData.copyNbt();
            if(nbt.contains("ShadowID")){
                try {
                    shadowId = nbt.get("ShadowID", Uuids.INT_STREAM_CODEC).orElse(null);
                }catch(Exception ignored){}
                if (shadowId == null){
                    shadowId = nbt.getString("ShadowID")
                            .map(UUID::fromString)
                            .orElse(null);
                }
            }
        }catch(Exception e){
            return stack;
        }
        if(shadowId == null)return stack;
        if(ShadowCacheManager.SHADOW_CACHE.containsKey(shadowId)){
            ItemStack masterStack = ShadowCacheManager.SHADOW_CACHE.get(shadowId);
            if(stack.getCount() > masterStack.getCount()){
                masterStack.setCount(stack.getCount());
            }
            return masterStack;
        }else{
            ShadowCacheManager.SHADOW_CACHE.put(shadowId, stack);
            return stack;
        }
    }

    @Inject(method = "areItemsAndComponentsEqual", at = @At("RETURN"), cancellable = true)
    private static void allowRefillingShadow(ItemStack stack, ItemStack otherStack, CallbackInfoReturnable<Boolean> cir){

        if(!REMSSettings.durableItemShadow)return;
        if(cir.getReturnValue() || stack.isEmpty() || otherStack.isEmpty())return;
        if(!stack.isOf(otherStack.getItem()))return;

        NbtComponent dataA = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtComponent dataB = otherStack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound dataNbtA = null;
        if (dataA != null) {
            dataNbtA = dataA.copyNbt();
        }
        NbtCompound dataNbtB = null;
        if (dataB != null) {
            dataNbtB = dataB.copyNbt();
        }
        boolean hasShadowA = dataA != null && dataNbtA.contains("ShadowID");
        boolean hasShadowB = dataB != null && dataNbtB.contains("ShadowID");
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

            return ItemStack.areItemsAndComponentsEqual(copyA, copyB);
        }catch(Exception e){
            return false;
        }
    }

    @Unique
    private static void removeShadowId(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound dataNbt = null;
        if (component != null) {
            dataNbt = component.copyNbt();
        }
        if (component != null && dataNbt.contains("ShadowID")) {
            NbtCompound nbt = component.copyNbt();
            nbt.remove("ShadowID");
            if (nbt.isEmpty()) {
                stack.remove(DataComponentTypes.CUSTOM_DATA);
            } else {
                stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
            }
        }
    }
}
