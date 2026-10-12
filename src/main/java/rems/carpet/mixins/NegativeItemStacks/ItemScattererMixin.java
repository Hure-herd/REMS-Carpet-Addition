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
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import rems.carpet.REMSSettings;
import rems.carpet.utils.NegativeItemStacks.NegativeItemScreenHandler;
import rems.carpet.utils.NegativeItemStacks.NegativeItemStacks;

import java.util.ArrayList;
import java.util.List;
@Mixin(ItemScatterer.class)
public abstract class ItemScattererMixin {
    @WrapMethod(method = "spawn(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/inventory/Inventory;)V")
    private static void retainDestroyedChestStacks(World world, BlockPos pos, Inventory inventory, Operation<Void> original) {
        if (!REMSSettings.negativeItemStacks || world.isClient() || !(inventory instanceof ChestBlockEntity)) {
            original.call(world, pos, inventory);
            return;
        }

        List<GenericContainerScreenHandler> viewers = new ArrayList<>();
        for (PlayerEntity player : world.getPlayers()) {
            if (player.currentScreenHandler instanceof GenericContainerScreenHandler handler) {
                Inventory viewedInventory = handler.getInventory();
                if (viewedInventory == inventory
                        || viewedInventory instanceof DoubleInventory doubleInventory && doubleInventory.isPart(inventory)) {
                    viewers.add(handler);
                }
            }
        }

        List<ItemStack> stacks = new ArrayList<>();
        if (!viewers.isEmpty()) {
            for (int slot = 0; slot < inventory.size(); slot++) {
                ItemStack stack = inventory.getStack(slot);
                if (!stack.isEmpty() && stack.getCount() > 0) {
                    stacks.add(stack);
                }
            }
        }

        original.call(world, pos, inventory);

        for (ItemStack stack : stacks) {
            if (stack.getCount() == 0) {
                NegativeItemStacks.mark(stack);
            }
        }
        for (GenericContainerScreenHandler handler : viewers) {
            ((NegativeItemScreenHandler) handler).rems$allowDestroyedChestUntil(world.getTime() + 6);
            handler.sendContentUpdates();
        }
    }

    @Redirect(
            method = "spawn(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z")
    )
    private static boolean stopScatteringNegativeStacks(ItemStack stack) {
        return stack.isEmpty() || NegativeItemStacks.isNegativeStack(stack);
    }
}
