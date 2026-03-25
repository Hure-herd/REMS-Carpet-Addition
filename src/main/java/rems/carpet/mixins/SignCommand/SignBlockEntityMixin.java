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

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSServer;
import rems.carpet.REMSSettings;
import rems.carpet.utils.SignCommand;

import java.util.List;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin {
    @Inject(method = "tryChangeText", at = @At("HEAD"), cancellable = true)
    public void PreventChangeTextWhenEmptyHands(PlayerEntity player, boolean front, List<FilteredMessage> messages, CallbackInfo ci) {
        if (REMSSettings.SignCommand) {
            if (player instanceof ServerPlayerEntity) {
                REMSServer.LOGGER.debug("Player is trying to change the text, checking sign text");
                ServerWorld world = (ServerWorld) player.getEntityWorld();
                BlockPos pos = ((SignBlockEntity) (Object) this).getPos();
                if (world.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity) {
                    boolean isFront = signBlockEntity.isPlayerFacingFront(player);
                    SignText texts = signBlockEntity.getText(isFront);
                    Text[] text = texts.getMessages(false);
                    REMSServer.LOGGER.debug("Sign text: " + text[0].getString());
                    if (text[0].getString().startsWith("/")) {
                        REMSServer.LOGGER.debug("Player is trying to change the text, but the text starts with /");
                        Text message = Text.literal(SignCommand.getTranslation("carpet.runCommandOnSignTips"));
                        player.sendMessage(message, false);
                    }
                }
            }
        }
    }
}
