package org.exodusstudio.stellaris.common.utils;

import net.minecraft.resources.Identifier;

public class HeartUtils {
    private static final Identifier INFECTED_FULL = IdentifierUtils.id("heart/infected_full");
    private static final Identifier INFECTED_HALF = IdentifierUtils.id("heart/infected_half");
    private static final Identifier INFECTED_FULL_BLINKING = IdentifierUtils.id("heart/infected_full_blinking");
    private static final Identifier INFECTED_HALF_BLINKING = IdentifierUtils.id("heart/infected_half_blinking");
    private static final Identifier INFECTED_FULL_HARDCORE = IdentifierUtils.id("heart/infected_hardcore_full");
    private static final Identifier INFECTED_HALF_HARDCORE = IdentifierUtils.id("heart/infected_hardcore_half");
    private static final Identifier INFECTED_FULL_HARDCORE_BLINKING = IdentifierUtils.id("heart/infected_hardcore_full_blinking");
    private static final Identifier INFECTED_HALF_HARDCORE_BLINKING = IdentifierUtils.id("heart/infected_hardcore_half_blinking");

    public static Identifier getInfectedSprite(boolean hardcore, boolean halfHeart, boolean blinking) {
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
