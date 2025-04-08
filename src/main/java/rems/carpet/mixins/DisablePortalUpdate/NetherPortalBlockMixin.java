/*
 * This file is part of the Carpet AMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 A Minecraft Server and contributors
 *
 * Carpet AMS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet AMS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet AMS Addition. If not, see <https://www.gnu.org/licenses/>.
 */

package rems.carpet.mixins.DisablePortalUpdate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;

@Mixin(NetherPortalBlock.class)
@SuppressWarnings("deprecation")
public abstract class NetherPortalBlockMixin extends Block {
    public NetherPortalBlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    public abstract BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                         WorldAccess world, BlockPos pos, BlockPos neighborPos);

    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"), cancellable = true)
    private void update(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos,
                        BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (REMSSettings.disablePortalUpdate) {
            cir.setReturnValue(super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos));
        }
    }
}
