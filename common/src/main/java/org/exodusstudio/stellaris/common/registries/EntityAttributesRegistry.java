package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import org.exodusstudio.stellaris.common.entities.alien.AlienEntity;
import org.exodusstudio.stellaris.common.entities.mobs.*;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawler.StarCrawlerEntity;

public class EntityAttributesRegistry {
    public static void register() {
        EntityAttributeRegistry.register(EntityTypesRegistry.BLUE_FISH, BlueFishEntity::createAttributes);
        EntityAttributeRegistry.register(EntityTypesRegistry.LUNAR_PARASITE, LunarParasiteEntity::createAttributes);
        EntityAttributeRegistry.register(EntityTypesRegistry.PARASITE_AFFECTED_VILLAGER, ParasiteAffectedVillagerEntity::createAttributes);
        EntityAttributeRegistry.register(EntityTypesRegistry.PARASITE_AFFECTED_VILLAGER_EVOLVED, EvolvedParasiteAffectedVillagerEntity::createAttributes);
        EntityAttributeRegistry.register(EntityTypesRegistry.LUNA_SHADOW, LunaShadowEntity::createAttributes);
        EntityAttributeRegistry.register(EntityTypesRegistry.STAR_CRAWLER, StarCrawlerEntity::createAttributes);
        EntityAttributeRegistry.register(EntityTypesRegistry.ALIEN, AlienEntity::createAttributes);
    }
}
