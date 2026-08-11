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

package rems.carpet.utils.DisableAi;

import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.raid.RaiderEntity;
import rems.carpet.REMSSettings;

import java.util.*;

public class AiGoalRegistrar {

    public static void initialize() {
        register("move",
                WanderAroundGoal.class, WanderAroundFarGoal.class, WanderNearTargetGoal.class,
                SwimGoal.class, SwimAroundGoal.class, FlyGoal.class, BreatheAirGoal.class,
                DiveJumpingGoal.class, DolphinJumpGoal.class, MoveIntoWaterGoal.class,
                EscapeDangerGoal.class, PowderSnowJumpGoal.class, AmbientStandGoal.class,
                MoveThroughVillageGoal.class, MoveToTargetPosGoal.class,
                GoToWalkTargetGoal.class, GoToVillageGoal.class, GoToBedAndSleepGoal.class,
                FollowGroupLeaderGoal.class, FollowMobGoal.class, StepAndDestroyBlockGoal.class,
                SlimeEntity.MoveGoal.class, SlimeEntity.SwimmingGoal.class,
                FollowParentGoal.class, FollowOwnerGoal.class, FleeEntityGoal.class,
                EatGrassGoal.class, EscapeSunlightGoal.class, AvoidSunlightGoal.class,
                StrollTask.class, WalkTowardsPosTask.class, WalkTowardsLandTask.class,
                WalkTowardsWaterTask.class, WanderAroundTask.class, WanderIndoorsTask.class,
                WalkHomeTask.class, WalkTowardJobSiteTask.class, WalkTowardsLookTargetTask.class,
                WalkToNearestVisibleWantedItemTask.class, GoToNearbyPositionTask.class,
                GoToRememberedPositionTask.class, GoToIfNearbyTask.class,
                GoToPointOfInterestTask.class, GoToSecondaryPositionTask.class,
                GoTowardsLookTargetTask.class, RidingTask.class,
                DismountVehicleTask.class, StayAboveWaterTask.class, SeekWaterTask.class,
                SeekSkyTask.class, OpenDoorsTask.class, HoldTradeOffersTask.class,
                EmergeTask.class, WalkTowardClosestAdultTask.class, BiasedLongJumpTask.class,
                FleeTask.class, PanicTask.class, StopPanickingTask.class,
                HideInHomeTask.class, HideWhenBellRingsTask.class,
                PlayDeadTask.class, PlayDeadTimerTask.class, PacifyTask.class,
                FollowCustomerTask.class,
                // Phantom
                PhantomEntity.MovementGoal.class, PhantomEntity.CircleMovementGoal.class,
                PhantomEntity.SwoopMovementGoal.class,
                // Bee
                BeeEntity.BeeWanderAroundGoal.class, BeeEntity.FindHiveGoal.class,
                BeeEntity.MoveToFlowerGoal.class, BeeEntity.MoveToHiveGoal.class,
                BeeEntity.EnterHiveGoal.class, BeeEntity.PollinateGoal.class,
                BeeEntity.GrowCropsGoal.class,
                // Dolphin
                DolphinEntity.LeadToNearbyTreasureGoal.class,
                DolphinEntity.PlayWithItemsGoal.class, DolphinEntity.SwimWithPlayerGoal.class,
                // Fish
                FishEntity.SwimToRandomPlaceGoal.class,
                // Fox
                FoxEntity.FoxSwimGoal.class, FoxEntity.GoToVillageGoal.class,
                FoxEntity.MoveToHuntGoal.class, FoxEntity.StopWanderingGoal.class,
                FoxEntity.EscapeWhenNotAggressiveGoal.class, FoxEntity.AvoidDaylightGoal.class,
                FoxEntity.FollowParentGoal.class,
                // Ghast
                GhastEntity.FlyRandomlyGoal.class,
                // Parrot
                ParrotEntity.FlyOntoTreeGoal.class,
                // Patrol
                PatrolEntity.PatrolGoal.class,
                // Raider
                RaiderEntity.PatrolApproachGoal.class,
                // Rabbit
                RabbitEntity.EscapeDangerGoal.class, RabbitEntity.FleeGoal.class,
                RabbitEntity.EatCarrotCropGoal.class,
                // Turtle
                TurtleEntity.GoHomeGoal.class, TurtleEntity.WanderInWaterGoal.class,
                TurtleEntity.WanderOnLandGoal.class, TurtleEntity.TravelGoal.class,
                TurtleEntity.TurtleEscapeDangerGoal.class,
                // Drowned
                DrownedEntity.WanderAroundOnSurfaceGoal.class, DrownedEntity.LeaveWaterGoal.class,
                // Squid
                SquidEntity.SwimGoal.class,
                // Strider
                StriderEntity.GoBackToLavaGoal.class,
                // Cat
                CatEntity.CatFleeGoal.class,
                // Ocelot
                OcelotEntity.FleeGoal.class,
                // Panda
                PandaEntity.PandaFleeGoal.class, PandaEntity.PandaEscapeDangerGoal.class,
                PandaEntity.LieOnBackGoal.class, PandaEntity.PlayGoal.class,
                PandaEntity.SneezeGoal.class,
                // Silverfish
                SilverfishEntity.WanderAndInfestGoal.class,
                // Enderman
                EndermanEntity.TeleportTowardsPlayerGoal.class,
                // 仅 1.20.1 存在的类
                //#if MC<12100
                PolarBearEntity.PolarBearEscapeDangerGoal.class,
                WolfEntity.WolfEscapeDangerGoal.class,
                //#endif
                PolarBearEntity.PolarBearRevengeGoal.class,
                PolarBearEntity.ProtectBabiesGoal.class
        );

        register("look",
                LookAtEntityGoal.class, LookAroundGoal.class, StopAndLookAtEntityGoal.class,
                SlimeEntity.FaceTowardTargetGoal.class, SlimeEntity.RandomLookGoal.class,
                IronGolemLookGoal.class, IronGolemWanderAroundGoal.class,
                LookAtMobTask.class, LookAtMobWithIntervalTask.class,
                LookAtDisturbanceTask.class, LookAroundTask.class, RandomLookAroundTask.class,
                AdmireItemTask.class, WantNewItemTask.class,
                SingleTickTask.class, MultiTickTask.class, WaitTask.class,
                RandomTask.class, CompositeTask.class, MemoryTransferTask.class,
                ScheduleActivityTask.class, FindWalkTargetTask.class,
                FindInteractionTargetTask.class, FindPointOfInterestTask.class,
                FindEntityTask.class,
                // Bee
                BeeEntity.NotAngryGoal.class,
                // Fox
                FoxEntity.SitDownAndLookAroundGoal.class, FoxEntity.LookAtEntityGoal.class,
                // Panda
                PandaEntity.LookAtEntityGoal.class,
                // Llama
                LlamaEntity.SpitRevengeGoal.class,
                // Shulker
                ShulkerEntity.PeekGoal.class
        );

        register("attack",
                MeleeAttackGoal.class, ProjectileAttackGoal.class, ZombieAttackGoal.class,
                CreeperIgniteGoal.class, BowAttackGoal.class, CrossbowAttackGoal.class,
                PounceAtTargetGoal.class, AttackGoal.class, AttackWithOwnerGoal.class,
                ActiveTargetGoal.class, UniversalAngerGoal.class, RevengeGoal.class,
                RaidGoal.class, UntamedActiveTargetGoal.class, DisableableFollowTargetGoal.class,
                TrackIronGolemTargetGoal.class, TrackOwnerAttackerGoal.class,
                BreakDoorGoal.class, DoorInteractGoal.class, LongDoorInteractGoal.class,
                MeleeAttackTask.class, CrossbowAttackTask.class, AttackTask.class,
                RoarTask.class, SonicBoomTask.class, SniffTask.class,
                RamImpactTask.class, LeapingChargeTask.class, DefeatTargetTask.class,
                FindRoarTargetTask.class, PrepareRamTask.class, RangedApproachTask.class,
                StartSniffingTask.class, DigTask.class, HuntFinishTask.class,
                HuntHoglinTask.class,
                // Phantom
                PhantomEntity.StartAttackGoal.class, PhantomEntity.FindTargetGoal.class,
                // Bee
                BeeEntity.StingGoal.class, BeeEntity.BeeRevengeGoal.class,
                // Blaze
                BlazeEntity.ShootFireballGoal.class,
                // Cat
                CatEntity.CatFleeGoal.class,
                // Drowned
                DrownedEntity.DrownedAttackGoal.class, DrownedEntity.TridentAttackGoal.class,
                DrownedEntity.TargetAboveWaterGoal.class,
                // Enderman
                EndermanEntity.ChasePlayerGoal.class,
                // Evoker
                EvokerEntity.ConjureFangsGoal.class, EvokerEntity.SummonVexGoal.class,
                EvokerEntity.WololoGoal.class,
                // Fox
                FoxEntity.AttackGoal.class, FoxEntity.DefendFriendGoal.class,
                FoxEntity.JumpChasingGoal.class,
                // Ghast
                GhastEntity.ShootFireballGoal.class,
                // Guardian
                GuardianEntity.FireBeamGoal.class,
                // Illusioner
                IllusionerEntity.GiveInvisibilityGoal.class,
                // Panda
                PandaEntity.AttackGoal.class, PandaEntity.PandaRevengeGoal.class,
                // PolarBear
                PolarBearEntity.AttackGoal.class, PolarBearEntity.PolarBearRevengeGoal.class,
                PolarBearEntity.ProtectBabiesGoal.class,
                // Pufferfish
                PufferfishEntity.InflateGoal.class,
                // Raider
                RaiderEntity.AttackHomeGoal.class, RaiderEntity.CelebrateGoal.class,
                //#if MC<12100
                RaiderEntity.PickupBannerAsLeaderGoal.class,
                //#endif
                // Shulker
                ShulkerEntity.ShootBulletGoal.class, ShulkerEntity.TargetOtherTeamGoal.class,
                ShulkerEntity.TargetPlayerGoal.class,
                // Silverfish
                SilverfishEntity.CallForHelpGoal.class,
                // Spider
                SpiderEntity.AttackGoal.class,
                // Squid
                SquidEntity.EscapeAttackerGoal.class,
                // Wither
                WitherEntity.DescendAtHalfHealthGoal.class,
                // Wolf
                WolfEntity.AvoidLlamaGoal.class,
                // Zombie
                ZombieEntity.DestroyEggGoal.class,
                // 仅 1.20.1
                //#if MC<12100
                RabbitEntity.RabbitAttackGoal.class,
                RavagerEntity.AttackGoal.class,
                VindicatorEntity.AttackGoal.class,
                VindicatorEntity.BreakDoorGoal.class,
                //#endif
                IllagerEntity.LongDoorInteractGoal.class
        );

        register("breed",
                AnimalMateGoal.class, BreedTask.class, VillagerBreedTask.class,
                FoxEntity.MateGoal.class, PandaEntity.PandaMateGoal.class,
                TurtleEntity.MateGoal.class
        );

        register("tempt",
                TemptGoal.class, TemptTask.class, TemptationCooldownTask.class,
                CatEntity.TemptGoal.class, OcelotEntity.OcelotTemptGoal.class
        );

        register("pickup",
                GatherItemsVillagerTask.class, GiveInventoryToLookTargetTask.class,
                EndermanEntity.PickUpBlockGoal.class, EndermanEntity.PlaceBlockGoal.class,
                FoxEntity.PickupItemGoal.class, PandaEntity.PickUpFoodGoal.class
        );

        register("work",
                VillagerWorkTask.class, FarmerWorkTask.class, FarmerVillagerTask.class,
                BoneMealTask.class, GatherItemsVillagerTask.class,
                GiveGiftsToHeroTask.class, VillagerBreedTask.class,
                PlayWithVillagerBabiesTask.class, SleepTask.class, WakeUpTask.class,
                PanicTask.class, CelebrateRaidWinTask.class,
                AdmireItemTask.class, GiveInventoryToLookTargetTask.class,
                TemptTask.class, LayFrogSpawnTask.class, CroakTask.class,
                PlayDeadTask.class, MeetVillagerTask.class, RingBellTask.class,
                TakeJobSiteTask.class, GoToWorkTask.class, LoseJobOnSiteLossTask.class,
                WorkStationCompetitionTask.class, StartRaidTask.class,
                EndRaidTask.class, ForgetBellRingTask.class,
                ForgetCompletedPointOfInterestTask.class,
                VillagerWalkTowardsTask.class, WalkTowardJobSiteTask.class,
                JumpInBedTask.class, FindPointOfInterestTask.class,
                FrogEatEntityTask.class, HoldTradeOffersTask.class
        );

        generateAllOption();
    }

    private static void register(String key, Class<?>... goals) {
        List<Class<?>> list = REMSSettings.GOAL_MAPPING.computeIfAbsent(key, k -> new ArrayList<>());
        list.addAll(Arrays.asList(goals));
    }

    private static void generateAllOption() {
        Set<Class<?>> all = new HashSet<>();
        for (List<Class<?>> list : REMSSettings.GOAL_MAPPING.values()) all.addAll(list);
        REMSSettings.GOAL_MAPPING.put("all", new ArrayList<>(all));
    }
}
