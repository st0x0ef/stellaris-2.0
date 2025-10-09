package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.entities.Rocket;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class EntityTypesRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = DeferredRegister.create(Stellaris.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<Rocket>> ROCKET = ENTITY_TYPE.register("rocket",
            () -> EntityType.Builder.of(Rocket::new, MobCategory.MISC).sized(1.1f, 4.4f).fireImmune().build(ResourceLocationUtils.resourceKey(Registries.ENTITY_TYPE, "rocket")));
}
