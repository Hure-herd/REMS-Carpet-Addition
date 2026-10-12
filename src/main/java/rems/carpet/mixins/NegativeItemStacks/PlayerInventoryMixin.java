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

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import rems.carpet.REMSSettings;
import rems.carpet.utils.NegativeItemStacks.NegativeItemStacks;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @WrapMethod(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z")
    private boolean absorbOrdinaryStacks(int requestedSlot, ItemStack incoming, Operation<Boolean> original) {
        if (!REMSSettings.negativeItemStacks || incoming.isEmpty() || incoming.getCount() <= 0 || !incoming.isStackable()) {
            return original.call(requestedSlot, incoming);
        }
        PlayerInventory inventory = (PlayerInventory) (Object) this;
        boolean absorbed = false;
        for (int slotIndex = 0; slotIndex < inventory.size(); slotIndex++) {
            if (requestedSlot != -1 && requestedSlot != slotIndex) {
                continue;
            }
            ItemStack target = inventory.getStack(slotIndex);
            if (!NegativeItemStacks.canAbsorb(target, incoming)) {
                continue;
            }
            //#if MC>=12100
            //$$ int maxCount = inventory.getMaxCount(target);
            //#else
            int maxCount = Math.min(target.getMaxCount(), inventory.getMaxCountPerStack());
            //#endif
            int amount = (int) Math.min(incoming.getCount(), (long) maxCount - target.getCount());
            if (amount <= 0) {
                continue;
            }
            target.increment(amount);
            incoming.decrement(amount);
            inventory.markDirty();
            absorbed = true;
            if (incoming.isEmpty()) {
                return true;
            }
        }
        return original.call(requestedSlot, incoming) || absorbed;
    }
}
