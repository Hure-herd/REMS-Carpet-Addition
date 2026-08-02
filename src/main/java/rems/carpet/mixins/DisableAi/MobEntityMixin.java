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

package rems.carpet.mixins.DisableAi;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

import java.util.*;

import net.minecraft.text.Text;
import java.util.Set;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    @Final @Shadow protected GoalSelector goalSelector;
    @Final @Shadow protected GoalSelector targetSelector;

    @Shadow public abstract boolean isAiDisabled();

    @Shadow protected abstract void initGoals();

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

@Inject(method = "tick", at = @At("HEAD"))
private void manageDynamicAi(CallbackInfo ci) {
    if (this.getWorld().isClient) return;

    GoalSelectorAccessor goalAccessor = (GoalSelectorAccessor) this.goalSelector;
    GoalSelectorAccessor targetAccessor = (GoalSelectorAccessor) this.targetSelector;
    Set<PrioritizedGoal> currentGoals = goalAccessor.getGoals();

    boolean isTargetEntity = REMSSettings.NO_AI_TYPES.contains(this.getType());
    boolean hasDisabledGoals = !REMSSettings.DISABLED_GOAL_CLASSES.isEmpty();
    boolean nameTagDisabled = false;

    if (isTargetEntity && hasDisabledGoals) {
        removeDisabled(goalAccessor, targetAccessor, REMSSettings.DISABLED_GOAL_CLASSES);
    }

    Text name = this.getCustomName();
    if (name != null) {
        String nameStr = name.getString().toLowerCase();
        for (Map.Entry<String, List<Class<?>>> e : REMSSettings.GOAL_MAPPING.entrySet()) {
            if (!e.getKey().equals("all") && nameStr.contains(e.getKey())) {
                removeDisabled(goalAccessor, targetAccessor, e.getValue());
                nameTagDisabled = true;
            }
        }
    }

    if (!isTargetEntity && !nameTagDisabled && currentGoals.isEmpty() && !this.isAiDisabled()) {
        this.initGoals();
    }
}

private static void removeDisabled(GoalSelectorAccessor goals, GoalSelectorAccessor targets, Collection<Class<?>> disabled) {
    goals.getGoals().removeIf(g -> {
        for (Class<?> c : disabled) if (c.isInstance(g.getGoal())) return true;
        return false;
    });
    targets.getGoals().removeIf(g -> {
        for (Class<?> c : disabled) if (c.isInstance(g.getGoal())) return true;
        return false;
    });
}
}
