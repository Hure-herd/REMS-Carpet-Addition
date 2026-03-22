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

package rems.carpet.utils.DisableAi;

import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.SlimeEntity;
import rems.carpet.REMSSettings;

import java.util.*;

public class AiGoalRegistrar {

    public static void initialize() {
        register("move", WanderAroundGoal.class, EscapeDangerGoal.class,
                SwimGoal.class, FlyGoal.class, BreatheAirGoal.class,
                DiveJumpingGoal.class, DolphinJumpGoal.class,
                SlimeEntity.MoveGoal.class, SlimeEntity.SwimmingGoal.class,
                WanderAroundPointOfInterestGoal.class, GoToWalkTargetGoal.class,
                StrollTask.class,
                WalkTowardsPosTask.class,
                GoToPointOfInterestTask.class,
                GoToSecondaryPositionTask.class,
                FleeTask.class,
                LongJumpTask.class,
                HuntHoglinTask.class
                );
        register("look", LookAtEntityGoal.class, LookAroundGoal.class,
                SlimeEntity.FaceTowardTargetGoal.class, SlimeEntity.RandomLookGoal.class,
                ActiveTargetGoal.class, UniversalAngerGoal.class, RevengeGoal.class,
                LookAtCustomerGoal.class,LookAtMobTask.class,
                LookAtMobTask.class,
                LookAtDisturbanceTask.class,
                LookAtCustomerGoal.class,
                WantNewItemTask.class,
                LookAtMobTask.class,
                AdmireItemTask.class
                );

        register("attack",
                MeleeAttackGoal.class, ProjectileAttackGoal.class,
                CreeperIgniteGoal.class, BowAttackGoal.class,
                CrossbowAttackGoal.class, PounceAtTargetGoal.class,
                RaidGoal.class,
                MeleeAttackTask.class,
                CrossbowAttackTask.class,
                RoarTask.class,
                SonicBoomTask.class,
                SniffTask.class,
                RamImpactTask.class
        );

        register("avoid_flee", FleeEntityGoal.class);
        register("avoid_sun", EscapeSunlightGoal.class, AvoidSunlightGoal.class);

        register("break_door", BreakDoorGoal.class);
        register("eat_grass", EatGrassGoal.class);

        register("breed", AnimalMateGoal.class);
        register("tempt", TemptGoal.class);
        register("follow", FollowParentGoal.class, FollowOwnerGoal.class);

        register("work",
                VillagerWorkTask.class,
                FarmerWorkTask.class,
                BoneMealTask.class,
                GatherItemsVillagerTask.class,
                GiveGiftsToHeroTask.class,
                VillagerBreedTask.class,
                PlayWithVillagerBabiesTask.class,
                SleepTask.class,
                WakeUpTask.class,
                PanicTask.class,
                CelebrateRaidWinTask.class,
                AdmireItemTask.class,
                GiveInventoryToLookTargetTask.class,
                TemptTask.class,
                LayFrogSpawnTask.class,
                CroakTask.class,
                PlayDeadTask.class
        );
        generateAllOption();
    }

    private static void register(String key, Class<?>... goals) {
        REMSSettings.GOAL_MAPPING.put(key, Arrays.asList(goals));
    }

    private static void generateAllOption() {
        Set<Class<?>> allGoalsSet = new HashSet<>();
        for (List<Class<?>> list : REMSSettings.GOAL_MAPPING.values()) {
            allGoalsSet.addAll(list);
        }
        REMSSettings.GOAL_MAPPING.put("all", new ArrayList<>(allGoalsSet));
    }
}
