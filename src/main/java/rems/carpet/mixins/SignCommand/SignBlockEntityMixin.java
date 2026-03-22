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

package rems.carpet.mixins.SignCommand;

import carpet.utils.Messenger;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;
import rems.carpet.utils.ComponentTranslate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin {

    @Shadow
    protected abstract Text[] getTexts(boolean filtered);

    @Inject(
            method = "onActivate",
            at = @At("HEAD"),
            cancellable = true
    )
    private void runCommandOnActivated(ServerPlayerEntity player, CallbackInfoReturnable<Boolean> ci) {
        if (REMSSettings.SignCommand) {

            if (player.isSneaking()) {
                return;
            }

            Text[] texts = this.getTexts(player.shouldFilterText());

            String fullCommand = processSignText(texts);

            if (fullCommand.isEmpty() || !fullCommand.startsWith("/")) {
                return;
            }

            String actualCommand = fullCommand.substring(1);
            if (!isCommandAllowed(actualCommand)) {
                player.sendMessage(ComponentTranslate.error("sign_command.not_allowed"), false);
                ci.setReturnValue(true);
                return;
            }

            if (player.getMainHandStack().isOf(Items.AIR) && !player.isSneaking()) {
                ci.setReturnValue(true);
                executeValidatedCommand(player, actualCommand);
            }
        }
    }

    @Unique
    private String processSignText(Text[] texts) {
        StringBuilder commandBuilder = new StringBuilder();

        for (int i = 0; i < texts.length; i++) {
            String line = texts[i].getString()
                    .replaceAll("§.", "")
                    .replaceAll("[\\x00-\\x1F]", "")
                    .trim();

            if (line.isEmpty()) continue;

            if (line.endsWith("\\")) {
                commandBuilder.append(line, 0, line.length() - 1);
            } else {
                commandBuilder.append(line);
                if (i != texts.length - 1) commandBuilder.append(" ");
            }
        }

        return commandBuilder.toString()
                .replaceAll("\\s+", " ")
                .trim();
    }

    @Unique
    private boolean isCommandAllowed(String rawCommand) {
        String[] parts = rawCommand.split(" ", 2);
        String baseCommand = parts[0].toLowerCase();

        int colonIndex = baseCommand.indexOf(':');
        if (colonIndex != -1) {
            baseCommand = baseCommand.substring(colonIndex + 1);
        }

        return REMSSettings.ALLOWED_COMMANDS.contains(baseCommand);
    }

    @Unique
    private void executeValidatedCommand(ServerPlayerEntity player, String command) {
        ServerWorld world = player.getWorld();
        ServerCommandSource commandSource = new ServerCommandSource(
                CommandOutput.DUMMY,
                player.getPos(),
                player.getRotationClient(),
                world,
                0,
                player.getName().getString(),
                player.getDisplayName(),
                world.getServer(),
                player
        );

        player.getServer().execute(() -> {
            try {
                CommandDispatcher<ServerCommandSource> dispatcher = commandSource.getServer().getCommandManager().getDispatcher();
                ParseResults<ServerCommandSource> results = dispatcher.parse(command, commandSource);

                if (results.getExceptions().isEmpty()) {
                    dispatcher.execute(results);
                } else {
                    player.sendMessage(ComponentTranslate.error("sign.command.syntax_error"), false);
                }
            } catch (CommandSyntaxException e) {
                player.sendMessage(ComponentTranslate.error("sign_command.failed", e.getMessage()), false);
            }
        });
    }
}
