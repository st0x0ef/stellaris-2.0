package org.exodusstudio.stellaris.util.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.worldgen.feature.StellarisTreeFeatures;
import org.jetbrains.annotations.NotNull;

public class StellarisFeatureUtil {
    public static void bootstrap(BootstrapContext<@NotNull ConfiguredFeature<?, ?>> context) {
        StellarisTreeFeatures.bootstrap(context);
    }
    public static ResourceKey<@NotNull ConfiguredFeature<?, ?>> createKey(String name)
    {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(Stellaris.MOD_ID, name));
    }
}
