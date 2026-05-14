package org.exodusstudio.stellaris.client.effects;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class ParasiteCameraShake {
    private static int ticks;
    private static float intensity;

    public static void start(int durationTicks, float strength) {
        ticks = Math.max(ticks, durationTicks);
        intensity = Math.max(intensity, strength);
    }

    public static void clientTick(Minecraft minecraft) {
        Player player = minecraft.player;
        if (player == null || ticks <= 0 || intensity <= 0.01F) {
            return;
        }

        float phase = (ticks + player.tickCount) * 0.77F;
        float pitchShake = Mth.sin(phase) * intensity * 0.36F;
        float yawShake = Mth.cos(phase * 1.7F) * intensity * 0.42F;

        player.setXRot(Mth.clamp(player.getXRot() + pitchShake, -90.0F, 90.0F));
        player.setYRot(player.getYRot() + yawShake);
        player.setYHeadRot(player.getYRot());

        ticks--;
        intensity *= 0.92F;
    }
}
