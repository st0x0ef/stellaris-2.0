package org.exodusstudio.stellaris.common.utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.data.Planet;
import org.jetbrains.annotations.NotNull;

public class TeleportUtil {


    public static boolean teleportToPlanet(Entity entity, Planet planet) {

        ServerLevel level = entity.level().getServer().getLevel(ResourceKey.create(Registries.DIMENSION, planet.dimension()));

        if(level == null) return false;

        TeleportUtil.teleportToLevel(entity, level, new Vec3(entity.getX(), 600, entity.getZ()));
        return true;
    }

    public static void teleportToLevel(Entity entity, @NotNull ServerLevel level, Vec3 coords) {
        entity.teleport(new TeleportTransition(level,coords, Vec3.ZERO, 0, 0, TeleportTransition.DO_NOTHING));
    }
}
