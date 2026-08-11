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

package rems.carpet.command.PetTransfer;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import rems.carpet.REMSSettings;
import rems.carpet.utils.ComponentTranslate;

public class PetTransferCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("petTransfer")
                .requires(source -> CommandHelper.canUseCommand(source, REMSSettings.commandpetOwnerTransfer))
                .then(CommandManager.argument("petUuid", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            var player = ctx.getSource().getPlayer();
                            if (player == null) return builder.buildFuture();
                            String r = builder.getRemaining().toLowerCase();
                            for (TameableEntity pet : ctx.getSource().getWorld().getEntitiesByClass(TameableEntity.class,
                                    player.getBoundingBox().expand(50.0),
                                    p -> p.isTamed() && p.isOwner(player))) {
                                String uid = pet.getUuid().toString();
                                if (uid.startsWith(r)) builder.suggest(uid);
                            }
                            return builder.buildFuture();
                        })
                        .then(CommandManager.argument("target", EntityArgumentType.player())
                                .executes(ctx -> {
                                    String uuidStr = StringArgumentType.getString(ctx, "petUuid");
                                    java.util.UUID uuid;
                                    try {
                                        uuid = java.util.UUID.fromString(uuidStr);
                                    } catch (IllegalArgumentException e) {
                                        ctx.getSource().sendError(ComponentTranslate.tr("petTransfer.invalid_uuid"));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    var target = EntityArgumentType.getPlayer(ctx, "target");
                                    for (ServerWorld world : ctx.getSource().getServer().getWorlds()) {
                                        for (TameableEntity pet : world.getEntitiesByClass(TameableEntity.class,
                                                ctx.getSource().getPlayer().getBoundingBox().expand(100.0),
                                                pet -> pet.isTamed() && pet.isOwner(ctx.getSource().getPlayer()))) {
                                            if (pet.getUuid().equals(uuid)) {
                                                pet.setOwner(target);
                                                String name = target.getName().getString();
                                                ctx.getSource().sendFeedback(() -> ComponentTranslate.tr("petTransfer.success", name), false);
                                                return Command.SINGLE_SUCCESS;
                                            }
                                        }
                                    }
                                    ctx.getSource().sendError(ComponentTranslate.error("petTransfer.not_found"));
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }
}
