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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.utils.NegativeItemStacks.NegativeItemStacks;
@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void moveNegativeStack(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        ScreenHandler handler = (ScreenHandler) (Object) this;
        ItemStack cursor = handler.getCursorStack();
        boolean negativeCursor = NegativeItemStacks.isNegativeStack(cursor);
        boolean validSlot = slotIndex >= 0 && slotIndex < handler.slots.size();
        boolean negativeSlot = validSlot && NegativeItemStacks.isNegativeStack(handler.slots.get(slotIndex).getStack());
        if (!negativeCursor && !negativeSlot) {
            return;
        }
        if (actionType == SlotActionType.QUICK_MOVE) {
            if (negativeSlot) {
                Slot source = handler.slots.get(slotIndex);
                if ((button == 0 || button == 1) && source.canTakeItems(player)) {
                    handler.quickMove(player, slotIndex);
                }
                ci.cancel();
            }
            return;
        }
        if (actionType != SlotActionType.PICKUP && actionType != SlotActionType.SWAP) {
            ci.cancel();
            return;
        }
        if (actionType == SlotActionType.SWAP) {
            return;
        }
        if (!validSlot || button < 0 || button > 1) {
            ci.cancel();
            return;
        }
        Slot slot = handler.slots.get(slotIndex);
        ItemStack stack = slot.getStack();
        if ((stack.isEmpty() || slot.canTakeItems(player))
                && (cursor.isEmpty() || slot.canInsert(cursor) && cursor.getCount() <= slot.getMaxItemCount(cursor))) {
            handler.setCursorStack(stack);
            slot.setStack(cursor);
        }
        ci.cancel();
    }

    @Inject(method = "insertItem", at = @At("HEAD"), cancellable = true)
    private void insertWholeNegativeStack(ItemStack stack, int startIndex, int endIndex, boolean fromLast, CallbackInfoReturnable<Boolean> cir) {
        if (!NegativeItemStacks.isNegativeStack(stack)) {
            return;
        }
        ScreenHandler handler = (ScreenHandler) (Object) this;
        int slotIndex = fromLast ? endIndex - 1 : startIndex;
        while (slotIndex >= startIndex && slotIndex < endIndex) {
            Slot destination = handler.slots.get(slotIndex);
            if (!destination.hasStack() && destination.canInsert(stack) && destination.getMaxItemCount(stack) > 0) {
                destination.setStack(stack.copy());
                stack.setCount(1);
                stack.setCount(0);
                cir.setReturnValue(true);
                return;
            }
            slotIndex += fromLast ? -1 : 1;
        }
        cir.setReturnValue(false);
    }

    @Inject(method = "onClosed", at = @At("HEAD"))
    private void returnNegativeCursorStack(PlayerEntity player, CallbackInfo ci) {
        ScreenHandler handler = (ScreenHandler) (Object) this;
        ItemStack cursor = handler.getCursorStack();
        if (NegativeItemStacks.isNegativeStack(cursor)) {
            if (!NegativeItemStacks.getWorld(player).isClient() && player.isAlive()) {
                int emptySlot = player.getInventory().getEmptySlot();
                if (emptySlot >= 0) {
                    player.getInventory().setStack(emptySlot, cursor);
                }
            }
            handler.setCursorStack(ItemStack.EMPTY);
        }
    }
}
