package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;

import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaBossEntity;

import java.util.Map;
import java.util.WeakHashMap;

public final class HeartOfLunaCloudSurface {
    private static final Vec3[][] EMPTY = new Vec3[0][];
    private static final Map<HeartOfLunaBossEntity, Surface> CACHE = new WeakHashMap<>();

    public static Vec3[][] sample(HeartOfLunaBossEntity boss) {
        if (boss.cloudTicks() <= 0) { CACHE.remove(boss); return EMPTY; }
        Vec3 center = boss.cloudCenter();
        Surface previous = CACHE.get(boss);
        if (previous != null && previous.center.equals(center) && boss.tickCount - previous.tick < 40) return previous.rings;
        Vec3[][] rings = new Vec3[3][97];
        double[] radii = {6.35, 6.8, HeartOfLunaBossEntity.CLOUD_RADIUS};
        for (int ring = 0; ring < rings.length; ring++) {
            for (int i = 0; i < 96; i++) {
                double angle = i * Math.PI * 2 / 96;
                Vec3 p = center.add(Math.cos(angle) * radii[ring], 0, Math.sin(angle) * radii[ring]);
                var hit = boss.level().clip(new ClipContext(p.add(0, 2, 0), p.add(0, -4, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, boss));
                if (hit.getType() == HitResult.Type.BLOCK) rings[ring][i] = hit.getLocation().add(0, 0.035 + ring * 0.008, 0);
            }
            rings[ring][96] = rings[ring][0];
        }
        CACHE.put(boss, new Surface(center, boss.tickCount, rings));
        return rings;
    }

    private record Surface(Vec3 center, int tick, Vec3[][] rings) {}
    private HeartOfLunaCloudSurface() {}
}
