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

package rems.carpet.command.ClearPearTrail;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import rems.carpet.REMSSettings;
import rems.carpet.utils.NoSensationPearlLoad.ClearPearTrail;

public class ClearPearTrailCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("clearPearlTrail")
                .requires(source -> CommandHelper.canUseCommand(source, REMSSettings.commandClearPearTrail))
                .executes(context -> {
                    ClearPearTrail.clearAll();
                    return Command.SINGLE_SUCCESS;
                })
        );
    }
}