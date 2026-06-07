package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.entities.LanderEntity;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.exodusstudio.stellaris.common.entities.mobs.BlueFishEntity;
import org.exodusstudio.stellaris.common.entities.mobs.EvolvedParasiteAffectedVillagerEntity;
import org.exodusstudio.stellaris.common.entities.mobs.LunaShadowEntity;
import org.exodusstudio.stellaris.common.entities.mobs.LunarParasiteEntity;
import org.exodusstudio.stellaris.common.entities.mobs.ParasiteAffectedVillagerEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.entities.vehicles.RoverEntity;

public class EntityTypesRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = DeferredRegister.create(Stellaris.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<RocketEntity>> ROCKET = ENTITY_TYPE.register("rocket",
            () -> EntityType.Builder.of(RocketEntity::new, MobCategory.MISC).sized(1.1f, 4.4f).fireImmune().build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, "rocket")));

    public static final RegistrySupplier<EntityType<RoverEntity>> ROVER = ENTITY_TYPE.register("rover",
            () -> EntityType.Builder.of(RoverEntity::new, MobCategory.MISC).sized(2.5f, 2.2f).build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, "rover")));

    public static final RegistrySupplier<EntityType<LanderEntity>> LANDER = ENTITY_TYPE.register("lander",
            () -> EntityType.Builder.<LanderEntity>of(LanderEntity::new, MobCategory.MISC).sized(2.5f, 2.2f).build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, "lander")));

    public static final RegistrySupplier<EntityType<Boat>> LUNAR_BOAT = ENTITY_TYPE.register("lunar_boat",
            () -> EntityType.Builder.<Boat>of((e, l) -> new Boat(e, l, ItemsRegistry.LUNAR_BOAT), MobCategory.MISC).sized(1.375f, 0.5625f).build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, "lunar_boat")));

    public static final RegistrySupplier<EntityType<ChestBoat>> LUNAR_CHEST_BOAT = ENTITY_TYPE.register("lunar_chest_boat",
            () -> EntityType.Builder.<ChestBoat>of((e, l) -> new ChestBoat(e, l, ItemsRegistry.LUNAR_CHEST_BOAT), MobCategory.MISC).sized(1.375f, 0.5625f).build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, "lunar_boat")));

    public static final RegistrySupplier<EntityType<BlueFishEntity>> BLUE_FISH = ENTITY_TYPE.register("blue_fish",
        () -> EntityType.Builder.of(BlueFishEntity::new, MobCategory.WATER_AMBIENT)
                .sized(0.78F, 0.56F)
                .build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, "blue_fish")));

    public static final RegistrySupplier<EntityType<LunarParasiteEntity>> LUNAR_PARASITE = ENTITY_TYPE.register("lunar_parasite",
            () -> EntityType.Builder.of(LunarParasiteEntity::new, MobCategory.MONSTER).sized(0.65F, 0.35F).build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, "lunar_parasite")));

    public static final RegistrySupplier<EntityType<ParasiteAffectedVillagerEntity>> PARASITE_AFFECTED_VILLAGER = ENTITY_TYPE.register("parasite_affected_villager",
            () -> EntityType.Builder.of(ParasiteAffectedVillagerEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, "parasite_affected_villager")));

    public static final RegistrySupplier<EntityType<EvolvedParasiteAffectedVillagerEntity>> PARASITE_AFFECTED_VILLAGER_EVOLVED = ENTITY_TYPE.register("parasite_affected_villager_evolved",
            () -> EntityType.Builder.of(EvolvedParasiteAffectedVillagerEntity::new, MobCategory.MONSTER).sized(0.85F, 2.35F).build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, "parasite_affected_villager_evolved")));

    public static final RegistrySupplier<EntityType<LunaShadowEntity>> LUNA_SHADOW = ENTITY_TYPE.register("luna_shadow",
        () -> EntityType.Builder.of(LunaShadowEntity::new, MobCategory.MONSTER)
                .sized(1.1F, 2.6F)
                .fireImmune()
                .build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, "luna_shadow")));
}
