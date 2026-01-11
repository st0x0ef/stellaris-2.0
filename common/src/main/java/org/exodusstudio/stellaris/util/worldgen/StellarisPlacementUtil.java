package org.exodusstudio.stellaris.util.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.worldgen.placement.StellarisTreePlacements;
import org.jetbrains.annotations.NotNull;

public class StellarisPlacementUtil {
    public static void bootstrap(BootstrapContext<@NotNull PlacedFeature> context) {
        StellarisTreePlacements.bootstrap(context);
    }

    public static ResourceKey<@NotNull PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Stellaris.MOD_ID, name));
    }
}
