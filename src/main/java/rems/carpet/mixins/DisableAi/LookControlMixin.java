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

package rems.carpet.mixins.DisableAi;

import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

@Mixin(LookControl.class)
public class LookControlMixin {

    @Final @Shadow protected MobEntity entity;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void stopLooking(CallbackInfo ci) {
        boolean isTarget = REMSSettings.NO_AI_TYPES.contains(this.entity.getType());
        boolean isLookDisabled = REMSSettings.DISABLED_GOAL_CLASSES.contains(
                net.minecraft.entity.ai.goal.LookAtEntityGoal.class
        ) || REMSSettings.DISABLED_GOAL_CLASSES.contains(
                net.minecraft.entity.ai.brain.task.LookAtMobTask.class
        );

        boolean nameLook = false;
        if (this.entity.getCustomName() != null) {
            nameLook = this.entity.getCustomName().getString().toLowerCase().contains("look");
        }
        if (isLookDisabled && (isTarget || nameLook)) {
            ci.cancel();
        }
    }
}