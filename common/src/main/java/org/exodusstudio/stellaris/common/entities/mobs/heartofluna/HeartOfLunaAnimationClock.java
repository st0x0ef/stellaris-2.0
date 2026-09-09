package org.exodusstudio.stellaris.common.entities.mobs.heartofluna;

import net.minecraft.util.Mth;

public final class HeartOfLunaAnimationClock {
    private int action = -1;
    private int animation = 1;
    private int previousAnimation = 1;
    private float startAge;
    private float transitionAge;
    private float previousStartAge;

    public Frame sample(int nextAction, int nextAnimation, float ticks, float age) {
        if (action != nextAction || animation != nextAnimation) {
            previousAnimation = action < 0 ? nextAnimation : animation;
            previousStartAge = action < 0 ? age - ticks : startAge;
            transitionAge = action < 0 ? age - 4 : age;
            action = nextAction;
            animation = nextAnimation;
        }
        startAge = age - ticks;
        float blend = animation < 2 && previousAnimation < 2 ? Mth.clamp((age - transitionAge) / 4, 0, 1) : 1;
        return new Frame(animation, ticks, previousAnimation, Math.max(0, age - previousStartAge), blend);
    }

    public record Frame(int animation, float ticks, int previousAnimation, float previousTicks, float blend) {}
}
