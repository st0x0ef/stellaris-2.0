package org.exodusstudio.stellaris.common.registries;

import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.common.items.RocketItem;
import org.exodusstudio.stellaris.common.items.SDCardItem;
import org.exodusstudio.stellaris.common.items.CanItem;
import org.exodusstudio.stellaris.common.items.TabletItem;
import org.exodusstudio.stellaris.common.items.infection.ParasiteItem;
import org.exodusstudio.stellaris.common.items.infection.PathogenStorageCellItem;
import org.exodusstudio.stellaris.common.items.modules.GalaxySkinModule;
import org.exodusstudio.stellaris.common.items.modules.HydrogenFuelModuleItem;
import org.exodusstudio.stellaris.common.items.modules.ShieldModule;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static org.exodusstudio.stellaris.Stellaris.MOD_ID;


@SuppressWarnings("all")
public final class ItemsRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);

    /** Basic Materials */
    public static final RegistrySupplier<Item> TITANIUM_INGOT = item("titanium_ingot");
    public static final RegistrySupplier<Item> RAW_TITANIUM = item("raw_titanium");

    public static final RegistrySupplier<Item> SILICON = item("silicon");

    public static final RegistrySupplier<Item> DESH_INGOT = item("desh_ingot");
    public static final RegistrySupplier<Item> RAW_DESH = item("raw_desh");

    /** Tools */
    public static final RegistrySupplier<Item> TITANIUM_PICKAXE = item("titanium_pickaxe", new Item.Properties().pickaxe(ToolMaterialsRegistry.TITANIUM, 1.0F, -2.8F).arch$tab(CreativeTabsRegistry.STELLARIS_MAIN));
    public static final RegistrySupplier<Item> TITANIUM_AXE = item("titanium_axe", new Item.Properties().axe(ToolMaterialsRegistry.TITANIUM, 6.0F, -3.1F).arch$tab(CreativeTabsRegistry.STELLARIS_MAIN));
    public static final RegistrySupplier<Item> TITANIUM_SHOVEL = item("titanium_shovel", new Item.Properties().shovel(ToolMaterialsRegistry.TITANIUM, 1.5F, -3.0F).arch$tab(CreativeTabsRegistry.STELLARIS_MAIN));
    public static final RegistrySupplier<Item> TITANIUM_SWORD = item("titanium_sword", new Item.Properties().sword(ToolMaterialsRegistry.TITANIUM, 3, -2.4F).arch$tab(CreativeTabsRegistry.STELLARIS_MAIN));
    public static final RegistrySupplier<Item> TITANIUM_HOE = item("titanium_hoe", new Item.Properties().hoe(ToolMaterialsRegistry.TITANIUM, -3.0F, 0.0F).arch$tab(CreativeTabsRegistry.STELLARIS_MAIN));


    /** Special Items */
    public static final RegistrySupplier<TabletItem> TABLET = item("tablet", TabletItem::new);
    public static final RegistrySupplier<ShieldModule> SHIELD_MODULE = item("shield_module", ShieldModule::new);
    public static final RegistrySupplier<GalaxySkinModule> GALAXY_SKIN = item("galaxy_skin", GalaxySkinModule::new);
    public static final RegistrySupplier<HydrogenFuelModuleItem> HYDROGEN_MOTOR = item("hydrogen_motor", HydrogenFuelModuleItem::new);

    public static final RegistrySupplier<SDCardItem> SD_CARD = item("sd_card", SDCardItem::new);
    public static final RegistrySupplier<RocketItem> ROCKET = item("rocket", new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_MAIN).stacksTo(1), RocketItem::new);

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

    public static final RegistrySupplier<ArchitecturyBucketItem> HYDROGEN_BUCKET = item("hydrogen_bucket", (properties -> new ArchitecturyBucketItem(FluidsRegistry.HYDROGEN_STILL, properties)));
    public static final RegistrySupplier<ArchitecturyBucketItem> FUEL_BUCKET = item("fuel_bucket", (properties -> new ArchitecturyBucketItem(FluidsRegistry.FUEL_STILL, properties)));

    // Moon lore items
    public static final RegistrySupplier<ParasiteItem> PARASITE = item("parasite", ParasiteItem::new);
    public static final RegistrySupplier<Item> PATHOGEN_STORAGE_CELL = item("pathogen_storage_cell", PathogenStorageCellItem::new);


    public static RegistrySupplier<Item> item(String name) {
        return item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_MAIN));
    }

    public static <I extends Item> RegistrySupplier<I> item(String name, Function<Item.Properties, I> itemFunc) {
        return item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_MAIN), itemFunc);
    }

    public static @NotNull RegistrySupplier<Item> item(String name, Item.Properties properties) {
        Identifier id = IdentifierUtils.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return ITEMS.register(id, () -> new Item(properties.setId(key)));
    }

    public static <I extends Item> @NotNull RegistrySupplier<I> item(String name, Item.Properties properties, Function<Item.Properties, I> itemFunc) {
        Identifier id = IdentifierUtils.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return ITEMS.register(id, () -> itemFunc.apply(properties.setId(key)));
    }
    private ItemsRegistry() {}
}