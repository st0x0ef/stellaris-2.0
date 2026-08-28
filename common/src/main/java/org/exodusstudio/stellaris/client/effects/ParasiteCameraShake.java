package org.exodusstudio.stellaris.client.effects;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public final class ParasiteCameraShake {
    private static final float MIN_STRENGTH = 0.01F;
    private static final float STRENGTH_DECAY = 0.92F;
    private static final float END_FADE_TICKS = 4.0F;

    private static int ticksRemaining;
    private static float intensity;
    private static float phase;

    private static float previousPitchOffset;
    private static float pitchOffset;
    private static float previousYawOffset;
    private static float yawOffset;

    private ParasiteCameraShake() {
    }

    public static void start(int durationTicks, float strength) {
        if (durationTicks <= 0 || !Float.isFinite(strength) || strength <= 0.0F) {
            return;
        }

        if (ticksRemaining <= 0 && !hasCameraOffset()) {
            phase = 0.0F;
        }

        ticksRemaining = Math.max(ticksRemaining, durationTicks);
        intensity = Math.max(intensity, strength);
    }

    public static void clientTick(Minecraft minecraft) {
        if (minecraft.player == null || minecraft.level == null) {
            reset();
            return;
        }

        previousPitchOffset = pitchOffset;
        previousYawOffset = yawOffset;

        if (ticksRemaining <= 0 || intensity <= MIN_STRENGTH) {
            ticksRemaining = 0;
            intensity = 0.0F;
            pitchOffset = 0.0F;
            yawOffset = 0.0F;
            return;
        }

        phase += 0.82F;
        float endFade = Mth.clamp(ticksRemaining / END_FADE_TICKS, 0.0F, 1.0F);
        float strength = intensity * endFade;

        pitchOffset = Mth.sin(phase * 2.05F) * strength * 0.60F;
        yawOffset = Mth.sin(phase * 1.37F + 0.35F) * strength * 0.75F;

        ticksRemaining--;
        intensity *= STRENGTH_DECAY;
    }

    public static boolean hasCameraOffset() {
        return Math.abs(previousPitchOffset) > 0.0001F
                || Math.abs(pitchOffset) > 0.0001F
                || Math.abs(previousYawOffset) > 0.0001F
                || Math.abs(yawOffset) > 0.0001F;
    }

    public static float pitchOffset(float partialTick) {
        return Mth.lerp(clampPartialTick(partialTick), previousPitchOffset, pitchOffset);
    }

    public static float yawOffset(float partialTick) {
        return Mth.lerp(clampPartialTick(partialTick), previousYawOffset, yawOffset);
    }

    private static float clampPartialTick(float partialTick) {
        return Mth.clamp(partialTick, 0.0F, 1.0F);
    }

    private static void reset() {
        ticksRemaining = 0;
        intensity = 0.0F;
        phase = 0.0F;
        previousPitchOffset = 0.0F;
        pitchOffset = 0.0F;
        previousYawOffset = 0.0F;
        yawOffset = 0.0F;
    }
}
