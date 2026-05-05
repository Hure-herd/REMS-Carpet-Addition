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

package rems.carpet.logging.logger;

import carpet.logging.Logger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import rems.carpet.logging.REMSHUDLogger;
import rems.carpet.logging.REMSLoggerRegistry;
import rems.carpet.mixins.LoadedChunksLogger.ChunkLoadingAccessor;
import rems.carpet.utils.ComponentTranslate;

public class LoadedChunksLogger implements REMSHUDLogger {

    private static final LoadedChunksLogger INSTANCE = new LoadedChunksLogger();

    public static final String NAME = "loadedChunks";

    private LoadedChunksLogger() {}

    public static LoadedChunksLogger getInstance() { return INSTANCE; }

    private int TotalAll = 0, TickAll = 0;
    private int TotalOvw = 0, TickOvw = 0;
    private int TotalNet = 0, TickNet = 0;
    private int TotalEnd = 0, TickEnd = 0;

    private String format(int ticking, int total) {
        String t = ticking == 0 ? "-" : String.valueOf(ticking);
        String p = total == 0 ? "-" : String.valueOf(total);
        return t + "/" + p;
    }

    @Override
    public Logger createCarpetLogger() {
        return REMSLoggerRegistry.standardHUDLogger("loadedChunks", "brief", new String[]{"brief", "overworld", "nether", "end", "full"}, true);
    }

    public void tickLog(MinecraftServer server) {
        carpet.logging.Logger logger = carpet.logging.LoggerRegistry.getLogger("loadedChunks");
        if (logger == null || !logger.hasOnlineSubscribers()) return;

        int totalAll = 0, tickAll = 0;
        int totalOvw = 0, tickOvw = 0;
        int totalNet = 0, tickNet = 0;
        int totalEnd = 0, tickEnd = 0;

        for (ServerWorld world : server.getWorlds()) {
            ChunkLoadingAccessor chunkStorage = (ChunkLoadingAccessor) world.getChunkManager().threadedAnvilChunkStorage;

            int ticking = 0;
            int loaded = 0;

            for (ChunkHolder holder : chunkStorage.getChunkHolders().values()) {
                int level = holder.getLevel();
                if (level < 33) {
                    loaded++;
                    if (level < 32) {
                        ticking++;
                    }
                }
            }
            totalAll += loaded;
            tickAll += ticking;
            if (world.getRegistryKey() == World.OVERWORLD) {
                totalOvw = loaded; tickOvw = ticking;
            } else if (world.getRegistryKey() == World.NETHER) {
                totalNet = loaded; tickNet = ticking;
            } else if (world.getRegistryKey() == World.END) {
                totalEnd = loaded; tickEnd = ticking;
            }
        }
        this.TotalAll = totalAll;
        this.TickAll = tickAll;
        this.TotalOvw = totalOvw;
        this.TickOvw = tickOvw;
        this.TotalNet = totalNet;
        this.TickNet = tickNet;
        this.TotalEnd = totalEnd;
        this.TickEnd = tickEnd;
    }

    @Override
    public Text[] onHudUpdate(String option, PlayerEntity playerEntity) {
        System.out.println("[Debug] Incoming option from Carpet is: [" + option + "]");
        MutableText messageText = Text.empty();
        String message = "";
        switch (option != null ? option.trim() : "full") {
            case "brief":
                messageText.append(Text.literal(" " + format(TickAll, TotalAll)).formatted(Formatting.WHITE));
                break;
            case "overworld":
                messageText.append(Text.literal(" " + format(TickOvw, TotalOvw)).formatted(Formatting.DARK_GREEN));
                break;
            case "nether":
                messageText.append(Text.literal(" " + format(TickNet, TotalNet)).formatted(Formatting.RED));
                break;
            case "end":
                messageText.append(Text.literal(" " + format(TickEnd, TotalEnd)).formatted(Formatting.YELLOW));
                break;
            case "full":
                messageText.append(Text.literal(" " + format(TickAll, TotalAll)).formatted(Formatting.WHITE))
                        .append(Text.literal(" " + format(TickOvw, TotalOvw)).formatted(Formatting.DARK_GREEN))
                        .append(Text.literal(" " + format(TickNet, TotalNet)).formatted(Formatting.RED))
                        .append(Text.literal(" " + format(TickEnd, TotalEnd)).formatted(Formatting.YELLOW));
                break;
        }
        MutableText header = ComponentTranslate.tr("loadedChunks.title").formatted(Formatting.DARK_GRAY);
        MutableText finalText = Text.empty().append(header).append(messageText);
        System.out.println("[Debug] Text ready to be sent to HUD: " + finalText.getString());
        return new Text[]{ finalText };
    }
}

