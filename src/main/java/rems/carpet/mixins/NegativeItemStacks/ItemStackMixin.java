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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;
import rems.carpet.utils.NegativeItemStacks.NegativeItemStack;
import rems.carpet.utils.NegativeItemStacks.NegativeItemStacks;
//#if MC>=12100
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//$$ import com.mojang.serialization.Codec;
//$$ import net.minecraft.component.DataComponentTypes;
//$$ import net.minecraft.component.type.NbtComponent;
//$$ import org.objectweb.asm.Opcodes;
//$$ import org.spongepowered.asm.mixin.Mutable;
//$$ import java.util.function.Function;
//#endif
//#if MC>=12104
//$$ import net.minecraft.component.MergedComponentMap;
//#elseif MC>=12100
//$$ import net.minecraft.component.ComponentMapImpl;
//#endif
//#if MC>=12108
//$$ import com.mojang.serialization.MapCodec;
//#endif
//#if MC>=260100
//$$ import net.minecraft.registry.entry.RegistryEntry;
//#endif

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements NegativeItemStack {
    //#if MC>=260100
    //$$ @Shadow @Final private RegistryEntry<Item> item;
    //#else
    @Shadow @Final private Item item;
    //#endif
    @Shadow private int count;
    //#if MC>=12100
    //$$ @Shadow @Final @Mutable private static Codec<ItemStack> CODEC;
    //#endif
    //#if MC>=12108
    //$$ @Shadow @Final @Mutable private static MapCodec<ItemStack> MAP_CODEC;
    //#endif
    //#if MC>=12104
    //$$ @Shadow @Final private MergedComponentMap components;
    //#elseif MC>=12100
    //$$ @Shadow @Final private ComponentMapImpl components;
    //#else
    @Shadow private NbtCompound nbt;
    //#endif

    @Override
    public NbtCompound rems$getStackData() {
        //#if MC>=12100
        //$$ NbtComponent data = components.get(DataComponentTypes.CUSTOM_DATA);
        //$$ return data == null ? null : data.copyNbt();
        //#else
        return nbt == null ? null : nbt.copy();
        //#endif
    }

    @Override
    public void rems$setStackData(NbtCompound data) {
        //#if MC>=12100
        //$$ components.set(DataComponentTypes.CUSTOM_DATA, data.isEmpty() ? null : NbtComponent.of(data));
        //#else
        ((ItemStack) (Object) this).setNbt(data.isEmpty() ? null : data);
        //#endif
    }

    @Override
    public void rems$setRawCount(int amount) {
        count = amount;
    }

    @Inject(method = "isEmpty", at = @At("HEAD"), cancellable = true)
    private void retainNegativeStack(CallbackInfoReturnable<Boolean> cir) {
        //#if MC>=260100
        //$$ Item rawItem = item == null ? null : item.value();
        //#else
        Item rawItem = item;
        //#endif
        if (REMSSettings.negativeItemStacks && rawItem != null && rawItem != Items.AIR && count <= 0
                && NegativeItemStacks.getStoredCount((ItemStack) (Object) this) <= 0) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "setCount", at = @At("TAIL"))
    private void storeSignedCount(int amount, CallbackInfo ci) {
        NegativeItemStacks.updateStoredCount((ItemStack) (Object) this, count);
    }

    //#if MC>=260100
    //$$ @Inject(method = "<init>(Lnet/minecraft/registry/entry/RegistryEntry;ILnet/minecraft/component/MergedComponentMap;)V", at = @At("RETURN"))
    //#elseif MC>=12104
    //$$ @Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;ILnet/minecraft/component/MergedComponentMap;)V", at = @At("RETURN"))
    //#elseif MC>=12100
    //$$ @Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;ILnet/minecraft/component/ComponentMapImpl;)V", at = @At("RETURN"))
    //#else
    @Inject(method = {"<init>(Lnet/minecraft/nbt/NbtCompound;)V", "setNbt"}, at = @At("RETURN"))
    //#endif
    private void restoreSignedCount(CallbackInfo ci) {
        int storedCount = NegativeItemStacks.getStoredCount((ItemStack) (Object) this);
        if (storedCount <= 0) {
            count = storedCount;
        }
    }

    //#if MC>=12100
    //$$ @Inject(method = {"applyChanges", "applyUnvalidatedChanges", "applyComponentsFrom"}, at = @At("TAIL"))
    //$$ private void restoreCountAfterComponents(CallbackInfo ci) {
    //$$     restoreSignedCount(ci);
    //$$ }
    //$$
    //$$ @WrapOperation(
    //$$         method = "<clinit>",
    //$$         at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemStack;CODEC:Lcom/mojang/serialization/Codec;", opcode = Opcodes.PUTSTATIC)
    //$$ )
    //$$ private static void encodeSignedCount(Codec<ItemStack> codec, Operation<Void> original) {
    //$$     original.call(codec.xmap(Function.identity(), NegativeItemStacks::forSerialization));
    //$$ }
    //#endif
    //#if MC>=12108
    //$$ @WrapOperation(
    //$$         method = "<clinit>",
    //$$         at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemStack;MAP_CODEC:Lcom/mojang/serialization/MapCodec;", opcode = Opcodes.PUTSTATIC)
    //$$ )
    //$$ private static void encodeSignedMapCount(MapCodec<ItemStack> codec, Operation<Void> original) {
    //$$     original.call(codec.xmap(Function.identity(), NegativeItemStacks::forSerialization));
    //$$ }
    //#endif

    @Inject(method = "split", at = @At("HEAD"), cancellable = true)
    private void splitNegativeStack(int amount, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (amount > 0 && NegativeItemStacks.isNegativeStack(stack)) {
            ItemStack result = stack.copyWithCount(amount);
            stack.decrement(amount);
            cir.setReturnValue(result);
        }
    }
}
