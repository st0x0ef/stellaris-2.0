package org.exodusstudio.stellaris.common.entities.mobs.heartofluna;

import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.event.EventResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HeartOfLunaMovementLock {
    private static final Map<UUID, Lock> LOCKS = new HashMap<>();

    public static void init() {
        TickEvent.PLAYER_POST.register(player -> { if (player instanceof ServerPlayer server) tick(server); });
        PlayerEvent.PLAYER_QUIT.register(player -> LOCKS.remove(player.getUUID()));
        PlayerEvent.PLAYER_RESPAWN.register((player, won, reason) -> LOCKS.remove(player.getUUID()));
        PlayerEvent.CHANGE_DIMENSION.register((player, oldLevel, newLevel) -> LOCKS.remove(player.getUUID()));
        LifecycleEvent.SERVER_STOPPING.register(server -> LOCKS.clear());
        EntityEvent.LIVING_DEATH.register((entity, source) -> { LOCKS.remove(entity.getUUID()); return EventResult.pass(); });
    }

    public static void repel(HeartOfLunaBossEntity boss, ServerPlayer player) {
        if (player.isPassenger()) player.stopRiding();
        Vec3 direction = player.position().subtract(boss.position()).horizontal().normalize();
        if (direction.lengthSqr() < 0.1) direction = Vec3.directionFromRotation(0, boss.getYRot());
        Vec3 start = player.position();
        Vec3 end = start;
        for (double distance = 0.25; distance <= 6; distance += 0.25) {
            Vec3 next = start.add(direction.scale(distance));
            if (!player.level().noCollision(player, player.getBoundingBox().move(next.subtract(start)))) break;
            end = next;
        }
        LOCKS.put(player.getUUID(), new Lock(boss.getUUID(), player, start, end, player.level().getGameTime()));
        HeartOfLunaVfx.shake(player, 8, 0.6F);
    }

    public static Vec3 aimPosition(ServerPlayer player) {
        Lock lock = LOCKS.get(player.getUUID());
        return lock == null ? player.getEyePosition() : lock.end.add(0, player.getEyeHeight() * 0.7, 0);
    }

    public static void releaseBoss(UUID boss) { LOCKS.values().removeIf(lock -> lock.boss.equals(boss)); }

    private static void tick(ServerPlayer player) {
        Lock lock = LOCKS.get(player.getUUID());
        if (lock == null) return;
        long elapsed = player.level().getGameTime() - lock.started;
        if (elapsed >= 44 || elapsed < 0 || player != lock.player || !player.isAlive() || player.isRemoved()
                || player.isCreative() || player.isSpectator() || player.position().distanceToSqr(lock.end) > 144) {
            LOCKS.remove(player.getUUID());
            return;
        }
        Vec3 point = elapsed < 4 ? lock.start.lerp(lock.end, (elapsed + 1) / 4.0) : lock.end;
        if (!player.level().noCollision(player, player.getBoundingBox().move(point.subtract(player.position())))) {
            LOCKS.remove(player.getUUID());
            return;
        }
        player.teleportTo(point.x, point.y, point.z);
        player.setDeltaMovement(Vec3.ZERO);
        player.hurtMarked = true;
        player.resetFallDistance();
        if (elapsed % 4 == 0) HeartOfLunaVfx.binding(player);
    }

    private record Lock(UUID boss, ServerPlayer player, Vec3 start, Vec3 end, long started) {}
    private HeartOfLunaMovementLock() {}
}
