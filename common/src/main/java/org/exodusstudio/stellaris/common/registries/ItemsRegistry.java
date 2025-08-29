package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static org.exodusstudio.stellaris.Stellaris.MOD_ID;
import static org.exodusstudio.stellaris.Stellaris.id;

public final class ItemsRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> TEST_ITEM = item("test_item");
    public static final RegistrySupplier<Item> DESH_INGOT = item("desh_ingot");
    public static final RegistrySupplier<Item> RAW_DESH = item("raw_desh");


    public static RegistrySupplier<Item> item(String name) {
        return item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_MAIN), Item::new);
    }

    public static <I extends Item> RegistrySupplier<I> item(String name, Function<Item.Properties, I> itemFunc) {
        return item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_MAIN), itemFunc);
    }

    public static <I extends Item> @NotNull RegistrySupplier<I> item(String name, Item.Properties properties, Function<Item.Properties, I> itemFunc) {
        ResourceLocation id = id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return ITEMS.register(id, () -> itemFunc.apply(properties.setId(key)));
    }


    private ItemsRegistry() {}

}
