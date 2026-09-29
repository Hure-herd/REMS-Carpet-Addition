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

package rems.carpet.mixins.GlobalSoundSuppression;

//#if MC<12100
import net.minecraft.world.event.GameEvent;
//#else
//$$ import net.minecraft.registry.entry.RegistryEntry;
//$$ import net.minecraft.world.event.GameEvent;
//#endif
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.Vibrations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;
import rems.carpet.utils.GlobalSoundSuppression.UpdateSuppressionContext;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Inject(method = "emitGameEvent", at = @At("HEAD"))
    //#if MC<12100
    private void suppressGameEventsGlobally(GameEvent event, Vec3d emitterPos, GameEvent.Emitter emitter, CallbackInfo ci) {
    //#else
    //$$ private void suppressGameEventsGlobally(RegistryEntry<GameEvent> event, Vec3d emitterPos, GameEvent.Emitter emitter, CallbackInfo ci) {
    //#endif
        if (shouldSuppress(Vibrations.getFrequency(event)) && !isEntityOpenedDoor(event, emitter)) {
            throw UpdateSuppressionContext.createException();
        }
    }

    //#if MC<12100
    private static boolean isEntityOpenedDoor(GameEvent event, GameEvent.Emitter emitter) {
    //#else
    //$$ private static boolean isEntityOpenedDoor(RegistryEntry<GameEvent> event, GameEvent.Emitter emitter) {
    //#endif
        return event == GameEvent.BLOCK_OPEN && emitter.sourceEntity() != null;
    }

    private static boolean shouldSuppress(int frequency) {
        String setting = REMSSettings.globalSoundSuppression;
        String target = Integer.toString(frequency);
        for (String selected : setting.split(",")) {
            if (selected.equals(target)) {
                return true;
            }
        }
        return false;
    }
}
