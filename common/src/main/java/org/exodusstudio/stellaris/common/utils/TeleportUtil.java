package org.exodusstudio.stellaris.common.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.entities.LanderEntity;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportUtil {


    public static void teleportRocketToPlanet(@Nullable Entity entity, ServerLevel planet, RocketEntity rocket, BlockPos destPos, boolean autopilot) {
        LanderEntity landerEntity = createLander(rocket, planet, destPos.getCenter(), autopilot);
        rocket.discard();

        landerEntity.setNoGravity(true);
        if (entity != null) {
            TeleportUtil.teleportToLevel(entity, planet, new Vec3(destPos.getX(), Stellaris.CONFIG.vehicleConfig.rocketTpHeight, destPos.getZ()));
            entity.startRiding(landerEntity);
        }
        landerEntity.setNoGravity(false);
    }

    public static void teleportToLevel(Entity entity, @NotNull ServerLevel level, Vec3 coords) {
        entity.teleport(new TeleportTransition(level, coords, Vec3.ZERO, 0, 0, TeleportTransition.DO_NOTHING));
    }

    public static LanderEntity createLander(RocketEntity rocket, ServerLevel level, Vec3 pos, boolean autopilot) {
        LanderEntity landerEntity = new LanderEntity(level, autopilot);
        landerEntity.setPos(pos);
        landerEntity.fillInventoryFromRocket(rocket);
        level.addFreshEntity(landerEntity);
        return landerEntity;
    }
}