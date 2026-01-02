/*
 * This file is part of the Carpet AMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 A Minecraft Server and contributors
 *
 * Carpet AMS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet AMS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet AMS Addition. If not, see <https://www.gnu.org/licenses/>.
 */

package rems.carpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import rems.carpet.utils.ComponentTranslate;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import rems.carpet.command.soundsuppressionintroduce.UpdateDepressionCommands;
import rems.carpet.utils.DurableItemShadow.ShadowCacheManager;

import java.util.Map;

public class REMSServer implements CarpetExtension, ModInitializer
{
    public static String MOD_ID = "remscarpetadditions";

    public static final String MOD_NAME = "Carpet REMS Additions";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    private static final REMSServer INSTANCE = new REMSServer();

    private static MinecraftServer minecraftServer;

    public static Boolean shouldKeepPearl;

    public static MinecraftServer getServer() {
        if (INSTANCE.minecraftServer == null) {
            throw new RuntimeException("MinecraftServer hasn't finished initializing yet!");
        } else {
            return INSTANCE.minecraftServer;
        }
    }

    public static String getVersion() {
        return FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString();
    }


    public static void loadExtension() {
        CarpetServer.manageExtension(INSTANCE);
    }

    @Override
    public void onInitialize() {
        REMSServer.loadExtension();
        shouldKeepPearl = Boolean.getBoolean("pearl.keep");
        //#if MC>=12001
        //$$ CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) ->
        //$$         UpdateDepressionCommands.register(dispatcher, context));
        //#endif
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            ShadowCacheManager.clearCache();
        });
    }

    @Override
    public void onGameStarted() {
        LOGGER.info(MOD_ID + " " + "v" + getVersion() + "载入成功");
        LOGGER.info("开源链接：https://github.com/Hure-herd/REMS-Carpet-extra");
        CarpetServer.settingsManager.parseSettingsClass(REMSSettings.class);

    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return ComponentTranslate.getTranslationFromResourcePath(lang);
    }

    @Override
    public void onServerLoaded(MinecraftServer server) {
        this.minecraftServer = server;
    }
}