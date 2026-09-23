package org.exodusstudio.stellaris.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;

public final class DamageTypesRegistry {
    public static final ResourceKey<DamageType> LUNAR_PULSE = key("lunar_pulse");
    public static final ResourceKey<DamageType> OXYGEN_DEPRIVATION = key("oxygen_deprivation");
    public static final ResourceKey<DamageType> ROVER_IMPACT = key("rover_impact");
    public static final ResourceKey<DamageType> CORROSION = key("corrosion");
    public static final ResourceKey<DamageType> INFECTION = key("infection");

    private DamageTypesRegistry() {}

    private static ResourceKey<DamageType> key(String path) {
        return IdentifierUtils.resourceKey(Registries.DAMAGE_TYPE, path);
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> type) {
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(type));
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> type, @Nullable Entity direct, @Nullable Entity causing) {
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(type), direct, causing);
    }
}
