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

package rems.carpet.logging;

import carpet.logging.LoggerRegistry;
import net.minecraft.server.MinecraftServer;
import rems.carpet.logging.logger.LoadedChunksLogger;

public class REMSHUDController
{
    public static void updateHUD(MinecraftServer server)
    {
        doHudLogging(REMSLoggerRegistry.__loadedChunks, LoadedChunksLogger.NAME, LoadedChunksLogger.getInstance());
    }

    public static void doHudLogging(boolean condition, String loggerName, REMSHUDLogger logger)
    {
        if (condition)
        {
            LoggerRegistry.getLogger(loggerName).log(logger::onHudUpdate);
        }
    }
}
