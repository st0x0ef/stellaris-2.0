package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.level.biome.BiomeModifications;
import dev.architectury.registry.level.entity.SpawnPlacementsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import org.exodusstudio.stellaris.common.entities.mobs.BlueFishEntity;
import org.exodusstudio.stellaris.common.entities.mobs.LunarParasiteEntity;
import org.exodusstudio.stellaris.common.world.ModPlacedFeatures;

public class BiomeModificationsRegistry {
    private static final SpawnPlacementType IN_WATER_OR_BLUE_LIQUID = (level, pos, entityType) -> {
        if (entityType == null || !level.getWorldBorder().isWithinBounds(pos)) {
            return false;
        }

        BlockPos above = pos.above();
        return isWaterOrBlueLiquid(level, pos)
                && !level.getBlockState(above).isRedstoneConductor(level, above);
    };

    public static void register() {
        BiomeModifications.addProperties((context) -> context.hasTag(BiomeTags.IS_OVERWORLD), ((biomeContext, mutable) -> {
            mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, ModPlacedFeatures.LAKE_OIL_SURFACE);
            mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, ModPlacedFeatures.LAKE_OIL_UNDERGROUND);
            mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.ORE_TITANIUM);

            // Do NOT spawn Moon fish in the Overworld.
        }));

        BiomeModifications.addProperties((context) -> context.hasTag(TagsRegistry.BiomeTags.IS_MOON), ((biomeContext, mutable) -> {
            mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.MOON_TITANIUM_ORE);
            mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.MOON_DESH_ORE);
            mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.MOON_STONE_IRON_ORE);

            // Parasites can spawn anywhere on the Moon.
            // Their spawn predicate makes underground spawns much more likely than surface spawns.
            mutable.getSpawnProperties().addSpawn(
                    MobCategory.MONSTER,
                    new MobSpawnSettings.SpawnerData(EntityTypesRegistry.LUNAR_PARASITE.get(), 1, 3),
                    42
            );

            // Do NOT randomly spawn infected villagers in every Moon biome.
            // They should be added through Moon village structure/template-pool spawning instead.
        }));

        BiomeModifications.addProperties((context) -> context.hasTag(TagsRegistry.BiomeTags.IS_MOON_WATER), ((biomeContext, mutable) -> {
            mutable.getSpawnProperties().addSpawn(
                    MobCategory.WATER_AMBIENT,
                    new MobSpawnSettings.SpawnerData(EntityTypesRegistry.BLUE_FISH.get(), 3, 7),
                    22
            );
        }));

        BiomeModifications.addProperties((context) -> context.hasTag(TagsRegistry.BiomeTags.IS_MOON_FOREST), ((biomeContext, mutable) -> {
            mutable.getSpawnProperties().addSpawn(
                    MobCategory.MONSTER,
                    new MobSpawnSettings.SpawnerData(EntityTypesRegistry.LUNA_SHADOW.get(), 1, 1),
                    12
            );
        }));

        SpawnPlacementsRegistry.register(
                EntityTypesRegistry.BLUE_FISH,
                IN_WATER_OR_BLUE_LIQUID,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BlueFishEntity::checkBlueFishSpawnRules
        );

        SpawnPlacementsRegistry.register(
                EntityTypesRegistry.LUNAR_PARASITE,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                LunarParasiteEntity::checkLunarParasiteSpawnRules
        );

        SpawnPlacementsRegistry.register(
                EntityTypesRegistry.PARASITE_AFFECTED_VILLAGER,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules
        );

        SpawnPlacementsRegistry.register(
                EntityTypesRegistry.PARASITE_AFFECTED_VILLAGER_EVOLVED,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules
        );

        SpawnPlacementsRegistry.register(
                EntityTypesRegistry.LUNA_SHADOW,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BiomeModificationsRegistry::checkMoonMonsterSpawnRules
        );
    }

    private static boolean isWaterOrBlueLiquid(LevelReader level, BlockPos pos) {
        return level.getFluidState(pos).is(FluidTags.WATER)
                || level.getFluidState(pos).is(TagsRegistry.FluidTags.BLUE_LIQUID);
    }

    private static boolean checkMoonMonsterSpawnRules(
            EntityType<? extends Monster> entityType,
            ServerLevelAccessor level,
            EntitySpawnReason spawnReason,
            BlockPos pos,
            RandomSource random
    ) {
        return Monster.checkAnyLightMonsterSpawnRules(entityType, level, spawnReason, pos, random);
    }
}
