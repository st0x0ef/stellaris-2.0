package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.menu.MainTabletMenu;

public class MenuTypesRegistry {

    public static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(Stellaris.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<MainTabletMenu>> TABLET = MENU_TYPE.register("tablet", () -> MenuRegistry.ofExtended(MainTabletMenu::new));

}
