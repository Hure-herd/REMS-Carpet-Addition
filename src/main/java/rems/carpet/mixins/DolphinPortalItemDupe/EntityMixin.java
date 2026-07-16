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

package rems.carpet.mixins.DolphinPortalItemDupe;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(
            method = "removeFromDimension",
            at = @At("HEAD")
    )
    private void dropItemsBeforeCrossDimension(CallbackInfo ci) {
        if (!REMSSettings.reintroduceDolphinPortalItemDupe) {
            return;
        }
        Entity self = (Entity) (Object) this;
        World world = self.getEntityWorld();
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        if (self instanceof LivingEntity living && !(self instanceof PlayerEntity)) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                //#if MC<260100
                ItemStack stack = living.getEquippedStack(slot);
                //#else
                //$$ ItemStack stack = living.getEquipment(slot);
                //#endif
                if (!stack.isEmpty()) {
                    //#if MC<260100
                    living.equipStack(slot, ItemStack.EMPTY);
                    //#else
                    //$$ living.setEquipment(slot, ItemStack.EMPTY);
                    //#endif
                    //#if MC<12104
                    self.dropStack(stack);
                    //#else
                    //$$ self.dropStack(serverWorld, stack);
                    //#endif
                }
            }
        }
    }
}
