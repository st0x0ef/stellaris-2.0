package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.exodusstudio.stellaris.common.world.ModPlacedFeatures;

public class BiomeModificationsRegistry {

    public static void register() {
        BiomeModifications.addProperties((context) -> context.hasTag(BiomeTags.IS_OVERWORLD), ((biomeContext, mutable) -> {
            mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, ModPlacedFeatures.LAKE_OIL_SURFACE);

            mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, ModPlacedFeatures.LAKE_OIL_UNDERGROUND);

            mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.ORE_TITANIUM);
        }));

        BiomeModifications.addProperties((context) -> context.hasTag(TagsRegistry.BiomeTags.IS_MOON), ((biomeContext, mutable) -> {
            mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.MOON_TITANIUM_ORE);
            mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.MOON_DESH_ORE);
            mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.MOON_STONE_IRON_ORE);
        }));
    }

}
