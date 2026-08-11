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

package rems.carpet.mixins.SignCommand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;
import rems.carpet.utils.ComponentTranslate;
//#if MC>12110
//$$ import net.minecraft.command.permission.PermissionPredicate;
//#endif
import java.util.*;

@Mixin(AbstractSignBlock.class)
public class AbstractSignBlockMixin {

    @Inject(
            method = "onUse",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> ci) {
        if (REMSSettings.SignCommand) {

            if (player.isSneaking() || world.isClient()) {
                return;
            }

            if (world.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity) {
                boolean filtered = player.shouldFilterText();

                boolean isFront = signBlockEntity.isPlayerFacingFront(player);
                SignText signText = isFront ?
                        signBlockEntity.getFrontText() :
                        signBlockEntity.getBackText();

                String fullCommand = processSignText(signText, filtered);

                if (!fullCommand.startsWith("/")) {
                    return;
                }

                String actualCommand = fullCommand.substring(1);
                if (!isCommandAllowed(actualCommand)) {
                    //#if MC<260000
                    player.sendMessage(ComponentTranslate.error("sign_command.not_allowed"), false);
                    //#else
                    //$$ player.sendMessage(ComponentTranslate.error("sign_command.not_allowed"));
                    //#endif
                    //#if MC<12104
                    ci.setReturnValue(ActionResult.success(true));
                    //#else
                    //$$ ci.setReturnValue(ActionResult.SUCCESS);
                    //#endif
                    return;
                }
                //#if MC<260000
                if (player.getMainHandStack().isOf(Items.AIR) && !player.isSneaking()) {
                //#else
                //$$ if (player.getMainHandStack().is(Items.AIR) && !player.isSneaking()) {
                //#endif
                    //#if MC<12104
                    ci.setReturnValue(ActionResult.success(true));
                    //#else
                    //$$ ci.setReturnValue(ActionResult.SUCCESS);
                    //#endif
                    executeValidatedCommand(player, actualCommand);
                }
            }
        }
    }

    @Unique
    private String processSignText(SignText signText, boolean filtered) {
        StringBuilder commandBuilder = new StringBuilder();

        List<Text> textLines = List.of(signText.getMessages(filtered));

        for (int i = 0; i < textLines.size(); i++) {
            String line = textLines.get(i).getString()
                    .replaceAll("§.", "")
                    .replaceAll("[\\x00-\\x1F]", "")
                    .trim();

            if (line.isEmpty()) continue;

            if (line.endsWith("\\")) {
                commandBuilder.append(line, 0, line.length() - 1);
            } else {
                commandBuilder.append(line);
                if (i != textLines.size() - 1) commandBuilder.append(" ");
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
    private void executeValidatedCommand(PlayerEntity player, String command) {
        //#if MC<12110
        ServerWorld world = Objects.requireNonNull(player.getServer()).getOverworld();;
        //#else
        //$$ ServerWorld world = Objects.requireNonNull(player.getEntityWorld().getServer()).getOverworld();;
        //#endif
        ServerCommandSource commandSource = new ServerCommandSource(
                CommandOutput.DUMMY,
                player.getPos(),
                player.getRotationClient(),
                world,
                //#if MC<12111
                4,
                //#else
                //$$ PermissionPredicate.ALL,
                //#endif
                player.getName().getString(),
                player.getDisplayName(),
                world.getServer(),
                player
        );

        //#if MC<12110
        player.getServer().execute(() -> {
        //#else
        //$$ player.getEntityWorld().getServer().execute(() -> {
        //#endif
            try {
                CommandDispatcher<ServerCommandSource> dispatcher = commandSource.getServer().getCommandManager().getDispatcher();
                ParseResults<ServerCommandSource> results = dispatcher.parse(command, commandSource);

                if (results.getExceptions().isEmpty()) {
                    dispatcher.execute(results);
                } else {
                    //#if MC<260000
                    player.sendMessage(ComponentTranslate.error("sign.command.syntax_error"), false);
                    //#else
                    //$$ player.sendMessage(ComponentTranslate.error("sign.command.syntax_error"));
                    //#endif
                }
            } catch (CommandSyntaxException e) {
                //#if MC<260000
                player.sendMessage(ComponentTranslate.error("sign_command.failed", e.getMessage()), false);
                //#else
                //$$ player.sendMessage(ComponentTranslate.error("sign_command.failed", e.getMessage()));
                //#endif
            }
        });
    }
}
