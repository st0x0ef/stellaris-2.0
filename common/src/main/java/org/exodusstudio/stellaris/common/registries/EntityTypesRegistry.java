package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.entities.alien.AlienEntity;
import org.exodusstudio.stellaris.common.entities.mobs.*;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawler.StarCrawlerEntity;
import org.exodusstudio.stellaris.common.entities.vehicles.LanderEntity;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.entities.vehicles.RoverEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.function.Function;

public class EntityTypesRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = DeferredRegister.create(Stellaris.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<RocketEntity>> ROCKET = register("rocket",
            RocketEntity::new, MobCategory.MISC,
            builder -> builder.sized(1.1f, 4.4f).fireImmune());

    public static final RegistrySupplier<EntityType<RoverEntity>> ROVER = register("rover",
            RoverEntity::new, MobCategory.MISC, builder -> builder.sized(2.5f, 2.2f));

    public static final RegistrySupplier<EntityType<LanderEntity>> LANDER = register("lander",
            LanderEntity::new, MobCategory.MISC, builder -> builder.sized(2.5f, 2.2f));

    public static final RegistrySupplier<EntityType<Boat>> LUNAR_BOAT = register("lunar_boat",
            (e, l) -> new Boat(e, l, ItemsRegistry.LUNAR_BOAT), MobCategory.MISC,
            builder -> builder.sized(1.375f, 0.5625f));

    public static final RegistrySupplier<EntityType<ChestBoat>> LUNAR_CHEST_BOAT = register("lunar_chest_boat",
            (e, l) -> new ChestBoat(e, l, ItemsRegistry.LUNAR_CHEST_BOAT), MobCategory.MISC,
            builder -> builder.sized(1.375f, 0.5625f));

    public static final RegistrySupplier<EntityType<BlueFishEntity>> BLUE_FISH = register("blue_fish",
        BlueFishEntity::new, MobCategory.WATER_AMBIENT, builder -> builder.sized(0.78F, 0.56F));

    public static final RegistrySupplier<EntityType<LunarParasiteEntity>> LUNAR_PARASITE = register("lunar_parasite",
            LunarParasiteEntity::new, MobCategory.MONSTER, builder -> builder.sized(0.65F, 0.35F));

    public static final RegistrySupplier<EntityType<ParasiteAffectedVillagerEntity>> PARASITE_AFFECTED_VILLAGER = register("parasite_affected_villager",
            ParasiteAffectedVillagerEntity::new, MobCategory.MONSTER, builder -> builder.sized(0.6F, 1.95F));

    public static final RegistrySupplier<EntityType<EvolvedParasiteAffectedVillagerEntity>> PARASITE_AFFECTED_VILLAGER_EVOLVED =
            register("parasite_affected_villager_evolved", EvolvedParasiteAffectedVillagerEntity::new, MobCategory.MONSTER,
            builder -> builder.sized(0.85F, 2.35F));

    public static final RegistrySupplier<EntityType<LunaShadowEntity>> LUNA_SHADOW = register("luna_shadow",
        LunaShadowEntity::new, MobCategory.MONSTER,
            builder -> builder.sized(1.1F, 2.6F).fireImmune());

    public static final RegistrySupplier<EntityType<StarCrawlerEntity>> STAR_CRAWLER = register("star_crawler",
        StarCrawlerEntity::new, MobCategory.MONSTER,
            builder -> builder.sized(1.45F, 1.25F).clientTrackingRange(8));

    public static final RegistrySupplier<EntityType<AlienEntity>> ALIEN = register("alien",
        AlienEntity::new, MobCategory.CREATURE,
            builder -> builder.sized(0.75F, 2.5F));

    private static <T extends Entity> RegistrySupplier<EntityType<T>> register(String id, EntityType.EntityFactory<T> factory,
                                                                               MobCategory category,
                                                                               Function<EntityType.Builder<T>, EntityType.Builder<T>> builder) {
        return ENTITY_TYPE.register(id,
                () -> builder.apply(EntityType.Builder.of(factory, category))
                        .build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, id)));
    }
}
