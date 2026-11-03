package org.exodusstudio.stellaris.common.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.entities.vehicles.LanderEntity;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportUtil {
    private static final int DRY_LANDING_SEARCH_RADIUS = 32;
    private static final int DRY_LANDING_SEARCH_STEP = 4;

    public static void teleportRocketToPlanet(@Nullable Entity entity, ServerLevel planet, RocketEntity rocket, BlockPos destPos, boolean autopilot) {
        BlockPos landingPos = findDryLandingColumn(planet, destPos);
        Vec3 arrivalPos = new Vec3(landingPos.getX() + 0.5, Stellaris.CONFIG.vehicleConfig.rocketTpHeight, landingPos.getZ() + 0.5);

        LanderEntity landerEntity = createLander(rocket, planet, arrivalPos, autopilot);
        rocket.discard();

        landerEntity.setNoGravity(true);
        if (entity != null) {
            TeleportUtil.teleportToLevel(entity, planet, arrivalPos);
            entity.startRiding(landerEntity, true, false);
        }
        landerEntity.setNoGravity(false);
    }

    private static BlockPos findDryLandingColumn(ServerLevel level, BlockPos origin) {
        for (int ring = 0; ring <= DRY_LANDING_SEARCH_RADIUS; ring += DRY_LANDING_SEARCH_STEP) {
            for (int dx = -ring; dx <= ring; dx += DRY_LANDING_SEARCH_STEP) {
                for (int dz = -ring; dz <= ring; dz += DRY_LANDING_SEARCH_STEP) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) continue;

                    int x = origin.getX() + dx;
                    int z = origin.getZ() + dz;
                    int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                    if (top > level.getMinY() && level.getFluidState(new BlockPos(x, top - 1, z)).isEmpty()) {
                        return new BlockPos(x, origin.getY(), z);
                    }
                }
            }
        }

        return origin;
    }

    public static void teleportFallingEntityToPlanet(Entity entity, ServerLevel planet, BlockPos destPos) {

        Entity vehicle = entity.getVehicle();
        TeleportUtil.teleportToLevel(entity, planet, new Vec3(destPos.getX(), Stellaris.CONFIG.vehicleConfig.rocketTpHeight, destPos.getZ()));

        if(vehicle != null) {
            TeleportUtil.teleportToLevel(vehicle, planet, new Vec3(destPos.getX(), Stellaris.CONFIG.vehicleConfig.rocketTpHeight, destPos.getZ()));
            entity.startRiding(vehicle, true, false);
        }
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