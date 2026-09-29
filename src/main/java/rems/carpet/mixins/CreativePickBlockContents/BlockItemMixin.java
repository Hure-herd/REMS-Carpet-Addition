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

package rems.carpet.mixins.CreativePickBlockContents;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.LeveledCauldronBlock;
//#if MC>=12100
//$$ import net.minecraft.component.DataComponentTypes;
//$$ import net.minecraft.component.type.NbtComponent;
//#endif
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rems.carpet.REMSSettings;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    private static final String CONTENT_KEY = "rems:cauldron_content";
    private static final String LEVEL_KEY = "rems:cauldron_level";

    @Inject(
            method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
            at = @At("RETURN")
    )
    private void restoreContentsAfterPlacement(
            ItemPlacementContext context,
            CallbackInfoReturnable<ActionResult> cir
    ) {
        if (!REMSSettings.creativeCtrlPickBlockContents || !cir.getReturnValue().isAccepted()) {
            return;
        }

        BlockItem self = (BlockItem) (Object) this;
        if (self.getBlock() != Blocks.CAULDRON && self.getBlock() != Blocks.COMPOSTER) {
            return;
        }

        //#if MC>=12100
        //$$ NbtComponent customData = context.getStack().get(DataComponentTypes.CUSTOM_DATA);
        //$$ if (customData == null) {
        //$$     return;
        //$$ }
        //$$ NbtCompound nbt = customData.copyNbt();
        //#else
        NbtCompound nbt = context.getStack().getNbt();
        if (nbt == null) {
            return;
        }
        //#endif

        //#if MC>=12108
        //$$ String contents = nbt.getString(CONTENT_KEY, "");
        //$$ int level = nbt.getInt(LEVEL_KEY, 0);
        //#else
        String contents = nbt.getString(CONTENT_KEY);
        int level = nbt.getInt(LEVEL_KEY);
        //#endif
        BlockState copiedState = switch (contents) {
            case "composter" -> Blocks.COMPOSTER.getDefaultState()
                    .with(ComposterBlock.LEVEL, Math.max(0, Math.min(8, level)));
            case "water" -> Blocks.WATER_CAULDRON.getDefaultState()
                    .with(LeveledCauldronBlock.LEVEL, Math.max(1, Math.min(3, level)));
            case "lava" -> Blocks.LAVA_CAULDRON.getDefaultState();
            case "powder_snow" -> Blocks.POWDER_SNOW_CAULDRON.getDefaultState()
                    .with(LeveledCauldronBlock.LEVEL, Math.max(1, Math.min(3, level)));
            default -> null;
        };
        if (copiedState != null) {
            context.getWorld().setBlockState(context.getBlockPos(), copiedState, 3);
        }
    }
}
