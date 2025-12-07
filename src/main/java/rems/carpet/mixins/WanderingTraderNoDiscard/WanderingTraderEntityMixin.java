/*
 * This file is part of the Carpet REMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 A Minecraft Server and contributors
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

package rems.carpet.mixins.WanderingTraderNoDiscard;

import net.minecraft.entity.passive.WanderingTraderEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

import java.util.Objects;

@Mixin(WanderingTraderEntity.class)
public class WanderingTraderEntityMixin {
    @Inject(
            method = "tickDespawnDelay",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void tickDespawnDelay(CallbackInfo ci){
        WanderingTraderEntity self = (WanderingTraderEntity) (Object) this;
        if(REMSSettings.wanderingTraderNoDisappear && "Load".equals(Objects.requireNonNull(self.getCustomName()).getString())){
            ci.cancel();
        }
    }
}
