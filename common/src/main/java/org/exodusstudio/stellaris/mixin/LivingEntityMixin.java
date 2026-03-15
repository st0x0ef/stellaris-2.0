package org.exodusstudio.stellaris.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.entities.VehicleEntity;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.exodusstudio.stellaris.common.utils.GravityUtils;
import org.exodusstudio.stellaris.common.utils.OxygenUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private LivingEntity stellaris$entity = (LivingEntity)(Object)this;

    @Unique
    private int stellaris$gravityCounter = 0;
    @Unique
    private int stellaris$oxygenCounter = 0;

    @Inject(method = "getDefaultGravity", at = @At("RETURN"))
    private void getDefaultGravity(CallbackInfoReturnable<Double> cir) {
        if (stellaris$gravityCounter >= Stellaris.CONFIG.gravityConfig.gravityUpdateInterval) {
            stellaris$gravityCounter = 0;
            GravityUtils.setLivingEntityGravity(stellaris$entity);
        }
        stellaris$gravityCounter++;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (!Stellaris.CONFIG.oxygenConfig.enableOxygenSystem || stellaris$entity.getType().is(TagsRegistry.EntityTags.NO_OXYGEN_NEEDED)) {
            return;
        }

        if (stellaris$oxygenCounter >= Stellaris.CONFIG.oxygenConfig.oxygenDamageInterval) {
            stellaris$oxygenCounter = 0;

            if (stellaris$entity.level() instanceof ServerLevel serverLevel) {
                if (!OxygenUtils.isOxygenated(stellaris$entity.level(), stellaris$entity.blockPosition())) {
                    stellaris$entity.hurtServer(serverLevel, stellaris$entity.damageSources().generic(), Stellaris.CONFIG.oxygenConfig.noOxygenDamage);
                }
            }
        }

        stellaris$oxygenCounter++;
    }


    @Inject(at = @At(value = "HEAD"), method = "rideTick")
    private void getPassengerRidingPosition(CallbackInfo ci) {
        Stellaris.LOG.error("1");
        if(stellaris$entity.getVehicle() instanceof VehicleEntity vehicleEntity) {
            Stellaris.LOG.error("2");
            Stellaris.LOG.error(vehicleEntity.getRiderPose().name());
            stellaris$entity.setPose(vehicleEntity.getRiderPose());
            Stellaris.LOG.error(stellaris$entity.getPose().name());
        }
    }
}
