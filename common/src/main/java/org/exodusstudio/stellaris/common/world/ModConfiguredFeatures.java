package org.exodusstudio.stellaris.common.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> LAKE_OIL = registerKey("lake_oil");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LUNAR_TREE = registerKey("lunar_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CRATER = registerKey("crater");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BURNT_FOREST_TREE = registerKey("burnt_forest_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ASH_FLOOR = registerKey("ash_floor");

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_TITANIUM = registerKey("ore_titanium");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOON_TITANIUM_ORE = registerKey("moon_titanium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOON_DESH_ORE = registerKey("moon_desh_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOON_STONE_IRON_ORE = registerKey("moon_stone_iron_ore");

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, IdentifierUtils.id(name));
    }
}