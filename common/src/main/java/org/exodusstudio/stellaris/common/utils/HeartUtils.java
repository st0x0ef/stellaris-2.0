package org.exodusstudio.stellaris.common.utils;

import net.minecraft.resources.ResourceLocation;

public class HeartUtils {
    private static final ResourceLocation INFECTED_FULL = ResourceLocationUtils.id("heart/infected_full");
    private static final ResourceLocation INFECTED_HALF = ResourceLocationUtils.id("heart/infected_half");
    private static final ResourceLocation INFECTED_FULL_BLINKING = ResourceLocationUtils.id("heart/infected_full_blinking");
    private static final ResourceLocation INFECTED_HALF_BLINKING = ResourceLocationUtils.id("heart/infected_half_blinking");
    private static final ResourceLocation INFECTED_FULL_HARDCORE = ResourceLocationUtils.id("heart/infected_hardcore_full");
    private static final ResourceLocation INFECTED_HALF_HARDCORE = ResourceLocationUtils.id("heart/infected_hardcore_half");
    private static final ResourceLocation INFECTED_FULL_HARDCORE_BLINKING = ResourceLocationUtils.id("heart/infected_hardcore_full_blinking");
    private static final ResourceLocation INFECTED_HALF_HARDCORE_BLINKING = ResourceLocationUtils.id("heart/infected_hardcore_half_blinking");

    public static ResourceLocation getInfectedSprite(boolean hardcore, boolean halfHeart, boolean blinking) {
        if (!hardcore) {
            if (halfHeart) {
                return blinking ? INFECTED_HALF_BLINKING : INFECTED_HALF;
            } else {
                return blinking ? INFECTED_FULL_BLINKING : INFECTED_FULL;
            }
        } else if (halfHeart) {
            return blinking ? INFECTED_HALF_HARDCORE_BLINKING : INFECTED_HALF_HARDCORE;
        } else {
            return blinking ? INFECTED_FULL_HARDCORE_BLINKING : INFECTED_FULL_HARDCORE;
        }
    }
}
