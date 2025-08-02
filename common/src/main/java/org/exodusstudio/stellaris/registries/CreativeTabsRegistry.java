package org.exodusstudio.stellaris.registries;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.exodusstudio.stellaris.utils.ResourceLocationUtils;

import static org.exodusstudio.stellaris.Stellaris.MANAGER;

public final class CreativeTabsRegistry extends ModRegistries {
    public static final CreativeTabsRegistry creativeTabsRegistry = new CreativeTabsRegistry();
    public static final Registrar<CreativeModeTab> CREATIVE_MODE_TABS = MANAGER.get().get(Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> STELLARIS_TAB = creativeTabs(ResourceLocationUtils.id("stellaris_tab"),
            Component.translatable("itemGroup.stellaris.tab"), new ItemStack(Items.STICK)); // TODO : find a way to use our items here (currently crash)

    public static RegistrySupplier<CreativeModeTab> creativeTabs(ResourceLocation location, Component translatable, ItemStack icon) {
        return CREATIVE_MODE_TABS.register(location, () -> CreativeTabRegistry.create(translatable, () -> icon));
    }

    @Override
    public Registrar<?> getRegistrar() {
        return CREATIVE_MODE_TABS;
    }

    @Override
    public ModRegistries getStaticInstance() {
        return creativeTabsRegistry;
    }
}
