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

//#if MC>=12104
//$$ import net.minecraft.block.BlockState;
//$$ import net.minecraft.block.Blocks;
//$$ import net.minecraft.block.ComposterBlock;
//$$ import net.minecraft.block.LeveledCauldronBlock;
//$$ import net.minecraft.component.DataComponentTypes;
//$$ import net.minecraft.component.type.NbtComponent;
//$$ import net.minecraft.item.ItemStack;
//$$ import net.minecraft.server.network.ServerPlayNetworkHandler;
//$$ import net.minecraft.util.math.BlockPos;
//$$ import net.minecraft.world.WorldView;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//$$ import rems.carpet.REMSSettings;
//#else
import rems.carpet.utils.compat.DummyClass;
//#endif
import org.spongepowered.asm.mixin.Mixin;

//#if MC>=12104
//$$ @Mixin(ServerPlayNetworkHandler.class)
//#else
@Mixin(DummyClass.class)
//#endif
public abstract class ServerPlayNetworkHandlerMixin {
//#if MC>=12104
//$$     private static final String CONTENT_KEY = "rems:cauldron_content";
//$$     private static final String LEVEL_KEY = "rems:cauldron_level";
//$$
//$$     @Redirect(
//$$             method = "onPickItemFromBlock",
//$$             at = @At(
//$$                     value = "INVOKE",
//$$                     target = "Lnet/minecraft/block/BlockState;getPickStack(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Z)Lnet/minecraft/item/ItemStack;"
//$$             )
//$$     )
//$$     private ItemStack copyContents(BlockState state, WorldView world, BlockPos pos, boolean includeData) {
//$$         ItemStack stack = state.getPickStack(world, pos, includeData);
//$$         if (!REMSSettings.creativeCtrlPickBlockContents || !includeData || stack.isEmpty()) {
//$$             return stack;
//$$         }
//$$
//$$         if (state.getBlock() == Blocks.COMPOSTER) {
//$$             setContents(stack, "composter", state.get(ComposterBlock.LEVEL));
//$$         } else if (state.getBlock() == Blocks.WATER_CAULDRON) {
//$$             setContents(stack, "water", state.get(LeveledCauldronBlock.LEVEL));
//$$         } else if (state.getBlock() == Blocks.LAVA_CAULDRON) {
//$$             setContents(stack, "lava", 3);
//$$         } else if (state.getBlock() == Blocks.POWDER_SNOW_CAULDRON) {
//$$             setContents(stack, "powder_snow", state.get(LeveledCauldronBlock.LEVEL));
//$$         }
//$$         return stack;
//$$     }
//$$
//$$     private static void setContents(ItemStack stack, String contents, int level) {
//$$         NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> {
//$$             nbt.putString(CONTENT_KEY, contents);
//$$             nbt.putInt(LEVEL_KEY, level);
//$$         });
//$$     }
//#else
    // Item picking is handled by MinecraftClientMixin on older versions.
//#endif
}
