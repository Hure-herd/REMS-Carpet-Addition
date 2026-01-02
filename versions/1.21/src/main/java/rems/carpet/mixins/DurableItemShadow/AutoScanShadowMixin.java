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

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public class AutoScanShadowMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void scanForShadows(CallbackInfo ci) {

        if(!REMSSettings.durableItemShadow)return;

        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // 每 100 ticks (5秒) 运行一次，防止刷屏太快
        if (player.age % 20 != 0) return;

        PlayerInventory inv = player.getInventory();

        // --- 调试开始：打印前 9 格物品的内存地址 ---
        System.out.println("========== [ShadowMod Debug] 正在扫描背包 ==========");

        // 记录内存地址相同的物品
        List<Integer> seenHashes = new ArrayList<>();
        boolean foundShadow = false;

        // 只扫描快捷栏 (0-8) 和背包 (9-35)，避免太多日志
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty()) continue;

            // 获取对象的内存哈希码 (这是物品在内存里的真实身份证)
            int memoryId = System.identityHashCode(stack);
            String itemName = stack.getName().getString();

            // 打印日志：让能在后台看到每一个物品的真实身份
            // 如果两个物品的 [MemoryID] 一模一样，说明它们是分身
            System.out.println("槽位 " + i + ": " + itemName + " | MemoryID: " + memoryId);

            if (seenHashes.contains(memoryId)) {
                System.out.println(">>> 发现分身！(MemoryID " + memoryId + " 重复出现)");
                foundShadow = true;
                ensureShadowId(stack, i);
            } else {
                seenHashes.add(memoryId);
            }
        }
        System.out.println("==================================================");

        // 如果发现了分身，标记背包脏数据（保存存档）
        if (foundShadow) {
            inv.markDirty();
        }
    }

    private void ensureShadowId(ItemStack stack, int slotIndex) {
        // 检查 NBT
        NbtComponent currentData = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);

        // 如果已经有 ID，我们在控制台告诉你是哪个 ID
        if (currentData.contains("ShadowID")) {
            String existingId = currentData.copyNbt().getUuid("ShadowID").toString();
            System.out.println("   -> 槽位 " + slotIndex + " 已经有 ID 了: " + existingId);
            return;
        }

        // 如果没有 ID，则添加
        NbtCompound mutableNbt = currentData.copyNbt();
        UUID newId = UUID.randomUUID();
        mutableNbt.putUuid("ShadowID", newId);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(mutableNbt));

        System.out.println("   -> 槽位 " + slotIndex + " 锁定成功！新 ID: " + newId);
    }
}

