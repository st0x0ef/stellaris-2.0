package org.exodusstudio.stellaris.common.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.exodusstudio.stellaris.Stellaris;

public record FeaturesRecord(String key, ResourceKey<ConfiguredFeature<?, ?>> configuredFeature, ResourceKey<PlacedFeature> placedFeature) {
    public static FeaturesRecord create(String key) {
        return new FeaturesRecord(key, createConfiguredFeature(key), createPlacementFeature(key));
    }

    public static ResourceKey<PlacedFeature> createPlacementFeature(String key) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Stellaris.MOD_ID, key));
    }

    public static ResourceKey<ConfiguredFeature<?,?>> createConfiguredFeature(String key) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(Stellaris.MOD_ID, key));
    }
}
