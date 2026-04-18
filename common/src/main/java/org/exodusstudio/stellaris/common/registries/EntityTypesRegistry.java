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
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class EntityTypesRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = DeferredRegister.create(Stellaris.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<RocketEntity>> ROCKET = ENTITY_TYPE.register("rocket",
            () -> EntityType.Builder.of(RocketEntity::new, MobCategory.MISC).sized(1.1f, 4.4f).fireImmune().build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, "rocket")));

    public static final RegistrySupplier<EntityType<LanderEntity>> LANDER = ENTITY_TYPE.register("lander",
            () -> EntityType.Builder.<LanderEntity>of(LanderEntity::new, MobCategory.MISC).sized(2.5f, 2.2f).build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, "lander")));

    public static final RegistrySupplier<EntityType<Boat>> LUNAR_BOAT = ENTITY_TYPE.register("lunar_boat",
            () -> EntityType.Builder.<Boat>of((e, l) -> new Boat(e, l, ItemsRegistry.LUNAR_BOAT), MobCategory.MISC).sized(1.375f, 0.5625f).build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, "lunar_boat")));

    public static final RegistrySupplier<EntityType<ChestBoat>> LUNAR_CHEST_BOAT = ENTITY_TYPE.register("lunar_chest_boat",
            () -> EntityType.Builder.<ChestBoat>of((e, l) -> new ChestBoat(e, l, ItemsRegistry.LUNAR_CHEST_BOAT), MobCategory.MISC).sized(1.375f, 0.5625f).build(IdentifierUtils.resourceKey(Registries.ENTITY_TYPE, "lunar_boat")));
}
