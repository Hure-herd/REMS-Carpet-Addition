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

package rems.carpet.mixins.DispenserSpearCharge;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;

import java.util.List;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    @Inject(method = "getBehaviorForItem*", at = @At("HEAD"), cancellable = true)
    private void onGetSpearChargeBehavior(World world, ItemStack stack, CallbackInfoReturnable<DispenserBehavior> cir) {
        if(!REMSSettings.dispenserSpearCharge)return;
        Item item = stack.getItem();

        String itemPath = Registries.ITEM.getId(item).getPath();

        if (itemPath.endsWith("_spear")) {

            DispenserBehavior chargeBehavior = new ItemDispenserBehavior() {
                @Override
                protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                    ServerWorld world = pointer.world();
                    Direction direction = pointer.state().get(DispenserBlock.FACING);

                    double chargeDistance = 3.0;

                    Box hitBox = new Box(pointer.pos()).stretch(
                            direction.getOffsetX() * chargeDistance,
                            direction.getOffsetY() * chargeDistance,
                            direction.getOffsetZ() * chargeDistance
                    );

                    List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, hitBox, entity -> true);

                    boolean hitSomething = false;
                    for (LivingEntity target : targets) {
                        target.damage(world, world.getDamageSources().generic(), 6.0f);
                        world.spawnParticles(ParticleTypes.SWEEP_ATTACK,
                                target.getX(), target.getBodyY(0.5), target.getZ(),
                                1, 0, 0, 0, 0);

                        hitSomething = true;
                    }
                    world.playSound(null, pointer.pos(), SoundEvents.ITEM_SPEAR_ATTACK.value(), SoundCategory.BLOCKS, 1.0f, 1.0f);
                    if (hitSomething) {
                        stack.damage(1, world, null, brokenItem -> {});
                    }
                    return stack;
                }
            };
            cir.setReturnValue(chargeBehavior);
        }
    }
}
