package org.exodusstudio.stellaris.common.world.attribute;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttribute;
import org.exodusstudio.stellaris.Stellaris;

public class EnvironmentAttributesRegistry {
    public static final DeferredRegister<EnvironmentAttribute<?>> ENVIRONMENT_ATTRIBUTES = DeferredRegister.create(Stellaris.MOD_ID, Registries.ENVIRONMENT_ATTRIBUTE);
    /** NOTE: This attribute is prioritized over  "minecraft:gameplay/water_evaporates" so if both of these are true freezing will take effect*/
    public static final RegistrySupplier<EnvironmentAttribute<Boolean>> FLASH_FREEZE = ENVIRONMENT_ATTRIBUTES.register("gameplay/flash_freeze", () -> EnvironmentAttribute.builder(AttributeTypes.BOOLEAN).defaultValue(false).syncable().build());

}
