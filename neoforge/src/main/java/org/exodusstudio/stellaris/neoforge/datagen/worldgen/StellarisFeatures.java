package org.exodusstudio.stellaris.neoforge.datagen.worldgen;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.exodusstudio.stellaris.common.world.StellarisOreFeatures;

public class StellarisFeatures {
    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?,?>> context) {
        StellarisOreFeatures.bootstrapConfigured(context);
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        StellarisOreFeatures.bootstrapPlaced(context);

    }
}
