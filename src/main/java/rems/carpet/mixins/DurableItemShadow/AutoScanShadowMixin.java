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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Unique;
import rems.carpet.REMSSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public class AutoScanShadowMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void scanForShadows(CallbackInfo ci){

        if(!REMSSettings.durableItemShadow)return;

        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        if(player.age % 100 != 0)return;

        PlayerInventory inv = player.getInventory();
        List<Integer> seenHashes = new ArrayList<>();
        boolean foundShadow = false;

        for(int i = 0; i < 36; i++) {
            ItemStack stack = inv.getStack(i);
            if(stack.isEmpty())continue;
            int memoryId = System.identityHashCode(stack);
            if(seenHashes.contains(memoryId)) {
                foundShadow = true;
                ensureShadowId(stack, i);
            }else{
                seenHashes.add(memoryId);
            }
        }
        if(foundShadow) {
            inv.markDirty();
        }
    }

    @Unique
    private void ensureShadowId(ItemStack stack, int slotIndex){

        NbtCompound currentData = stack.getOrCreateNbt();
        if(currentData.contains("ShadowID"))return;

        NbtCompound mutableNbt = currentData.copy();
        UUID newId = UUID.randomUUID();
        mutableNbt.putUuid("ShadowID", newId);
        stack.setNbt(mutableNbt);
    }
}
