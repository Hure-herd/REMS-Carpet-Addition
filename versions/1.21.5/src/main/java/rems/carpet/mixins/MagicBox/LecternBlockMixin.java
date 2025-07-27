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

package rems.carpet.mixins.MagicBox;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;
import rems.carpet.utils.ChunkLoaderState;
import rems.carpet.utils.Soundsuppressionutils;
import rems.carpet.utils.magicboxutils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Mixin(LecternBlock.class)
public abstract class LecternBlockMixin extends BlockWithEntity  {
    protected LecternBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onStateReplaced", at = @At("TAIL"))
    private void cancelIfMagicBox(
            BlockState state,
            ServerWorld world,
            BlockPos pos,
            boolean moved,
            CallbackInfo ci
    ) {
        if (REMSSettings.magicBox) {
            magicboxutils.ismark(pos);
            //Text message1 = Text.literal("方块在 " + pos.toShortString() + " 标记成功")
            //        .styled(style -> style.withColor(Formatting.GOLD));
            //world.getServer().getPlayerManager().broadcast(message1, false);
        }
    }
}
