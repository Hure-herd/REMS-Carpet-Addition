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

import java.util.*;

@Mixin(AbstractSignBlock.class)
public class AbstractSignBlockMixin {
    @Unique
    private static final Set<String> ALLOWED_COMMANDS = new HashSet<>(Arrays.asList(
            "say",
            "player",
            "tick"
    ));

    @Inject(
            method = "onUse",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> ci) {
        if (REMSSettings.SignCommand) {
            if (world.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity) {
                // 获取过滤标识（关键新增参数）
                boolean filtered = player.shouldFilterText();

                // 获取正确的 SignText 对象
                boolean isFront = signBlockEntity.isPlayerFacingFront(player);
                SignText signText = isFront ?
                        signBlockEntity.getFrontText() :  // 使用明确方法获取
                        signBlockEntity.getBackText();

                // 传递两个参数（关键修复）
                String fullCommand = processSignText(signText, filtered);

                // 第二步：验证命令格式
                if (!fullCommand.startsWith("/")) {
                    return;
                }

                // 第三步：白名单验证
                String actualCommand = fullCommand.substring(1);
                if (!isCommandAllowed(actualCommand)) {
                    player.sendMessage(Text.literal("§c该指令未被允许通过告示牌执行"), false);
                    ci.setReturnValue(ActionResult.SUCCESS);
                    return;
                }

                // 第四步：执行条件检查
                if (player.getMainHandStack().isOf(Items.AIR) && !player.isSneaking()) {
                    ci.setReturnValue(ActionResult.SUCCESS);
                    executeValidatedCommand(player, actualCommand);
                }
            }
        }
    }

    // 文本处理核心方法
    @Unique
    private String processSignText(SignText signText, boolean filtered) {
        StringBuilder commandBuilder = new StringBuilder();

        // 获取过滤后的文本列表
        List<Text> textLines = List.of(signText.getMessages(filtered));

        for (int i = 0; i < textLines.size(); i++) {
            String line = textLines.get(i).getString()
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
                if (i != textLines.size() - 1) commandBuilder.append(" ");
            }
        }

        return commandBuilder.toString()
                .replaceAll("\\s+", " ") // 合并连续空格
                .trim();
    }

    // 白名单验证逻辑
    @Unique
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
    @Unique
    private void executeValidatedCommand(PlayerEntity player, String command) {
        ServerWorld world = Objects.requireNonNull(player.getEntityWorld().getServer()).getOverworld();;
        ServerCommandSource commandSource = new ServerCommandSource(
                CommandOutput.DUMMY,
                player.getEntityPos(),
                player.getRotationClient(),
                world,
                4,
                player.getName().getString(),
                player.getDisplayName(),
                world.getServer(),
                player
        );

        player.getEntityWorld().getServer().execute(() -> {
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
