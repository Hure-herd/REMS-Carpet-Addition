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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import rems.carpet.REMSSettings;
import rems.carpet.utils.ComponentTranslate;
import carpet.CarpetSettings;

import java.util.UUID;

//#if MC>=12100
//$$ import net.minecraft.component.DataComponentTypes;
//$$ import net.minecraft.component.type.NbtComponent;
//#endif
//#if MC>=12104
//$$ import net.minecraft.entity.SpawnReason;
//#endif
//#if MC>=12108
//$$ import net.minecraft.storage.NbtReadView;
//$$ import net.minecraft.storage.NbtWriteView;
//$$ import net.minecraft.util.ErrorReporter;
//#endif

public final class PetCapture {
    public static final String PAYLOAD = "rems:pet_capture";
    public static final String PROJECTILE = "rems:capture_eye";
    public static final String OWNER = "rems:pet_owner";
    public static final String SITTING = "rems:pet_sitting";

    public static World world(Entity entity) {
        //#if MC>=12110
        //$$ return entity.getEntityWorld();
        //#else
        return entity.getWorld();
        //#endif
    }

    public static NbtCompound data(ItemStack stack) {
        //#if MC>=12100
        //$$ NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        //$$ return component == null ? new NbtCompound() : component.copyNbt();
        //#else
        return stack.hasNbt() ? stack.getNbt().copy() : new NbtCompound();
        //#endif
    }

    public static void setData(ItemStack stack, NbtCompound data) {
        //#if MC>=12100
        //$$ stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(data));
        //#else
        stack.setNbt(data);
        //#endif
    }

    public static String string(NbtCompound data, String key) {
        //#if MC>=12108
        //$$ return data.getString(key, "");
        //#else
        return data.getString(key);
        //#endif
    }

    public static NbtCompound compound(NbtCompound data, String key) {
        //#if MC>=12108
        //$$ return data.getCompoundOrEmpty(key);
        //#else
        return data.getCompound(key);
        //#endif
    }

    public static UUID uuid(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException invalid) {
            return null;
        }
    }

    public static boolean isEye(ItemStack stack) {
        if (stack.getItem() != Items.ENDER_EYE || stack.isEmpty()) return false;
        //#if MC>=12100
        //$$ if (stack.get(DataComponentTypes.CUSTOM_NAME) == null) return false;
        //#else
        if (!stack.hasCustomName()) return false;
        //#endif
        String name = stack.getName().getString().trim();
        return name.equals("收服之眼") || name.equalsIgnoreCase("Capture Eye");
    }

    public static boolean isEgg(ItemStack stack) {
        return stack.getItem() instanceof SpawnEggItem && data(stack).contains(PAYLOAD);
    }

    public static boolean isProjectile(ItemStack stack) {
        return data(stack).contains(PROJECTILE);
    }

    public static boolean throwEye(PlayerEntity player, ItemStack stack) {
        if (!(world(player) instanceof ServerWorld serverWorld)) return false;
        ItemStack thrown = stack.copyWithCount(1);
        NbtCompound data = data(thrown);
        data.putBoolean(PROJECTILE, true);
        setData(thrown, data);
        //#if MC>=12104
        //$$ SnowballEntity projectile = new SnowballEntity(serverWorld, player, thrown);
        //#else
        SnowballEntity projectile = new SnowballEntity(EntityType.SNOWBALL, serverWorld);
        //#endif
        projectile.setOwner(player);
        projectile.setItem(thrown);
        projectile.setPosition(player.getX(), player.getEyeY() - 0.1, player.getZ());
        projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 1.5F, 1.0F);
        if (!serverWorld.spawnEntity(projectile)) return false;
        if (!player.isCreative()) stack.decrement(1);
        //#if MC>=12104
        //$$ player.getItemCooldownManager().set(thrown, 10);
        //#else
        player.getItemCooldownManager().set(Items.ENDER_EYE, 10);
        //#endif
        return true;
    }

    public static UUID owner(MobEntity mob) {
        UUID custom = ((CapturedPet) mob).rems$getPetOwner();
        if (custom != null) return custom;
        if (mob instanceof TameableEntity tameable && tameable.isTamed()) {
            //#if MC>=12108
            //$$ return tameable.getOwnerReference() == null ? null : tameable.getOwnerReference().getUuid();
            //#else
            return tameable.getOwnerUuid();
            //#endif
        }
        if (mob instanceof AbstractHorseEntity horse && horse.isTame()) {
            //#if MC>=12108
            //$$ return horse.getOwnerReference() == null ? null : horse.getOwnerReference().getUuid();
            //#else
            return horse.getOwnerUuid();
            //#endif
        }
        return null;
    }

    public static boolean canCapture(MobEntity mob, PlayerEntity player) {
        UUID owner = owner(mob);
        return mob.isAlive() && !mob.isRemoved() && !mob.hasPassengers() && !mob.hasVehicle()
                && (owner == null || owner.equals(player.getUuid()))
                && (!(mob instanceof TameableEntity tameable) || !tameable.isTamed() || owner != null)
                && (!(mob instanceof AbstractHorseEntity horse) || !horse.isTame() || owner != null);
    }

    public static void assignOwner(MobEntity mob, PlayerEntity player) {
        ((CapturedPet) mob).rems$setPetOwner(player.getUuid());
        ((CapturedPet) mob).rems$setPetSitting(false);
        mob.setPersistent();
        mob.setTarget(null);
        mob.setAiDisabled(false);
        if (mob instanceof net.minecraft.entity.boss.WitherEntity wither) {
            wither.setInvulTimer(0);
            for (int head = 0; head < 3; head++) wither.setTrackedEntityId(head, 0);
        }
        if (mob instanceof net.minecraft.entity.boss.dragon.EnderDragonEntity dragon) dragon.setFight(null);
        if (mob instanceof TameableEntity tameable) {
            //#if MC>=12100
            //$$ tameable.setTamed(true, true);
            //#else
            tameable.setTamed(true);
            //#endif
            tameable.setOwner(player);
            tameable.setSitting(false);
        }
        if (mob instanceof AbstractHorseEntity horse) {
            //#if MC>=12108
            //$$ horse.setOwner(player);
            //#else
            horse.setOwnerUuid(player.getUuid());
            //#endif
            horse.setTame(true);
        }
    }

    public static NbtCompound save(MobEntity mob) {
        //#if MC>=12108
        //$$ NbtWriteView view = NbtWriteView.create(ErrorReporter.EMPTY, world(mob).getRegistryManager());
        //$$ if (!mob.saveSelfData(view)) return null;
        //$$ NbtCompound saved = view.getNbt();
        //#else
        NbtCompound saved = new NbtCompound();
        if (!mob.saveSelfNbt(saved)) return null;
        //#endif
        saved.remove("Passengers");
        saved.remove("Leash");
        saved.remove("leash");
        if (mob instanceof net.minecraft.entity.mob.CreeperEntity) saved.putBoolean("ignited", false);
        return saved;
    }

    public static ItemStack egg(MobEntity mob, PlayerEntity player) {
        NbtCompound saved = save(mob);
        if (saved == null) return ItemStack.EMPTY;
        saved.putString(OWNER, player.getUuidAsString());
        saved.putBoolean(SITTING, false);
        //#if MC>=260100
        //$$ ItemStack egg = SpawnEggItem.forEntity(mob.getType()).map(entry -> new ItemStack(entry, 1)).orElseGet(() -> new ItemStack(Items.PIG_SPAWN_EGG));
        //#else
        SpawnEggItem item = SpawnEggItem.forEntity(mob.getType());
        ItemStack egg = new ItemStack(item == null ? Items.PIG_SPAWN_EGG : item);
        //#endif
        NbtCompound payload = new NbtCompound();
        payload.putInt("version", 1);
        payload.putString("owner", player.getUuidAsString());
        payload.putString("uuid", mob.getUuidAsString());
        payload.put("entity", saved);
        NbtCompound data = new NbtCompound();
        data.put(PAYLOAD, payload);
        setData(egg, data);
        Text name = petEggName(mob, player);
        //#if MC>=12100
        //$$ egg.set(DataComponentTypes.CUSTOM_NAME, name);
        //$$ egg.set(DataComponentTypes.MAX_STACK_SIZE, 1);
        //#else
        egg.setCustomName(name);
        //#endif
        return egg;
    }

    private static Text petEggName(MobEntity mob, PlayerEntity player) {
        String language = CarpetSettings.language == null ? "en_us" : CarpetSettings.language.toLowerCase();
        boolean chinese = language.startsWith("zh");
        String prefix = chinese ? "宠物" : "Pet: ";
        Text officialName = mob.getName();
        Text customName = mob.getCustomName();
        Text displayedName = customName == null ? officialName : customName;
        return Text.literal(prefix).append(displayedName);
    }

    public static boolean capture(MobEntity mob, PlayerEntity player, float roll) {
        if (!REMSSettings.petCapture || !canCapture(mob, player) || !(world(mob) instanceof ServerWorld serverWorld)) return false;
        PetCaptureRegistry.observe(mob);
        PetCaptureRegistry registry = PetCaptureRegistry.get(serverWorld.getServer());
        if (!registry.isAvailable()) {
            notify(player, "petCapture.storage_failed");
            return false;
        }
        String type = PetCaptureRegistry.type(mob);
        if (!registry.canAcquire(mob.getUuid(), player.getUuid(), type)) {
            notify(player, "petCapture.limit", REMSSettings.petCaptureLimit);
            return false;
        }
        if (roll >= 0.25F) {
            notify(player, "petCapture.failed");
            return false;
        }
        ItemStack egg = egg(mob, player);
        if (egg.isEmpty()) return false;
        net.minecraft.entity.ItemEntity drop = new net.minecraft.entity.ItemEntity(serverWorld, mob.getX(), mob.getY(), mob.getZ(), egg);
        drop.setPickupDelay(0);
        if (!serverWorld.spawnEntity(drop)) return false;
        if (!registry.acquire(mob.getUuid(), player.getUuid(), type)) {
            drop.discard();
            notify(player, "petCapture.storage_failed");
            return false;
        }
        mob.discard();
        notify(player, "petCapture.success");
        return true;
    }

    public static boolean reclaim(MobEntity mob, PlayerEntity player) {
        if (!REMSSettings.petCapture || !canCapture(mob, player)
                || !player.isSneaking() || !(world(mob) instanceof ServerWorld serverWorld)) return false;
        UUID owner = ((CapturedPet) mob).rems$getPetOwner();
        if (owner == null || !owner.equals(player.getUuid())) return false;
        ItemStack egg = egg(mob, player);
        if (egg.isEmpty()) return false;
        PetCaptureRegistry registry = PetCaptureRegistry.get(serverWorld.getServer());
        if (!registry.isAvailable()) {
            notify(player, "petCapture.storage_failed");
            return false;
        }
        net.minecraft.entity.ItemEntity drop = new net.minecraft.entity.ItemEntity(serverWorld, mob.getX(), mob.getY(), mob.getZ(), egg);
        drop.setPickupDelay(0);
        if (!serverWorld.spawnEntity(drop)) return false;
        mob.discard();
        notify(player, "petCapture.reclaimed");
        return true;
    }

    public static boolean release(ItemStack egg, PlayerEntity player, Vec3d position) {
        if (!REMSSettings.petCapture || !isEgg(egg) || egg.getCount() <= 0 || !(world(player) instanceof ServerWorld serverWorld)) return false;
        NbtCompound payload = compound(data(egg), PAYLOAD);
        //#if MC>=12108
        //$$ if (payload.getInt("version", 0) != 1) return false;
        //#else
        if (payload.getInt("version") != 1) return false;
        //#endif
        UUID owner = uuid(string(payload, "owner"));
        UUID entityUuid = uuid(string(payload, "uuid"));
        if (owner == null || entityUuid == null || !owner.equals(player.getUuid())) return false;
        for (ServerWorld dimension : serverWorld.getServer().getWorlds()) {
            if (dimension.getEntity(entityUuid) != null) return false;
        }
        NbtCompound saved = compound(payload, "entity").copy();
        Identifier id = Identifier.tryParse(string(saved, "id"));
        if (id == null || !Registries.ENTITY_TYPE.containsId(id)) return false;
        //#if MC>=12104
        //$$ EntityType<?> type = Registries.ENTITY_TYPE.get(id);
        //#else
        EntityType<?> type = Registries.ENTITY_TYPE.get(id);
        //#endif
        //#if MC>=12104
        //$$ Entity entity = type.create(serverWorld, SpawnReason.SPAWN_ITEM_USE);
        //#else
        Entity entity = type.create(serverWorld);
        //#endif
        if (!(entity instanceof MobEntity mob)) return false;
        saved.remove("Passengers");
        saved.remove("Leash");
        saved.remove("leash");
        try {
            //#if MC>=12108
            //$$ mob.readData(NbtReadView.create(ErrorReporter.EMPTY, serverWorld.getRegistryManager(), saved));
            //#else
            mob.readNbt(saved);
            //#endif
        } catch (RuntimeException invalidData) {
            return false;
        }
        mob.setUuid(entityUuid);
        mob.refreshPositionAndAngles(position.x, position.y, position.z, player.getYaw(), 0.0F);
        mob.setVelocity(Vec3d.ZERO);
        assignOwner(mob, player);
        if (!mob.isAlive() || !serverWorld.getWorldBorder().contains(mob.getBoundingBox())
                || !serverWorld.isSpaceEmpty(mob, mob.getBoundingBox())) return false;
        PetCaptureRegistry registry = PetCaptureRegistry.get(serverWorld.getServer());
        String typeId = PetCaptureRegistry.type(mob);
        if (!registry.isAvailable()) {
            notify(player, "petCapture.storage_failed");
            return false;
        }
        if (!registry.matches(entityUuid, owner, typeId)) return false;
        if (!registry.canAcquire(entityUuid, owner, typeId)) {
            notify(player, "petCapture.limit", REMSSettings.petCaptureLimit);
            return false;
        }
        boolean registered = registry.contains(entityUuid);
        if (!registry.acquire(entityUuid, owner, typeId)) {
            notify(player, "petCapture.storage_failed");
            return false;
        }
        if (!serverWorld.spawnEntity(mob)) {
            if (!registered) registry.remove(entityUuid, owner);
            return false;
        }
        egg.decrement(1);
        return true;
    }

    public static void tickPet(MobEntity mob) {
        CapturedPet pet = (CapturedPet) mob;
        mob.setTarget(null);
        PlayerEntity owner = world(mob).getPlayerByUuid(pet.rems$getPetOwner());
        if (pet.rems$isPetSitting() || owner == null || !owner.isAlive()) {
            mob.getNavigation().stop();
            mob.setVelocity(0.0, mob.getVelocity().y, 0.0);
            return;
        }
        mob.getLookControl().lookAt(owner, 30.0F, 30.0F);
        if (mob.squaredDistanceTo(owner) > 9.0 && mob.squaredDistanceTo(owner) < 16384.0) {
            mob.getNavigation().startMovingTo(owner, 1.1);
        } else {
            mob.getNavigation().stop();
        }
        mob.getNavigation().tick();
        mob.getMoveControl().tick();
        mob.getLookControl().tick();
        mob.getJumpControl().tick();
    }

    private static void notify(PlayerEntity player, String key, Object... arguments) {
        //#if MC>=260100
        //$$ player.sendOverlayMessage(ComponentTranslate.tr(key, arguments));
        //#else
        player.sendMessage(ComponentTranslate.tr(key, arguments), true);
        //#endif
    }

    private PetCapture() {
    }
}
