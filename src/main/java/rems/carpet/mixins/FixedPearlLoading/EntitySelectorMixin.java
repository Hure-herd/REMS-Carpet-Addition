/*
 * This file is part of the REMS-Carpet-Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 Hureherd and contributors
 */

package rems.carpet.mixins.FixedPearlLoading;

import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;
import rems.carpet.utils.FixedPearlLoading.RetainedPearlIndex;

import java.util.List;
import java.util.function.Predicate;

@Mixin(EntitySelector.class)
public abstract class EntitySelectorMixin {
    @Shadow @Final private TypeFilter<Entity, ?> entityFilter;
    @Shadow @Final private Box box;

    //#if MC<12100
    @Inject(method = "appendEntitiesFromWorld", at = @At("TAIL"))
    private void appendWeaklyLoadedPearls(
            List<Entity> entities,
            ServerWorld world,
            Vec3d origin,
            Predicate<Entity> predicate,
            CallbackInfo ci
    ) {
        Box offsetBox = this.box == null ? null : this.box.offset(origin);
        this.appendWeaklyLoadedPearls(entities, world, offsetBox, predicate);
    }
    //#else
    //$$ @Inject(method = "appendEntitiesFromWorld", at = @At("TAIL"))
    //$$ private void appendWeaklyLoadedPearls(
    //$$         List<Entity> entities,
    //$$         ServerWorld world,
    //$$         Box offsetBox,
    //$$         Predicate<Entity> predicate,
    //$$         CallbackInfo ci
    //$$ ) {
    //$$     this.appendWeaklyLoadedPearls(entities, world, offsetBox, predicate);
    //$$ }
    //#endif

    @Unique
    private void appendWeaklyLoadedPearls(
            List<Entity> entities,
            ServerWorld world,
            Box offsetBox,
            Predicate<Entity> predicate
    ) {
        if (!REMSSettings.fixedpearlloading) {
            return;
        }

        for (EnderPearlEntity pearl : RetainedPearlIndex.get(world)) {
            if (this.entityFilter.downcast(pearl) != null
                    && (offsetBox == null || offsetBox.intersects(pearl.getBoundingBox()))
                    && predicate.test(pearl)
                    && !entities.contains(pearl)) {
                entities.add(pearl);
            }
        }
    }
}
