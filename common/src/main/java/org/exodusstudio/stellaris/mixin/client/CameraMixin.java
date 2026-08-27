package org.exodusstudio.stellaris.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossIntroController;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossDeathController;
import org.exodusstudio.stellaris.client.effects.ParasiteCameraShake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    public abstract float xRot();

    @Shadow
    public abstract float yRot();

    @Shadow
    public abstract float getCameraEntityPartialTicks(DeltaTracker deltaTracker);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Shadow
    protected abstract void setPosition(Vec3 position);

    @Shadow
    public abstract Vec3 position();

    @Inject(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;alignWithEntity(F)V",
                    shift = At.Shift.AFTER
            )
    )
    private void stellaris$applyCameraEffects(DeltaTracker deltaTracker, CallbackInfo ci) {
        float partialTick = this.getCameraEntityPartialTicks(deltaTracker);
        StarCrawlerBossDeathController.CameraPose deathPose =
                StarCrawlerBossDeathController.sampleCamera(
                        this.position(),
                        this.yRot(),
                        this.xRot(),
                        partialTick
                );
        if (deathPose != null) {
            this.setPosition(deathPose.position());
            this.setRotation(deathPose.yaw(), deathPose.pitch());
        } else {
            StarCrawlerBossIntroController.CameraPose introPose =
                    StarCrawlerBossIntroController.sampleCamera(
                            this.position(),
                            this.yRot(),
                            this.xRot(),
                            partialTick
                    );
            if (introPose != null) {
                this.setPosition(introPose.position());
                this.setRotation(introPose.yaw(), introPose.pitch());
            }
        }

        if (!ParasiteCameraShake.hasCameraOffset()
                || StarCrawlerBossIntroController.shouldSuppressCameraShake()
                || StarCrawlerBossDeathController.shouldSuppressCameraShake()) {
            return;
        }

        float pitch = Mth.clamp(
                this.xRot() + ParasiteCameraShake.pitchOffset(partialTick),
                -90.0F,
                90.0F
        );

        this.setRotation(
                this.yRot() + ParasiteCameraShake.yawOffset(partialTick),
                pitch
        );
    }
}
