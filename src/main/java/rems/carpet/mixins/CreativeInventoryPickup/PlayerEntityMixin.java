/*
 * This file is part of the REMS-Carpet-Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 A Minecraft Server and contributors
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

package rems.carpet.mixins.CreativeInventoryPickup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "giveItemStack", at = @At("HEAD"), cancellable = true)
    private void blockPickupInCreativeWhenFull(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!REMSSettings.noCreativePickupWhenFull) return;
        PlayerEntity self = (PlayerEntity)(Object)this;
        if (self.isCreative()) {
            PlayerInventory inv = self.getInventory();
            if (inv.getEmptySlot() == -1 && inv.getOccupiedSlotWithRoomForStack(stack) == -1) {
                cir.setReturnValue(false);
            }
        }
    }
}
