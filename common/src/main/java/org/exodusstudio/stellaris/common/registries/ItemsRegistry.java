package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.common.items.ParasiteItem;
import org.exodusstudio.stellaris.common.items.SDCardItem;
import org.exodusstudio.stellaris.common.items.CanItem;
import org.exodusstudio.stellaris.common.items.TabletItem;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static org.exodusstudio.stellaris.Stellaris.MOD_ID;


@SuppressWarnings("all")
public final class ItemsRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> TEST_ITEM = item("test_item");
    public static final RegistrySupplier<Item> DESH_INGOT = item("desh_ingot");
    public static final RegistrySupplier<Item> RAW_DESH = item("raw_desh");

    public static final RegistrySupplier<TabletItem> TABLET = item("tablet", TabletItem::new);
    public static final RegistrySupplier<SDCardItem> SD_CARD = item("sd_card", SDCardItem::new);

    /** Cans */
    // Small cans
    public static final RegistrySupplier<CanItem> BLACK_CAN = item("black_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> BLUE_CAN = item("blue_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> BROWN_CAN = item("brown_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> CYAN_CAN = item("cyan_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> GRAY_CAN = item("gray_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> GREEN_CAN = item("green_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> LIGHT_BLUE_CAN = item("light_blue_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> LIGHT_GRAY_CAN = item("light_gray_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> LIME_CAN = item("lime_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> MAGENTA_CAN = item("magenta_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> ORANGE_CAN = item("orange_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> PINK_CAN = item("pink_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> PURPLE_CAN = item("purple_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> RED_CAN = item("red_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> WHITE_CAN = item("white_can", (p) -> new CanItem(p, 10));
    public static final RegistrySupplier<CanItem> YELLOW_CAN = item("yellow_can", (p) -> new CanItem(p, 10));

    // Big cans
    public static final RegistrySupplier<CanItem> BIG_BLACK_CAN = item("big_black_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_BLUE_CAN = item("big_blue_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_BROWN_CAN = item("big_brown_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_CYAN_CAN = item("big_cyan_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_GRAY_CAN = item("big_gray_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_GREEN_CAN = item("big_green_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_LIGHT_BLUE_CAN = item("big_light_blue_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_LIGHT_GRAY_CAN = item("big_light_gray_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_LIME_CAN = item("big_lime_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_MAGENTA_CAN = item("big_magenta_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_ORANGE_CAN = item("big_orange_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_PINK_CAN = item("big_pink_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_PURPLE_CAN = item("big_purple_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_RED_CAN = item("big_red_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_WHITE_CAN = item("big_white_can", (p) -> new CanItem(p, 20));
    public static final RegistrySupplier<CanItem> BIG_YELLOW_CAN = item("big_yellow_can", (p) -> new CanItem(p, 20));

    // Moon lore items
    public static final RegistrySupplier<ParasiteItem> PARASITE = item("parasite", ParasiteItem::new);

    public static RegistrySupplier<Item> item(String name) {
        return item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_MAIN), Item::new);
    }

    public static <I extends Item> RegistrySupplier<I> item(String name, Function<Item.Properties, I> itemFunc) {
        return item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_MAIN), itemFunc);
    }

    public static <I extends Item> @NotNull RegistrySupplier<I> item(String name, Item.Properties properties, Function<Item.Properties, I> itemFunc) {
        ResourceLocation id = ResourceLocationUtils.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return ITEMS.register(id, () -> itemFunc.apply(properties.setId(key)));
    }
    private ItemsRegistry() {}
}