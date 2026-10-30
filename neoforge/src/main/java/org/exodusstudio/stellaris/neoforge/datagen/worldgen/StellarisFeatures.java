package org.exodusstudio.stellaris.neoforge.datagen.worldgen;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.exodusstudio.stellaris.common.world.constructed.StellarisCaveFeatures;
import org.exodusstudio.stellaris.common.world.constructed.StellarisOreFeatures;
import org.exodusstudio.stellaris.common.world.constructed.StellarisSurfaceFeatures;

public class StellarisFeatures {
    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?,?>> context) {
        StellarisOreFeatures.bootstrapConfigured(context);
        StellarisCaveFeatures.bootstrapConfigured(context);
        StellarisSurfaceFeatures.bootstrapConfigured(context);
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        StellarisOreFeatures.bootstrapPlaced(context);
        StellarisCaveFeatures.bootstrapPlaced(context);
        StellarisSurfaceFeatures.bootstrapPlaced(context);

    }
}
