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

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.utils.Soundsuppressionutils;
import rems.carpet.utils.magicboxutils;


@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/Entity;" +
                            "Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;" +
                            "Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void notsoshadowextras$swapBlockEntity(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {

        BlockPos pos = context.getBlockPos();

        World world = context.getWorld();
        if (world.isClient()) return;

        if (!(magicboxutils.isisSuppressed(pos)))return;

        magicboxutils.isclaer();
        BlockState state = world.getBlockState(pos);
        if (!state.hasBlockEntity()) return;
        BlockEntity blockEntity = new LecternBlockEntity(pos, state);
        world.removeBlockEntity(pos);
        world.addBlockEntity(blockEntity);
    }
}
