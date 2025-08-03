package org.exodusstudio.stellaris.registries;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static org.exodusstudio.stellaris.Stellaris.*;

public class CreativeTabsRegistry {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> STELLARIS_MAIN = create("stellaris", ItemsRegistry.DESH_INGOT);
    public static final RegistrySupplier<CreativeModeTab> STELLARIS_BLOCKS = create("stellaris_blocks", BlocksRegistry.MOON_ROCK.item);

    @SuppressWarnings("all")
    public static RegistrySupplier<CreativeModeTab> create(String name, Holder<? extends Item> icon) {

        return CREATIVE_MODE_TABS.register(id(name),
                () -> CreativeTabRegistry.create(
                        Component.translatable("itemGroup.stellaris." + name),
                        () -> new ItemStack((Holder<Item>) icon)
                ));
    }

    private CreativeTabsRegistry() {}

}
