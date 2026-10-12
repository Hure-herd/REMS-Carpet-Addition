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

package rems.carpet.utils.PetCapture;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import rems.carpet.REMSSettings;
import rems.carpet.REMSServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public final class PetCaptureRegistry {
    private static final Map<MinecraftServer, PetCaptureRegistry> REGISTRIES = new WeakHashMap<>();
    private final Map<UUID, Pet> pets = new HashMap<>();
    private final Path file;
    private boolean available = true;

    private record Pet(UUID owner, String type) {}

    private PetCaptureRegistry(Path file) {
        this.file = file;
        if (!Files.exists(file)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
            if (root.get("version").getAsInt() != 1) throw new IOException("Unsupported pet registry version");
            for (var entry : root.getAsJsonObject("pets").entrySet()) {
                UUID id = UUID.fromString(entry.getKey());
                JsonObject data = entry.getValue().getAsJsonObject();
                UUID owner = UUID.fromString(data.get("owner").getAsString());
                String type = data.get("type").getAsString();
                if (Identifier.tryParse(type) == null) throw new IOException("Invalid pet type: " + type);
                pets.put(id, new Pet(owner, type));
            }
        } catch (IOException | RuntimeException invalid) {
            available = false;
            REMSServer.LOGGER.error("Cannot load pet capture registry {}; capture and transfer are blocked", file, invalid);
        }
    }

    public static void initialize() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof MobEntity mob) observe(mob);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(PetCaptureRegistry::clear);
    }

    public static PetCaptureRegistry get(MinecraftServer server) {
        return REGISTRIES.computeIfAbsent(server, current -> new PetCaptureRegistry(
                current.getSavePath(WorldSavePath.ROOT).resolve("data/rems_pet_capture.json")));
    }

    public static void clear(MinecraftServer server) {
        REGISTRIES.remove(server);
    }

    public long count(UUID owner, String type) {
        return pets.values().stream().filter(pet -> pet.owner().equals(owner) && pet.type().equals(type)).count();
    }

    public boolean canAcquire(UUID id, UUID owner, String type) {
        if (!available) return false;
        Pet existing = pets.get(id);
        return new Pet(owner, type).equals(existing) || REMSSettings.petCaptureLimit == 0
                || count(owner, type) < REMSSettings.petCaptureLimit;
    }

    public boolean acquire(UUID id, UUID owner, String type) {
        return canAcquire(id, owner, type) && record(id, new Pet(owner, type));
    }

    private boolean record(UUID id, Pet pet) {
        if (!available) return false;
        Pet previous = pets.get(id);
        if (pet.equals(previous)) return true;
        pets.put(id, pet);
        if (save()) return true;
        if (previous == null) pets.remove(id);
        else pets.put(id, previous);
        return false;
    }

    public boolean remove(UUID id, UUID owner) {
        Pet previous = pets.get(id);
        if (!available || previous == null || !previous.owner().equals(owner)) return false;
        pets.remove(id);
        if (save()) return true;
        pets.put(id, previous);
        return false;
    }

    public boolean contains(UUID id) {
        return pets.containsKey(id);
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean matches(UUID id, UUID owner, String type) {
        Pet existing = pets.get(id);
        return existing == null || existing.equals(new Pet(owner, type));
    }

    public static String type(MobEntity mob) {
        return Registries.ENTITY_TYPE.getId(mob.getType()).toString();
    }

    public static void observe(MobEntity mob) {
        UUID owner = ((CapturedPet) mob).rems$getPetOwner();
        if (owner == null || !mob.isAlive() || !(PetCapture.world(mob) instanceof ServerWorld world)) return;
        PetCaptureRegistry registry = get(world.getServer());
        if (!registry.contains(mob.getUuid())) registry.record(mob.getUuid(), new Pet(owner, type(mob)));
    }

    public static void onDeath(MobEntity mob) {
        UUID owner = ((CapturedPet) mob).rems$getPetOwner();
        if (owner != null && PetCapture.world(mob) instanceof ServerWorld world) {
            get(world.getServer()).remove(mob.getUuid(), owner);
        }
    }

    private boolean save() {
        JsonObject root = new JsonObject();
        root.addProperty("version", 1);
        JsonObject records = new JsonObject();
        pets.forEach((id, pet) -> {
            JsonObject data = new JsonObject();
            data.addProperty("owner", pet.owner().toString());
            data.addProperty("type", pet.type());
            records.add(id.toString(), data);
        });
        root.add("pets", records);
        Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(temporary, root.toString(), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException unsupported) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException | RuntimeException failure) {
            REMSServer.LOGGER.error("Cannot save pet capture registry {}", file, failure);
            return false;
        }
    }
}
