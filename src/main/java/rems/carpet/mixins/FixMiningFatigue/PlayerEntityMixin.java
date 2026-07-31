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

package rems.carpet.mixins.FixMiningFatigue;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import rems.carpet.REMSSettings;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @ModifyConstant(
            method = "getBlockBreakingSpeed",
            constant = @Constant(floatValue = 0.0027F)
    )
    private float fixMiningFatigueLevel3(float original) {
        if (REMSSettings.fixMiningFatigue) {
            return 0.027F;
        }
        return original;
    }

    @ModifyConstant(
            method = "getBlockBreakingSpeed",
            constant = @Constant(floatValue = 8.1E-4F)
    )
    private float fixMiningFatigueLevel4(float original) {
        if (REMSSettings.fixMiningFatigue) {
            return 0.0081F;
        }
        return original;
    }
}
