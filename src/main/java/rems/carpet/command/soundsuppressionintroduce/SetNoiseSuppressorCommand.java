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

package rems.carpet.command.soundsuppressionintroduce;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.block.*;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rems.carpet.REMSSettings;

public class SetNoiseSuppressorCommand {
    private static final SimpleCommandExceptionType INVALID_BLOCK_EXCEPTION = new SimpleCommandExceptionType(
            Text.translatable("commands.setnoisesuppressor.invalidBlock", new Object[0])
    );
    private static final BlockState BENIS = Blocks.BEE_NEST.getDefaultState()
            .withIfExists(BeehiveBlock.HONEY_LEVEL, BeehiveBlock.FULL_HONEY_LEVEL);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess context) {
        dispatcher.register(CommandManager.literal("setnoisesuppressor")
                .requires(source -> CommandHelper.canUseCommand(source, REMSSettings.commandsetnoisesuppressor))
                .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                        .executes(ctx -> setNoiseSuppressor(ctx.getSource(), BlockPosArgumentType.getLoadedBlockPos(ctx, "pos"), BENIS))
                        .then(CommandManager.argument("validBlock", BlockStateArgumentType.blockState(context))
                                .executes(ctx -> setNoiseSuppressor(ctx.getSource(), BlockPosArgumentType.getBlockPos(ctx, "pos"), BlockStateArgumentType.getBlockState(ctx, "validBlock").getBlockState())))));

    }

    private static int setNoiseSuppressor(ServerCommandSource source, BlockPos pos, BlockState blockState) throws CommandSyntaxException {
        if (!blockState.hasBlockEntity() || !blockState.contains(CalibratedSculkSensorBlock.FACING)) {
            throw INVALID_BLOCK_EXCEPTION.create();
        }

        BlockState state = tryGetStateFacingEntity(source.getEntity(), blockState);
        World World = source.getWorld();
        World.setBlockState(pos, state,Block.NOTIFY_LISTENERS);
        World.addBlockEntity(((BlockEntityProvider) Blocks.CALIBRATED_SCULK_SENSOR).createBlockEntity(pos, state));

        return Command.SINGLE_SUCCESS;
    }
    private static BlockState tryGetStateFacingEntity(Entity entity, BlockState blockState) {
        if (entity == null)
            return blockState;

        return blockState.withIfExists(CalibratedSculkSensorBlock.FACING, entity.getHorizontalFacing().getOpposite());
    }
}

