package rems.carpet.mixins.scheduledRandomTick;

import net.minecraft.block.AbstractPlantPartBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rems.carpet.REMSSettings;

import java.util.Random;

@Mixin(AbstractPlantPartBlock.class)
public abstract class AbstractPlantPartBlockMixin {
    @Inject(
            method = "scheduledTick",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/server/world/ServerWorld;breakBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"
            ),
            cancellable = true
    )
    private void scheduleTickMixinInvoke(CallbackInfo ci) {
        if (REMSSettings.scheduledRandomTickPlants) {
            ci.cancel();
        }
    }

    @Inject(method = "scheduledTick", at = @At("TAIL"))
    private void scheduleTickMixinTail(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random, CallbackInfo ci) {
        if (state.getBlock() instanceof AbstractPlantStemBlock && (REMSSettings.scheduledRandomTickPlants)) {
            state.randomTick(world, pos, random);
        }
    }
}
