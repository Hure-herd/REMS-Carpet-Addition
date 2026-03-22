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

package rems.carpet;


import static rems.carpet.utils.REMSRuleCategory.*;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import carpet.api.settings.Validators;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import rems.carpet.utils.DisableAi.AiGoalRegistrar;

import java.util.*;


public class REMSSettings
{

    @Rule(
            categories = {REMS,EXPERIMENTAL,OPTIMIZATION},
            options = {"0","200"},
            strict = false,
            validators = Validators.NonNegativeNumber.class
    )
    public static int projectileRaycastLength = 0;

    @Rule(
            categories = {REMS, FEATURE}
    )
    public static boolean chestMinecartChunkLoader = false;

    @Rule(
            categories = {REMS, FEATURE}
    )
    public static boolean wanderingTraderNoDisappear = false;

    @Rule(
           categories = {REMS, FEATURE}
    )
    public static boolean PortalPearlWarp = false;

    @Rule(
            categories = {REMS, SURVIVAL}
    )
    public static boolean pistonBlockChunkloader =  false;

    @Rule(
            categories = {REMS, SURVIVAL}
    )
    public static boolean pearlPosVelocity = false;

    @Rule(
            categories = {REMS, SURVIVAL}
    )
    public static boolean stridergodie = false;

    @Rule(
            categories = {REMS, FEATURE}
    )
    public static boolean keepWorldTickUpdate = false;

    @Rule(
            categories = {REMS, FEATURE, SURVIVAL}
    )
    public static boolean scheduledRandomTickPlants = false;

    @Rule(
            categories = {REMS, FEATURE, SURVIVAL,TNT}
    )
    public static boolean mergeTNTPro = false;

    @Rule(
            categories = {REMS, FEATURE, SURVIVAL,TNT}
    )
    public static boolean mergeTNTMax = false;

    @Rule(
            categories = {REMS, FEATURE}
    )
    public static boolean disableBatCanSpawn = false;

    @Rule(
            categories = {REMS, CREATIVE, SURVIVAL}
    )
    public static boolean cactusWrenchSound = false;

    @Rule(
            categories = {REMS, CREATIVE}
    )
    public static boolean disablePortalUpdate = false;
    //#if MC<12100
    @Rule(
            categories = {REMS, SURVIVAL}
    )
    public static boolean endGatewayChunkLoader = false;
    //#endif

    @Rule(
            categories = {REMS, EXPERIMENTAL}
    )
    public static boolean itemShadowing = false;

    @Rule(
            categories = {REMS, FEATURE, SURVIVAL}
    )
    public static boolean sharedVillagerDiscounts = false;

    @Rule(
            categories = {REMS, EXPERIMENTAL}
    )
    public static boolean  endstonefram = false;

    @Rule(
            categories = {REMS, SURVIVAL}
    )
    public static boolean reloadrefreshirongolem = false;

    // @Rule(
    //         categories = {REMS, EXPERIMENTAL}
    // )
    // public static boolean teleportToPoiWithoutPortals = false;

    @Rule(
            categories = {REMS, SURVIVAL}
    )
    public static boolean SignCommand = false;

    @Rule(
            categories = {REMS, FEATURE}
    )
    public static boolean reintroduceLlamaItemDuplicating = false;

    @Rule(
            categories = {REMS, FEATURE}
    )
    public static boolean opInSurvivalCommandBlocks = false;

    //#if MC>=12001
    //$$ @Rule(
    //$$         categories = {REMS,FEATURE},
    //$$         options = {"8","16","32"},
    //$$         strict = false,
    //$$         validators = soundSuppressionMaxRadiusValue.class
    //$$ )
    //$$  public static int soundSuppressionRadius = 16;

    //$$ private static class soundSuppressionMaxRadiusValue extends Validator<Integer> {
    //$$    @Override public Integer validate(ServerCommandSource source, CarpetRule<Integer> currentRule, Integer newValue, String string) {
    //$$        return newValue > 0 && newValue <= 64 ? newValue : null;
    //$$    }
    //$$    @Override
    //$$    public String description() { return Language.getInstance().get("carpet.rule.soundSuppressionRadius.validate");}
    //$$ }
    //#endif

    //#if MC<12102
    @Rule(
            categories = {REMS,FEATURE},
            options = {"0","40"},
            strict = false,
            validators = Validators.NonNegativeNumber.class
    )
    public static int Pearltime = 40;

    @Rule(
            categories = {REMS, FEATURE}
    )
    public static boolean enderpearlloadchunk = false;
    //#endif

    @Rule(
            categories = {REMS, FEATURE}
    )
    public static boolean durableItemShadow = false;

    @Rule(
             options = {"ops", "true", "false"},
             categories = {REMS, CREATIVE,COMMAND}
    )
    public static String commandClearPearTrail = "ops";

    //#if MC>=12001
    //$$ @Rule(
    //$$         options = {"ops", "true", "false"},
    //$$         categories = {REMS, CREATIVE,COMMAND}
    //$$ )
    //$$ public static String commandsetnoisesuppressor = "false";
    //#endif

    //#if MC>=12006
    //$$ @Rule(
    //$$        categories = {REMS, FEATURE}
    //$$ )
    //$$ public static boolean ComparatorIgnoresStateUpdatesFromBelow = false;
    //#endif

    //#if MC>=12102
    //$$ @Rule(
    //$$ categories = {REMS, FEATURE, SURVIVAL}
    //$$ )
    //$$ public static boolean stringDupeReintroduced = false;
    //#endif

    //#if MC>=12102
    //$$ @Rule(
    //$$ categories = {REMS, FEATURE}
    //$$ )
    //$$ public static boolean pre21ThrowableEntityMovement = false;
    //#endif

    //#if MC>=12102
    //$$ @Rule(
    //$$ categories = {REMS, FEATURE}
    //$$ )
    //$$ public static boolean pearlnotloadingchunk = false;
    //#endif

    //#if MC<12102
    @Rule(
            categories = {REMS, FEATURE}
    )
    public static boolean introduceHighVersionThrowableEntityMovement = false;
    //#endif

    @Rule(
            categories = {REMS, BUGFIX}
    )
    public static boolean fixedpearlloading = false;

    //#if MC>=12004
    //$$ @Rule(
    //$$ categories = {REMS,EXPERIMENTAL}
    //$$ )
    //$$ public static boolean blockentityreplacement = false;
    //#endif

    @Rule(
            categories = {REMS, FEATURE}
    )
    public static boolean noSensationPearlLoad = false;

    @Rule(
            categories = {REMS, FEATURE},
            validators = CommandListValidator.class,
            options = {"false", "say", "player,tick", "say,player,tick"},
            strict = false
    )
    public static String signAllowedCommands = "false";

    public static final Set<String> ALLOWED_COMMANDS = new HashSet<>();

    public static class CommandListValidator extends Validator<String> {

        @Override
        public String validate(ServerCommandSource source, CarpetRule<String> currentRule, String newValue, String string) {
            String normalizedValue = newValue.toLowerCase().trim();
            ALLOWED_COMMANDS.clear();
            if (normalizedValue.equals("false") || normalizedValue.isEmpty()) {
                return "false";
            }
            String[] parts = normalizedValue.split(",");
            for (String part : parts) {
                String cmd = part.trim();
                if (!cmd.isEmpty()) {
                    ALLOWED_COMMANDS.add(cmd);
                }
            }
            return normalizedValue;
        }
    }

    public static final Set<Class<?>> DISABLED_GOAL_CLASSES = new HashSet<>();
    public static final Map<String, List<Class<?>>> GOAL_MAPPING = new HashMap<>();

    @Rule(
            categories = {REMS, FEATURE},
            validators = EntityListValidator.class,
            options = {"false", "zombie", "creeper", "zombie,skeleton"},
            strict = false
    )
    public static String disableAIEntitities = "false";

    @Rule(
            categories = {REMS, FEATURE},
            validators = GoalListValidator.class,
            options = {"false", "move", "attack", "move,attack,shoot", "all"},
            strict = false
    )
    public static String disableAiGoals = "false";

    public static final Set<EntityType<?>> NO_AI_TYPES = new HashSet<>();

    public static class EntityListValidator extends Validator<String> {

        @Override
        public String validate(ServerCommandSource source, CarpetRule<String> currentRule, String newValue, String string) {
            String normalizedValue = newValue.toLowerCase().trim();
            NO_AI_TYPES.clear();
            if (normalizedValue.equals("false") || normalizedValue.isEmpty()) {
                return "false";
            }
            String[] parts = normalizedValue.split(",");
            for (String part : parts) {
                String entityName = part.trim();
                Identifier id;
                if (entityName.contains(":")) {
                    String[] split = entityName.split(":");
                    id = Identifier.of(split[0], split[1]);
                } else {
                    id = Identifier.of("minecraft", entityName);
                }
                if (Registries.ENTITY_TYPE.containsId(id)) {
                    EntityType<?> type = Registries.ENTITY_TYPE.get(id);
                    NO_AI_TYPES.add(type);
                }
            }
            return normalizedValue;
        }
    }

    public static class GoalListValidator extends Validator<String> {
        @Override
        public String validate(ServerCommandSource source, CarpetRule<String> currentRule, String newValue, String string) {
            String normalized = newValue.toLowerCase().trim();
            if (normalized.equals("false") || normalized.isEmpty()) {
                DISABLED_GOAL_CLASSES.clear();
                return "false";
            }
            Set<Class<?>> tempSet = new HashSet<>();
            String[] parts = normalized.split(",");
            for (String part : parts) {
                String key = part.trim();
                if (GOAL_MAPPING.containsKey(key)) {
                    tempSet.addAll(GOAL_MAPPING.get(key));
                } else {
                    return null;
                }
            }
            DISABLED_GOAL_CLASSES.clear();
            DISABLED_GOAL_CLASSES.addAll(tempSet);
            return normalized;
        }
    }
}

