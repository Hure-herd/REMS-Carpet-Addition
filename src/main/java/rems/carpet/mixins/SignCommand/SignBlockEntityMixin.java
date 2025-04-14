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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin {
    @Shadow
    protected abstract Text[] getTexts(boolean filtered);

    // 允许的命令白名单
    private static final Set<String> ALLOWED_COMMANDS = new HashSet<>(Arrays.asList(
            "say",
            "player",
            "tick"
    ));

    @Inject(
            method = "onActivate",
            at = @At("HEAD"),
            cancellable = true
    )
    private void runCommandOnActivated(ServerPlayerEntity player, CallbackInfoReturnable<Boolean> ci) {
        if (REMSSettings.SignCommand) {
            Text[] texts = this.getTexts(player.shouldFilterText());

            // 第一步：处理多行文本
            String fullCommand = processSignText(texts);

            // 第二步：验证命令格式
            if (fullCommand.isEmpty() || !fullCommand.startsWith("/")) {
                return;
            }

            // 第三步：白名单验证
            String actualCommand = fullCommand.substring(1);
            if (!isCommandAllowed(actualCommand)) {
                player.sendMessage(Text.literal("§c该指令未被允许通过告示牌执行"), false);
                ci.setReturnValue(true);
                return;
            }

            // 第四步：执行条件检查
            if (player.getMainHandStack().isOf(Items.AIR) && !player.isSneaking()) {
                ci.setReturnValue(true);
                executeValidatedCommand(player, actualCommand);
            }
        }
    }

    // 文本处理核心方法
    private String processSignText(Text[] texts) {
        StringBuilder commandBuilder = new StringBuilder();

        for (int i = 0; i < texts.length; i++) {
            String line = texts[i].getString()
                    .replaceAll("§.", "")       // 移除所有颜色代码
                    .replaceAll("[\\x00-\\x1F]", "") // 过滤控制字符
                    .trim();

            // 处理空行
            if (line.isEmpty()) continue;

            // 处理续行符（行末的\）
            if (line.endsWith("\\")) {
                commandBuilder.append(line, 0, line.length() - 1);
            } else {
                commandBuilder.append(line);
                // 在行尾添加空格（最后一行除外）
                if (i != texts.length - 1) commandBuilder.append(" ");
            }
        }

        return commandBuilder.toString()
                .replaceAll("\\s+", " ") // 合并连续空格
                .trim();
    }

    // 白名单验证逻辑
    private boolean isCommandAllowed(String rawCommand) {
        // 提取基础命令
        String[] parts = rawCommand.split(" ", 2);
        String baseCommand = parts[0].toLowerCase();

        // 处理命名空间（如 minecraft:give → give）
        int colonIndex = baseCommand.indexOf(':');
        if (colonIndex != -1) {
            baseCommand = baseCommand.substring(colonIndex + 1);
        }

        return ALLOWED_COMMANDS.contains(baseCommand);
    }

    // 命令执行方法
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
                    player.sendMessage(Text.literal("§c指令语法错误"), false);
                }
            } catch (CommandSyntaxException e) {
                player.sendMessage(Text.literal("§c执行失败: " + e.getMessage()), false);
            }
        });
    }
}
