package org.exodusstudio.stellaris.common.utils;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.timeline.Timeline;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public final class DayCycleUtils {

    private static final Identifier DAY_MARKER = Identifier.withDefaultNamespace("day");
    private static final Identifier NIGHT_MARKER = Identifier.withDefaultNamespace("night");

    private static final Map<Timeline, Optional<DayCycle>> CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    private DayCycleUtils() {
    }

    public static boolean isDay(Level level) {
        DayCycle cycle = dayCycleOf(level);
        return cycle == null ? level.isBrightOutside() : cycle.isDay(level);
    }

    public static boolean isNight(Level level) {
        DayCycle cycle = dayCycleOf(level);
        return cycle == null ? level.isDarkOutside() : !cycle.isDay(level);
    }

    private static @Nullable DayCycle dayCycleOf(Level level) {
        for (Holder<Timeline> holder : level.dimensionType().timelines()) {
            Timeline timeline = holder.value();
            Optional<DayCycle> cycle = CACHE.computeIfAbsent(timeline, DayCycleUtils::readDayCycle);

            if (cycle.isPresent()) {
                return cycle.get();
            }
        }
        return null;
    }

    private static Optional<DayCycle> readDayCycle(Timeline timeline) {
        if (timeline.periodTicks().isEmpty()) {
            return Optional.empty();
        }

        Map<Identifier, Integer> markers = new HashMap<>();
        timeline.registerTimeMarkers((key, marker) -> markers.put(key.identifier(), marker.ticks()));

        Integer dayStart = markers.get(DAY_MARKER);
        Integer nightStart = markers.get(NIGHT_MARKER);
        if (dayStart == null || nightStart == null) {
            return Optional.empty();
        }

        return Optional.of(new DayCycle(timeline, dayStart, nightStart));
    }

    /**
     * The tick at which day starts and the tick at which night starts, on a given timeline.
     */
    private record DayCycle(Timeline timeline, int dayStart, int nightStart) {

        boolean isDay(Level level) {
            long ticks = timeline.getCurrentTicks(level.clockManager());
            return dayStart <= nightStart
                    ? ticks >= dayStart && ticks < nightStart
                    : ticks >= dayStart || ticks < nightStart;
        }
    }
}
