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

package rems.carpet.utils;

import carpet.CarpetSettings;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import rems.carpet.REMSServer;

import java.util.Map;

public class SignCommand {
    public static void executeCommand(ServerPlayerEntity player, String command) {
        if (player == null || command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Player or command cannot be null or empty");
        }
// 若带有 / 则去掉
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        var server = player.getEntityWorld().getServer();
        if (server == null) {
            throw new IllegalStateException("Player is not on a server");
        }

        ServerCommandSource source = player.getCommandSource();
        CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();

        try {
            // 解析和执行命令
            var parseResults = dispatcher.parse(command, source);
            dispatcher.execute(parseResults);
        } catch (Exception e) {
            Text text = Text.literal(getTranslation("carpet.commandWentWrong"))
                    .setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.RED))).append(Text.literal(" /" + command + ": " + e.getMessage())
                            .setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.RED)).withUnderline(true)));
            player.sendMessage(text, false);
            REMSServer.LOGGER.debug("Failed to execute command: " + command + e.getMessage());
        }
    }

    public static String getTranslation(String key) {
        Map<String, String> lang = ComponentTranslate.getTranslationFromResourcePath(CarpetSettings.language);
        String translationText = lang.get(key);
        REMSServer.LOGGER.debug("Translation with key" + key + ": " + translationText);
        return lang.get(key);
    }
}
