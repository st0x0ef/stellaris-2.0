package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;

import net.minecraft.util.Mth;

public final class HeartOfLunaHudMotion {
    private double x, y, velocityX, velocityY;
    private int impacts;

    public void impulse(float damage, boolean phase, boolean discharge) {
        if (damage <= 0 && !phase && !discharge) return;
        double strength = Math.min(1.8, damage / 28.0) + (phase ? 1.4 : 0) + (discharge ? 1.6 : 0);
        velocityX += (++impacts % 2 == 0 ? -1 : 1) * strength * 20;
        velocityY -= strength * 9;
        velocityX = Mth.clamp(velocityX, -65, 65);
        velocityY = Mth.clamp(velocityY, -25, 25);
    }

    public Frame sample(double dt, double cycles, float charge, float discharge, float visibility) {
        double remaining = Mth.clamp(dt, 0, 0.1);
        while (remaining > 0) {
            double step = Math.min(remaining, 1.0 / 120);
            velocityX += (-190 * x - 16 * velocityX) * step;
            velocityY += (-230 * y - 20 * velocityY) * step;
            x += velocityX * step;
            y += velocityY * step;
            remaining -= step;
        }
        double phase = cycles - Math.floor(cycles);
        double first = Math.exp(-Math.pow((phase - 0.12) / 0.065, 2));
        double second = Math.exp(-Math.pow((phase - 0.30) / 0.055, 2)) * 0.6;
        float beat = (float)(first + second);
        float tremor = charge * charge * (float)Math.sin(cycles * Math.PI * 17) * 0.45F;
        return new Frame((float)Mth.clamp(x + tremor, -4, 4) * visibility,
                (float)Mth.clamp(y - beat * 0.3, -1.35, 1.35) * visibility,
                1 + (beat * 0.007F - charge * 0.022F + discharge * 0.025F) * visibility,
                1 + (beat * 0.035F + charge * 0.025F) * visibility, beat);
    }

    public record Frame(float x, float y, float scaleX, float scaleY, float beat) {}
}
