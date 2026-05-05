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

import carpet.logging.HUDLogger;
import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import rems.carpet.logging.logger.LoadedChunksLogger;
import rems.carpet.logging.logger.PortalPoisLogger;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class REMSLoggerRegistry {

    public static boolean __displaypoi;
    public static boolean __loadedChunks;

    private static final List<Runnable> onRegisteredCallbacks = new ArrayList<>();

    public static void registerLoggers() {
        register(PortalPoisLogger.getInstance());
        register(LoadedChunksLogger.getInstance());
        onRegisteredCallbacks.forEach(Runnable::run);
    }

    private static void register(REMSLogger logger) {
        Logger carpetLogger = logger.createCarpetLogger();
        LoggerRegistry.registerLogger(carpetLogger.getLogName(), carpetLogger);
    }

    public static Logger standardLogger(String logName, String def, String[] options) {
        return new Logger(getLoggerField(logName), logName, def, options);
    }

    public static HUDLogger standardHUDLogger(String logName, String def, String [] options, boolean strictOptions) {
        return new HUDLogger(getLoggerField(logName), logName, def, options, strictOptions);
    }

    public static Field getLoggerField(String logName) {
        try {
            return REMSLoggerRegistry.class.getField("__" + logName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to get logger field \"__" + logName + "\"", e);
        }
    }

    public static void addLoggerRegisteredCallback(Runnable callback) {
        onRegisteredCallbacks.add(callback);
    }
}