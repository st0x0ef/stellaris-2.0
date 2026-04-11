package org.exodusstudio.stellaris.common.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> LAKE_OIL_UNDERGROUND = createKey("lake_oil_underground");
    public static final ResourceKey<PlacedFeature> LAKE_OIL_SURFACE = createKey("lake_oil_surface");
    public static final ResourceKey<PlacedFeature> LUNAR_TREE = createKey("lunar_tree");
    public static final ResourceKey<PlacedFeature> CRATER_SMALL = createKey("crater_small");
    public static final ResourceKey<PlacedFeature> CRATER_MEDIUM = createKey("crater_medium");
    public static final ResourceKey<PlacedFeature> CRATER_LARGE = createKey("crater_large");
    public static final ResourceKey<PlacedFeature> BURNT_FOREST_TREE = createKey("burnt_forest_tree");
    public static final ResourceKey<PlacedFeature> ASH_FLOOR = createKey("ash_floor");

    public static final ResourceKey<PlacedFeature> ORE_TITANIUM = createKey("ore_titanium");

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, IdentifierUtils.id(name));
    }
}