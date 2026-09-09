package org.exodusstudio.stellaris.client.cinematic;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class HeartOfLunaCameraPath {
    public static Shot sample(Vec3 origin, float yaw, boolean death, float ticks, float duration) {
        float progress = smooth(ticks / duration);
        double angle = -yaw * Mth.DEG_TO_RAD + (death ? 0.65 + progress * 0.28 : -0.7 + progress * 0.65);
        double radius = death ? Mth.lerp(progress, 12, 10.5) : Mth.lerp(smooth(ticks / 125), 15, 10.5);
        double aimHeight = death ? Mth.lerp(smooth((ticks - 10) / 35), 3.6, 1.55) : 4.05;
        Vec3 aim = origin.add(0, aimHeight, 0);
        Vec3 camera = origin.add(Math.sin(angle) * radius, death ? 5.8 - progress * 1.5 : 5.15, Math.cos(angle) * radius);
        return new Shot(camera, aim, smooth(ticks / 32), smooth((ticks - duration + 32) / 32));
    }

    public static float titleOpacity(boolean death, float ticks, float duration) {
        return smooth((ticks - (death ? 60 : 118)) / 12) * smooth((duration - ticks - 4) / 14);
    }

    private static float smooth(float value) {
        float x = Mth.clamp(value, 0, 1);
        return x * x * x * (x * (x * 6 - 15) + 10);
    }

    public record Shot(Vec3 camera, Vec3 aim, float entrance, float exit) {}
    private HeartOfLunaCameraPath() {}
}
