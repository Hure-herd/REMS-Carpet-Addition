/*
 * This file is part of the REMS-Carpet-Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 A Minecraft Server and contributors
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

package rems.carpet.mixins.FlushFakePlayerNetworkQueue;

import carpet.patches.FakeClientConnection;
import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {

    @Shadow private Channel channel;

    @Unique private int fakePlayerCleanupTimer = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void flushFakePlayerPackets(CallbackInfo ci) {
        if(!REMSSettings.flushFakePlayerNetworkQueue)return;
        if (this.fakePlayerCleanupTimer++ % 1200 == 0) {
            if (this.channel instanceof EmbeddedChannel embeddedChannel) {
                embeddedChannel.releaseOutbound();
            }
        }
    }
}
