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

package rems.carpet.mixins.sharedVillagerDiscounts;

import rems.carpet.REMSSettings;
import net.minecraft.village.VillageGossipType;
import net.minecraft.village.VillagerGossips;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.UUID;
import java.util.function.Predicate;

@Mixin(VillagerGossips.class)
public abstract class VillagerGossipsMixin implements VillagerGossipsAccessor, VillagerGossips_ReputationInvoker {
    @Inject(method = "getReputationFor(Ljava/util/UUID;Ljava/util/function/Predicate;)I", at = @At("HEAD"), cancellable = true)
    private void getReputation(UUID target, Predicate<VillageGossipType> filter, CallbackInfoReturnable<Integer> cir) {
        if (REMSSettings.sharedVillagerDiscounts && filter.test(VillageGossipType.MAJOR_POSITIVE)) {
            VillagerGossips_ReputationInvoker targetReputation = (VillagerGossips_ReputationInvoker) this.getEntityReputation().get(target);
            int otherRep = 0;
            if (targetReputation != null) {
                otherRep = targetReputation.invokeGetValueFor(vgt -> filter.test(vgt) && !vgt.equals(VillageGossipType.MAJOR_POSITIVE));
            }
            int majorPositiveRep = 0;
            for (Object reputation : this.getEntityReputation().values()) {
                VillagerGossips_ReputationInvoker invoker = (VillagerGossips_ReputationInvoker) reputation;
                majorPositiveRep += invoker.invokeGetValueFor(vgt -> vgt.equals(VillageGossipType.MAJOR_POSITIVE));
            }
            int maxMajorPositiveRep = VillageGossipType.MAJOR_POSITIVE.maxValue * VillageGossipType.MAJOR_POSITIVE.multiplier;
            cir.setReturnValue(otherRep + Math.min(majorPositiveRep, maxMajorPositiveRep));
        }
    }
}
