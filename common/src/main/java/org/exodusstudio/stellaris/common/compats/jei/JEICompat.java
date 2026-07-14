package org.exodusstudio.stellaris.common.compats.jei;

public final class JEICompat {

    private static final boolean JEI_AVAILABLE;

    static {
        boolean present;
        try {
            Class.forName("mezz.jei.api.IModPlugin");
            present = true;
        } catch (ClassNotFoundException e) {
            present = false;
        }
        JEI_AVAILABLE = present;
    }

    private JEICompat() {}

    /**
     * Safely invoke the static JEI plugin reloadRecipes method if JEI is present.
     * Any exceptions are swallowed to avoid breaking gameplay when JEI is absent.
     */
    public static void reloadRecipesSafe() {
        if (!JEI_AVAILABLE) return;
        try {
            Class<?> plugin = Class.forName("org.exodusstudio.stellaris.common.compats.jei.JEIPlugin");
            java.lang.reflect.Method m = plugin.getMethod("reloadRecipes");
            m.invoke(null);
        } catch (Throwable ignored) {

        }
    }
}

