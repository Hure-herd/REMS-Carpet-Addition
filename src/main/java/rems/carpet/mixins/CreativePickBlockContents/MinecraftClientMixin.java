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

//#if MC<=12101
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
//#if MC>=12100
//$$ import net.minecraft.component.DataComponentTypes;
//$$ import net.minecraft.component.type.NbtComponent;
//#endif
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
//#if MC>=12100
//$$ import net.minecraft.world.WorldView;
//#else
import net.minecraft.world.BlockView;
//#endif
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
//#else
//$$ import rems.carpet.utils.compat.DummyClass;
//#endif
import org.spongepowered.asm.mixin.Mixin;

//#if MC<=12101
@Mixin(MinecraftClient.class)
//#else
//$$ @Mixin(DummyClass.class)
//#endif
public abstract class MinecraftClientMixin {
    //#if MC<=12101
    private static final String CONTENT_KEY = "rems:cauldron_content";
    private static final String LEVEL_KEY = "rems:cauldron_level";

    @Redirect(
            method = "doItemPick",
            at = @At(
                    value = "INVOKE",
                    //#if MC>=12100
                    //$$ target = "Lnet/minecraft/block/Block;getPickStack(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;"
                    //#else
                    target = "Lnet/minecraft/block/Block;getPickStack(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;"
                    //#endif
            )
    )
    private ItemStack copyContents(
            Block block,
            //#if MC>=12100
            //$$ WorldView world,
            //#else
            BlockView world,
            //#endif
            BlockPos pos,
            BlockState state
    ) {
        ItemStack stack = block.getPickStack(world, pos, state);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null
                || !client.player.getAbilities().creativeMode
                || !Screen.hasControlDown()
                || stack.isEmpty()) {
            return stack;
        }

        if (state.getBlock() == Blocks.COMPOSTER) {
            setContents(stack, "composter", state.get(ComposterBlock.LEVEL));
        } else if (state.getBlock() == Blocks.WATER_CAULDRON) {
            setContents(stack, "water", state.get(LeveledCauldronBlock.LEVEL));
        } else if (state.getBlock() == Blocks.LAVA_CAULDRON) {
            setContents(stack, "lava", 3);
        } else if (state.getBlock() == Blocks.POWDER_SNOW_CAULDRON) {
            setContents(stack, "powder_snow", state.get(LeveledCauldronBlock.LEVEL));
        }
        return stack;
    }

    private static void setContents(ItemStack stack, String contents, int level) {
        //#if MC>=12100
        //$$ NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> {
        //$$     nbt.putString(CONTENT_KEY, contents);
        //$$     nbt.putInt(LEVEL_KEY, level);
        //$$ });
        //#else
        stack.getOrCreateNbt().putString(CONTENT_KEY, contents);
        stack.getOrCreateNbt().putInt(LEVEL_KEY, level);
        //#endif
    }
    //#else
    //$$ // Item picking is handled by ServerPlayNetworkHandlerMixin on newer versions.
    //#endif
}
