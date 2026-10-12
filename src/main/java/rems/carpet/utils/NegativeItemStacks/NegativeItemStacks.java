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

package rems.carpet.utils.NegativeItemStacks;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
//#if MC<12108
import net.minecraft.nbt.NbtElement;
//#endif
import rems.carpet.REMSSettings;

public final class NegativeItemStacks {
    public static final String COUNT_KEY = "rems:negative_stack_count";

    public static boolean isNegativeStack(ItemStack stack) {
        return REMSSettings.negativeItemStacks && !stack.isEmpty() && stack.getCount() <= 0;
    }

    public static boolean canAbsorb(ItemStack target, ItemStack incoming) {
        if (!isNegativeStack(target) || incoming.isEmpty() || incoming.getCount() <= 0 || !incoming.isStackable()) {
            return false;
        }
        ItemStack normalized = target.copyWithCount(1);
        //#if MC>=12100
        //$$ return ItemStack.areItemsAndComponentsEqual(normalized, incoming);
        //#else
        return ItemStack.canCombine(normalized, incoming);
        //#endif
    }

    public static void mark(ItemStack stack) {
        NegativeItemStack access = (NegativeItemStack) (Object) stack;
        NbtCompound data = access.rems$getStackData();
        if (data == null) {
            data = new NbtCompound();
        }
        data.putInt(COUNT_KEY, 0);
        access.rems$setStackData(data);
        stack.setCount(0);
    }

    public static int getStoredCount(ItemStack stack) {
        NbtCompound data = ((NegativeItemStack) (Object) stack).rems$getStackData();
        if (data == null) {
            return 1;
        }
        //#if MC>=12108
        //$$ return data.getInt(COUNT_KEY, 1);
        //#else
        return data.contains(COUNT_KEY, NbtElement.INT_TYPE) ? data.getInt(COUNT_KEY) : 1;
        //#endif
    }

    public static void updateStoredCount(ItemStack stack, int count) {
        NegativeItemStack access = (NegativeItemStack) (Object) stack;
        NbtCompound data = access.rems$getStackData();
        if (data == null || getStoredCount(stack) > 0) {
            return;
        }
        if (count <= 0) {
            data.putInt(COUNT_KEY, count);
        } else {
            data.remove(COUNT_KEY);
        }
        access.rems$setStackData(data);
    }

    public static ItemStack forSerialization(ItemStack stack) {
        if (!isNegativeStack(stack)) {
            return stack;
        }
        ItemStack carrier = stack.copy();
        ((NegativeItemStack) (Object) carrier).rems$setRawCount(1);
        return carrier;
    }

    public static World getWorld(PlayerEntity player) {
        //#if MC>=12110
        //$$ return player.getEntityWorld();
        //#else
        return player.getWorld();
        //#endif
    }

    private NegativeItemStacks() {
    }
}
