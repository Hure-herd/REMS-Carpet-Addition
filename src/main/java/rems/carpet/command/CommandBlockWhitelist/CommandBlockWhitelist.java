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

package rems.carpet.command.CommandBlockWhitelist;

import net.minecraft.util.Formatting;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import rems.carpet.utils.ComponentTranslate;
import rems.carpet.utils.CommandBlockWhitelist.CbWhitelist;
//#if MC>12110
//$$ import net.minecraft.command.permission.Permission;
//$$ import net.minecraft.command.permission.PermissionLevel;
//#endif

public class CommandBlockWhitelist {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("cbwhitelist")
                //#if MC<12111
                .requires(source -> source.hasPermissionLevel(4)&& source.getEntity() == null)
                //#else
                //$$ .requires(source -> source.getPermissions().hasPermission(
                //$$         new Permission.Level(PermissionLevel.fromLevel(4)
                //$$         ))&& source.getEntity() == null)
                //#endif
                .then(CommandManager.literal("open")
                        .executes(context -> {
                                    CbWhitelist.setOpen(true);
                                    return Command.SINGLE_SUCCESS;
                        })
                )
                .then(CommandManager.literal("close")
                        .executes(context -> {
                            CbWhitelist.setOpen(false);
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("playerName", StringArgumentType.word())
                                .executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "playerName");
                                    if (CbWhitelist.WHITELIST.add(playerName)) {
                                        context.getSource().sendFeedback(() -> ComponentTranslate.tr("CommandBlockWhitelist.1").formatted(Formatting.GRAY)
                                                .append(Text.literal(" " + playerName))
                                                        .append(ComponentTranslate.tr("CommandBlockWhitelist.add.1").formatted(Formatting.GRAY)), false);
                                    } else {
                                        context.getSource().sendFeedback(() -> ComponentTranslate.tr("CommandBlockWhitelist.add.2").formatted(Formatting.GRAY), false);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )

                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("playerName", StringArgumentType.word())
                                .executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "playerName");
                                    if (CbWhitelist.WHITELIST.remove(playerName)) {
                                        context.getSource().sendFeedback(() -> ComponentTranslate.tr("CommandBlockWhitelist.1").formatted(Formatting.GRAY)
                                                .append(Text.literal(" " + playerName))
                                                .append(ComponentTranslate.tr("CommandBlockWhitelist.remove.1").formatted(Formatting.GRAY)), false);
                                    } else {
                                        context.getSource().sendFeedback(() -> ComponentTranslate.tr("CommandBlockWhitelist.remove.2").formatted(Formatting.GRAY), false);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )

                .then(CommandManager.literal("list")
                        .executes(context -> {
                            int size = CbWhitelist.WHITELIST.size();
                            String listString = String.join(", ", CbWhitelist.WHITELIST);
                            context.getSource().sendFeedback(() -> ComponentTranslate.tr("CommandBlockWhitelist.2").formatted(Formatting.GRAY)
                                    .append(Text.literal("("+ size))
                                    .append(ComponentTranslate.tr("CommandBlockWhitelist.3"))
                                    .append(Text.literal("):"+listString)), false);
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
    }
}
