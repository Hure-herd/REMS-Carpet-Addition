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

package rems.carpet.utils.DurableItemShadow;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShadowCacheManager {

    private static final String FILE_NAME = "rems_durable_shadow.nbt";
    private static final long AUTO_SAVE_INTERVAL_MS = 60_000L;

    public static final Map<UUID, ItemStack> SHADOW_CACHE = new HashMap<>();

    private static MinecraftServer server;
    private static boolean loadedFromDisk = false;
    private static long lastAutoSave = 0L;

    static {
        ServerLifecycleEvents.SERVER_STARTED.register(s -> {
            server = s;
            SHADOW_CACHE.clear();
            loadedFromDisk = false;
            lastAutoSave = 0L;
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(ShadowCacheManager::save);
    }

    public static void clearCache() {
        SHADOW_CACHE.clear();
        loadedFromDisk = false;
    }

    public static ItemStack resolve(UUID shadowId, ItemStack loaded) {
        if (!loadedFromDisk && server != null) {
            load(server);
        }
        ItemStack master = SHADOW_CACHE.get(shadowId);
        if (master != null) {
            if (loaded.getCount() > master.getCount()) {
                master.setCount(loaded.getCount());
            }
            return master;
        }
        SHADOW_CACHE.put(shadowId, loaded);
        return loaded;
    }

    public static void maybeAutoSave() {
        if (server == null || SHADOW_CACHE.isEmpty()) return;
        long now = System.currentTimeMillis();
        if (now - lastAutoSave < AUTO_SAVE_INTERVAL_MS) return;
        lastAutoSave = now;
        save(server);
    }

    public static void save(MinecraftServer srv) {
        try {
            File file = getSaveFile(srv);
            if (SHADOW_CACHE.isEmpty()) {
                Files.deleteIfExists(file.toPath());
                return;
            }
            NbtCompound root = new NbtCompound();
            NbtList list = new NbtList();
            for (Map.Entry<UUID, ItemStack> entry : SHADOW_CACHE.entrySet()) {
                NbtCompound e = new NbtCompound();
                e.putUuid("id", entry.getKey());
                NbtCompound stackNbt = new NbtCompound();
                entry.getValue().writeNbt(stackNbt);
                e.put("stack", stackNbt);
                list.add(e);
            }
            root.put("shadows", list);
            NbtIo.writeCompressed(root, file);
        } catch (IOException ignored) {}
    }

    private static void load(MinecraftServer srv) {
        loadedFromDisk = true;
        try {
            File file = getSaveFile(srv);
            if (!file.exists()) return;
            NbtCompound root = NbtIo.readCompressed(file);
            NbtList list = root.getList("shadows", NbtElement.COMPOUND_TYPE);
            for (NbtElement el : list) {
                NbtCompound e = (NbtCompound) el;
                UUID id = e.getUuid("id");
                if (id == null || !e.contains("stack")) continue;
                ItemStack stack = ItemStack.fromNbt(e.getCompound("stack"));
                if (!stack.isEmpty()) {
                    SHADOW_CACHE.put(id, stack);
                }
            }
        } catch (Exception e) {
            SHADOW_CACHE.clear();
        }
    }

    private static File getSaveFile(MinecraftServer srv) {
        return srv.getSavePath(WorldSavePath.ROOT).resolve(FILE_NAME).toFile();
    }
}
