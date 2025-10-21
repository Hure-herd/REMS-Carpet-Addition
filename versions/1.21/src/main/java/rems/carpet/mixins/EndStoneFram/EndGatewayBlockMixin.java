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

package rems.carpet.mixins.EndStoneFram;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

import java.util.List;

@Mixin(EndGatewayBlock.class)
public abstract class EndGatewayBlockMixin extends Block {

    public EndGatewayBlockMixin(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Inject(
            method = "onEntityCollision",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onEntityCollisionCheck(
            BlockState state,
            World world,
            BlockPos pos,
            Entity entity,
            CallbackInfo ci
    ) {
        if(REMSSettings.endstonefram) {
            if (!world.isClient() && entity.canUsePortals(false)) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof EndGatewayBlockEntity) {
                    EndGatewayBlockEntity endGateway = (EndGatewayBlockEntity) blockEntity;

                    boolean hasNoExit = endGateway.exitPortalPos == null;

                    boolean noHopperMinecart = true;
                    boolean hasValidHopper = true;

                    if(pos.getX() == 163 && pos.getZ() == 767 && entity instanceof ItemEntity){
                        ci.cancel();
                    }

                    if (pos.getX() > 400 ||pos.getZ() > 400) {
                        if (hasNoExit) {
                            Box detectionArea = new Box(pos.down(2)).expand(0);
                            List<HopperMinecartEntity> minecarts = world.getEntitiesByClass(
                                    HopperMinecartEntity.class,
                                    detectionArea,
                                    Entity::isAlive
                            );
                            noHopperMinecart = minecarts.isEmpty();
                            hasValidHopper = checkPointingHoppersWithItems(world, pos);
                        }
                        if (hasNoExit && hasValidHopper && noHopperMinecart) {
                            ci.cancel();
                        }
                    }
                }
            }
        }
    }
    private boolean checkPointingHoppersWithItems(World world, BlockPos gatewayPos) {
        for (Direction direction : Direction.values()) {
            BlockPos hopperPos = gatewayPos.offset(direction);
            BlockState hopperState = world.getBlockState(hopperPos);

            if (hopperState.getBlock() == Blocks.HOPPER) {
                Direction facing = hopperState.get(HopperBlock.FACING);
                if (facing == direction.getOpposite()) {
                    BlockEntity hopperEntity = world.getBlockEntity(hopperPos);
                    if (hopperEntity instanceof HopperBlockEntity) {
                        Inventory inventory = (Inventory) hopperEntity;
                        for (int i = 0; i < inventory.size(); i++) {
                            if (!inventory.getStack(i).isEmpty()) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}

