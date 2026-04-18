package org.exodusstudio.stellaris.common.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> LAKE_OIL_UNDERGROUND = createKey("lake_oil_underground");
    public static final ResourceKey<PlacedFeature> LAKE_OIL_SURFACE = createKey("lake_oil_surface");
    public static final ResourceKey<PlacedFeature> LUNAR_TREE = createKey("lunar_tree");
    public static final ResourceKey<PlacedFeature> CRATER = createKey("crater");
    public static final ResourceKey<PlacedFeature> BURNT_FOREST_TREE = createKey("burnt_forest_tree");
    public static final ResourceKey<PlacedFeature> ASH_FLOOR = createKey("ash_floor");

    public static final ResourceKey<PlacedFeature> ORE_TITANIUM = createKey("ore_titanium");
    public static final ResourceKey<PlacedFeature> MOON_TITANIUM_ORE = createKey("moon_titanium_ore");
    public static final ResourceKey<PlacedFeature> MOON_DESH_ORE = createKey("moon_desh_ore");
    public static final ResourceKey<PlacedFeature> MOON_STONE_IRON_ORE = createKey("moon_stone_iron_ore");

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, IdentifierUtils.id(name));
    }
}