package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.function.Supplier;

import static org.exodusstudio.stellaris.Stellaris.MOD_ID;

public class CreativeTabsRegistry {


    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final DeferredSupplier<CreativeModeTab> STELLARIS_MAIN = create("stellaris");
    public static final DeferredSupplier<CreativeModeTab> STELLARIS_BLOCKS = create("stellaris_blocks");

    @SuppressWarnings("all")
    public static DeferredSupplier<CreativeModeTab> create(String name) {
        return CreativeTabRegistry.defer(IdentifierUtils.id(name));
    }

    public static void register() {
        registerTab("stellaris", ItemsRegistry.TABLET);
        registerTab("stellaris_blocks", BlocksRegistry.MOON_STONE.item());

        CREATIVE_MODE_TABS.register();
    }

    public static void registerTab(String name, Supplier<? extends ItemLike> icon) {
        CREATIVE_MODE_TABS.register(name,
                () -> CreativeTabRegistry.create(
                        Component.translatable("itemGroup.stellaris." + name),
                        () -> new ItemStack(icon.get())
                ));
    }

    private CreativeTabsRegistry() {}

}
