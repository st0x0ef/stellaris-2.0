package org.exodusstudio.stellaris.registries;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Function;

import static org.exodusstudio.stellaris.Stellaris.*;

public final class ItemsRegistry extends ModRegistries {

    public static final ItemsRegistry itemsRegistry = new ItemsRegistry();
    public static final Registrar<Item> ITEMS = MANAGER.get().get(Registries.ITEM);



    public static <I extends Item> RegistrySupplier<I> item(String name, Item.Properties properties, Function<Item.Properties, I> itemFunc) {
        ResourceLocation id = id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return ITEMS.register(id, () -> itemFunc.apply(properties.setId(key)));
    }

    @Override
    public Registrar<?> getRegistrar() {
        return ITEMS;
    }

    @Override
    public ModRegistries getStaticInstance() {
        return itemsRegistry;
    }
}
