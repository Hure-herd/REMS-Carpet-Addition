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

package rems.carpet.command.VoidTrading;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import rems.carpet.REMSSettings;
import rems.carpet.utils.ComponentTranslate;
import rems.carpet.utils.VoidTrading.NoEndGatewayTicket;

import java.util.Set;

public class NoEndGatewayTicketCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("noEndGatewayTicket")
                .requires(source -> CommandHelper.canUseCommand(source, REMSSettings.commandnoEndGatewayTicket))
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                    BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(context, "pos");
                                    NoEndGatewayTicket.add(pos);
                                    player.sendOverlayMessage(ComponentTranslate.tr("commandnoEndGatewayTicket.add").formatted(Formatting.GRAY)
                                            .append(Text.literal(" " + pos.toShortString())));
                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                    BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(context, "pos");
                                    if (NoEndGatewayTicket.isMarked(pos)) {
                                        NoEndGatewayTicket.remove(pos);
                                        player.sendOverlayMessage(ComponentTranslate.tr("commandnoEndGatewayTicket.remove").formatted(Formatting.GRAY)
                                                .append(Text.literal(" " + pos.toShortString())));
                                    } else {
                                        player.sendOverlayMessage(ComponentTranslate.tr("commandnoEndGatewayTicket.remove.error").formatted(Formatting.GRAY));
                                    }
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("all")
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                    NoEndGatewayTicket.clear();
                                    player.sendOverlayMessage(ComponentTranslate.tr("commandnoEndGatewayTicket.remove.all").formatted(Formatting.GRAY));

                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal("list")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            Set<BlockPos> positions = NoEndGatewayTicket.getAll();
                            if (positions.isEmpty()) {
                                player.sendOverlayMessage(ComponentTranslate.tr("commandnoEndGatewayTicket.list.error").formatted(Formatting.GRAY));
                            } else {
                                player.sendOverlayMessage(
                                        ComponentTranslate.tr("commandnoEndGatewayTicket.list")
                                                .formatted(Formatting.GRAY)
                                                .append(Text.literal(" (" + positions.size() + "):"))
                                );
                                for (BlockPos p : positions) {
                                    context.getSource().sendFeedback(
                                            () -> Text.literal(" - " + p.toShortString()),
                                            false
                                    );
                                }
                            }
                            return positions.size();
                        })
                )
        );
    }
}
