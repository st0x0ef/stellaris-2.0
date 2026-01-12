package org.exodusstudio.stellaris.worldgen.feature;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.worldgen.feature.configurations.SpreadTreeConfiguration;
import org.exodusstudio.stellaris.worldgen.feature.tree.StellarisTreeFeature;
import org.exodusstudio.stellaris.worldgen.feature.tree.WideTreeFeature;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class StellarisBaseFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Stellaris.MOD_ID, Registries.FEATURE);

    public static final Supplier<StellarisTreeFeature<SpreadTreeConfiguration>> DEAD_TREE;
    public static final Supplier<StellarisTreeFeature<SpreadTreeConfiguration>> WIDE_MOON_TREE;

    static  {
        DEAD_TREE = register("dead_tree", new WideTreeFeature(SpreadTreeConfiguration.CODEC));
        WIDE_MOON_TREE = register("wide_moon_tree", new WideTreeFeature(SpreadTreeConfiguration.CODEC));

    }

    private static <C extends FeatureConfiguration, F extends Feature<@NotNull C>> RegistrySupplier<F> register(String name, F feature)
    {
        return FEATURES.register(name, () -> feature);
    }

    public static void init() {
        FEATURES.register();
    }
}
