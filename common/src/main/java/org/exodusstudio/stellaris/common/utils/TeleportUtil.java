package org.exodusstudio.stellaris.common.utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.entities.LanderEntity;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.exodusstudio.stellaris.common.entities.VehicleEntity;
import org.jetbrains.annotations.NotNull;

public class TeleportUtil {


    public static boolean teleportToPlanet(Entity entity, Planet planet) {

        ServerLevel level = entity.level().getServer().getLevel(ResourceKey.create(Registries.DIMENSION, planet.dimension()));

        if(level == null) return false;

        Entity playerVehicle = entity.getVehicle();

        TeleportUtil.teleportToLevel(entity, level, new Vec3(entity.getX(), 600, entity.getZ()));

        if(playerVehicle == null) return false;

        LanderEntity landerEntity = createLander(playerVehicle, level, entity.position());

        playerVehicle.discard();

        if(landerEntity == null) return false;

        landerEntity.setNoGravity(true);
        entity.startRiding(landerEntity);
        landerEntity.setNoGravity(false);

        return true;
    }

    public static void teleportToLevel(Entity entity, @NotNull ServerLevel level, Vec3 coords) {
        entity.teleport(new TeleportTransition(level,coords, Vec3.ZERO, 0, 0, TeleportTransition.DO_NOTHING));
    }

    public static LanderEntity createLander(Entity vehicle, Level level, Vec3 pos) {
        if(vehicle instanceof RocketEntity rocket) {
            LanderEntity landerEntity = new LanderEntity(level);
            landerEntity.setPos(pos);
            landerEntity.fillInventoryFromRocket(rocket);
            level.addFreshEntity(landerEntity);
            return landerEntity;
        }
        return null;
    }
}
