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

package rems.carpet.mixins.DurableItemShadow;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;
import rems.carpet.utils.DurableItemShadow.ShadowCacheManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public class AutoScanShadowMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void scanForShadows(CallbackInfo ci) {

        if (!REMSSettings.durableItemShadow) return;

        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player.age % 20 != 0) return;

        PlayerInventory inv = player.getInventory();
        Map<Integer, ItemStack> firstSeen = new HashMap<>();
        Map<Integer, UUID> shadowIds = new HashMap<>();
        boolean foundShadow = false;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty()) continue;
            int hash = System.identityHashCode(stack);
            if (shadowIds.containsKey(hash)) {
                foundShadow |= ensureShadowId(stack, shadowIds.get(hash));
            } else if (firstSeen.containsKey(hash)) {
                UUID id = UUID.randomUUID();
                shadowIds.put(hash, id);
                foundShadow |= ensureShadowId(firstSeen.get(hash), id);
                foundShadow |= ensureShadowId(stack, id);
            } else {
                firstSeen.put(hash, stack);
            }
        }

        if (foundShadow) {
            inv.markDirty();
            ShadowCacheManager.maybeAutoSave();
        }
    }

    @Unique
    private boolean ensureShadowId(ItemStack stack, UUID shadowId) {
        NbtCompound currentData = stack.getNbt();
        if (currentData != null && currentData.containsUuid("ShadowID")) return false;

        NbtCompound mutableNbt = currentData != null ? currentData.copy() : new NbtCompound();
        mutableNbt.putUuid("ShadowID", shadowId);
        stack.setNbt(mutableNbt);
        return true;
    }
}
