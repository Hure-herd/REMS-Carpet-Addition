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
import rems.carpet.utils.NegativeItemStacks.NegativeItemScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.GenericContainerScreenHandler;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;
import rems.carpet.utils.NegativeItemStacks.NegativeItemStacks;

@Mixin(GenericContainerScreenHandler.class)
public abstract class GenericContainerScreenHandlerMixin implements NegativeItemScreenHandler {
    @Unique private long rems$destroyedChestDeadline = -1;

    @Override
    public void rems$allowDestroyedChestUntil(long tick) {
        rems$destroyedChestDeadline = tick;
    }

    @Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
    private void allowLastChestClick(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (REMSSettings.negativeItemStacks && NegativeItemStacks.getWorld(player).getTime() < rems$destroyedChestDeadline) {
            cir.setReturnValue(true);
        }
    }
}
