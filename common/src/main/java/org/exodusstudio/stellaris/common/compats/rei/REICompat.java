package org.exodusstudio.stellaris.common.compats.rei;

import net.minecraft.client.Minecraft;

import java.lang.reflect.Method;

public final class REICompat {

    private static final Method RELOAD_PLUGINS;
    private static final Method ARE_ANY_RELOADING;
    private static final Object RELOAD_STAGE_END;
    private static final boolean REI_AVAILABLE;

    private static volatile int synced;
    private static volatile int built;

    static {
        Method reload = null;
        Method reloading = null;
        Object end = null;

        try {
            Class<?> core = Class.forName("me.shedaniel.rei.RoughlyEnoughItemsCoreClient");
            Class<?> stage = Class.forName("me.shedaniel.rei.api.common.registry.ReloadStage");
            Class<?> plugins = Class.forName("me.shedaniel.rei.api.common.plugins.PluginManager");
            Class<?> throttle = Class.forName("org.apache.commons.lang3.mutable.MutableLong");

            reload = core.getMethod("reloadPlugins", throttle, stage);
            reloading = plugins.getMethod("areAnyReloading");
            end = Enum.valueOf(stage.asSubclass(Enum.class), "END");
        } catch (Throwable ignored) {

        }

        RELOAD_PLUGINS = reload;
        ARE_ANY_RELOADING = reloading;
        RELOAD_STAGE_END = end;
        REI_AVAILABLE = reload != null && reloading != null && end != null;
    }

    private REICompat() {
    }

    public static void reloadDisplaysSafe() {
        if (!REI_AVAILABLE) return;

        synced++;
    }

    public static int syncGeneration() {
        return synced;
    }

    public static void markDisplaysBuilt(int generation) {
        built = generation;
    }

    public static void clientTick(Minecraft minecraft) {
        if (!REI_AVAILABLE || built == synced || minecraft.level == null || isReloading()) {
            return;
        }

        int generation = synced;
        reloadDisplays();
        markDisplaysBuilt(generation);
    }

    private static boolean isReloading() {
        try {
            return (boolean) ARE_ANY_RELOADING.invoke(null);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static void reloadDisplays() {
        try {
            RELOAD_PLUGINS.invoke(null, null, RELOAD_STAGE_END);
        } catch (Throwable ignored) {

        }
    }
}
