package org.exodusstudio.stellaris.util.biome;

import net.minecraft.util.RandomSource;

public class GeneratorUtil {
    public static int nextIntBetween(RandomSource rand, int a, int b)
    {
        if (a == b) {return a;}
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        return min + rand.nextInt(1 + max - min);
    }
}
