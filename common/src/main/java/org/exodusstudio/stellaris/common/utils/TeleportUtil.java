package org.exodusstudio.stellaris.common.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.entities.LanderEntity;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportUtil {


    public static boolean teleportToPlanet(Entity entity, Planet planet, BlockPos pos) {

        ServerLevel planetLevel = entity.level().getServer().getLevel(ResourceKey.create(Registries.DIMENSION, planet.dimension()));

        if(planetLevel == null) return false;

        if(entity.getVehicle() == null || !(entity.getVehicle() instanceof RocketEntity)) return false;

        //TODO fix this
        teleportRocketToPlanet(entity, planetLevel, (RocketEntity) entity.getVehicle(), true);

        return true;
    }

    public static void teleportRocketToPlanet(@Nullable Entity entity, ServerLevel planet, RocketEntity rocket, boolean autopilot) {
        LanderEntity landerEntity = createLander(rocket, planet, rocket.position(), autopilot);
        rocket.discard();

        landerEntity.setNoGravity(true);
        if (entity != null) {
            TeleportUtil.teleportToLevel(entity, planet, new Vec3(entity.getX(), Stellaris.CONFIG.vehicleConfig.rocketTpHeight, entity.getZ()));
            entity.startRiding(landerEntity);
        }
        landerEntity.setNoGravity(false);
    }

    public static void teleportToLevel(Entity entity, @NotNull ServerLevel level, Vec3 coords) {
        entity.teleport(new TeleportTransition(level,coords, Vec3.ZERO, 0, 0, TeleportTransition.DO_NOTHING));
    }

    public static LanderEntity createLander(Entity vehicle, Level level, Vec3 pos, boolean autopilot) {
        if(vehicle instanceof RocketEntity rocket) {
            LanderEntity landerEntity = new LanderEntity(level, autopilot);
            landerEntity.setPos(pos);
            landerEntity.fillInventoryFromRocket(rocket);
            level.addFreshEntity(landerEntity);
            return landerEntity;
        }
        return null;
    }
}
