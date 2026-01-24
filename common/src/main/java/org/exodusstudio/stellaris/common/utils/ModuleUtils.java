package org.exodusstudio.stellaris.common.utils;

import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModules;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModules;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;

public class ModuleUtils {

    public static Modules<SpaceSuitModule> getSpaceSuitModules(ItemStack stack) {
        return stack.getOrDefault(DataComponentsRegistry.SPACE_SUIT_MODULES.get(), SpaceSuitModules.empty());
    }

    public static boolean hasSpaceSuitModule(ItemStack stack, Class<? extends SpaceSuitModule> module) {
        Modules<SpaceSuitModule> modules = getSpaceSuitModules(stack);
        for (SpaceSuitModule mod : modules) {
            if (module.isInstance(mod)) {
                return true;
            }
        }
        return false;
    }

    public static <T extends SpaceSuitModule> T getSpaceSuitModule(ItemStack stack, Class<T> moduleClass) {
        Modules<SpaceSuitModule> modules = getSpaceSuitModules(stack);
        for (SpaceSuitModule mod : modules) {
            if (moduleClass.isInstance(mod)) {
                return moduleClass.cast(mod);
            }
        }
        return null;
    }

    public static Modules<RocketModule> getRocketModules(ItemStack stack) {
        return stack.getOrDefault(DataComponentsRegistry.ROCKET_MODULES.get(), RocketModules.empty());
    }
}
