package org.exodusstudio.stellaris.common.registries;

import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.equipment.ArmorMaterials;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.components.RoverComponent;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;
import org.exodusstudio.stellaris.common.items.*;
import org.exodusstudio.stellaris.common.items.infection.ParasiteItem;
import org.exodusstudio.stellaris.common.items.infection.PathogenStorageCellItem;
import org.exodusstudio.stellaris.common.items.infection.VaccineItem;
import org.exodusstudio.stellaris.common.items.modules.rocket.*;
import org.exodusstudio.stellaris.common.items.modules.space_suit.*;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitBoots;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitChestplate;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitHelmet;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitLeggings;
import org.exodusstudio.stellaris.common.items.tools.*;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.vehicle_upgrade.MotorUpgrade;
import org.exodusstudio.stellaris.common.vehicle_upgrade.TankUpgrade;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static org.exodusstudio.stellaris.Stellaris.MOD_ID;


@SuppressWarnings("all")
public final class ItemsRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);

    /** Basic Materials */
    public static final RegistrySupplier<Item> TITANIUM_INGOT = item("titanium_ingot");
    public static final RegistrySupplier<Item> TITANIUM_NUGGET = item("titanium_nugget");
    public static final RegistrySupplier<Item> RAW_TITANIUM = item("raw_titanium");

    public static final RegistrySupplier<Item> SILICON = item("silicon");

    public static final RegistrySupplier<Item> DESH_INGOT = item("desh_ingot");
    public static final RegistrySupplier<Item> RAW_DESH = item("raw_desh");

    /** Tools */
    public static final RegistrySupplier<Item> TITANIUM_PICKAXE = item("titanium_pickaxe", p -> new CustomPickaxeItem(p, ToolMaterialsRegistry.TITANIUM, 1.0F, -2.8F));
    public static final RegistrySupplier<Item> TITANIUM_AXE = item("titanium_axe", p -> new CustomAxeItem(p, ToolMaterialsRegistry.TITANIUM, 6.0F, -3.1F));
    public static final RegistrySupplier<Item> TITANIUM_SHOVEL = item("titanium_shovel", p -> new CustomShovelItem(p, ToolMaterialsRegistry.TITANIUM, 1.5F, -3.0F));
    public static final RegistrySupplier<Item> TITANIUM_SWORD = item("titanium_sword", p -> new CustomSwordItem(p, ToolMaterialsRegistry.TITANIUM, 3.0F, -2.4F));
    public static final RegistrySupplier<Item> TITANIUM_HOE = item("titanium_hoe", p -> new CustomHoeItem(p, ToolMaterialsRegistry.TITANIUM, -3.0F, 0.0F));


    /** Special Items */
    public static final RegistrySupplier<TabletItem> TABLET = item("tablet", TabletItem::new);
    public static final RegistrySupplier<Item> OIL_FINDER = item("oil_finder", OilFinderItem::new);
    public static final RegistrySupplier<SDCardItem> SD_CARD = item("sd_card", (p) -> new SDCardItem(p, "stellaris:creative"));

    /** Rover */
    public static final RegistrySupplier<RoverItem> ROVER = item("rover", p -> new RoverItem(p.component(DataComponentsRegistry.ROVER_COMPONENT.get(), new RoverComponent(MotorUpgrade.getBasic(false).getFuelType().getSerializedName(), 0, GUISprites.FUEL_OVERLAY, TankUpgrade.getBasic().getTankCapacity(), 1))));


    /** Rocket */
    public static final RegistrySupplier<RocketItem> ROCKET = item("rocket", new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_MAIN).stacksTo(1), RocketItem::new);

    // Modules
    public static final RegistrySupplier<ShieldModule> SHIELD_MODULE = item("shield_module", ShieldModule::new);
    public static final RegistrySupplier<HydrogenFuelModuleItem> HYDROGEN_MOTOR = item("hydrogen_motor", HydrogenFuelModuleItem::new);
    public static final RegistrySupplier<AutopilotModuleItem> AUTOPILOT_MODULE = item("autopilot_module", AutopilotModuleItem::new);
    public static final RegistrySupplier<CargoModuleItem> CARGO_MODULE = item("cargo_module", CargoModuleItem::new);

    // Skins
    public static final RegistrySupplier<RocketSkinModuleItem> GALAXY_SKIN = item("galaxy_skin", p -> new RocketSkinModuleItem(p, "galaxy"));
    public static final RegistrySupplier<RocketSkinModuleItem> FROST_SKIN = item("frost_skin", p -> new RocketSkinModuleItem(p, "frost"));
    public static final RegistrySupplier<RocketSkinModuleItem> MILITARY_SKIN = item("military_skin", p -> new RocketSkinModuleItem(p, "military"));

    // Models
    public static final RegistrySupplier<RocketModelModuleItem<?>> TINY_MODEL = item("tiny_model", p -> new RocketModelModuleItem<>(p, "tiny", "tiny", 3.75f));
    public static final RegistrySupplier<RocketModelModuleItem<?>> SMALL_MODEL = item("small_model", p -> new RocketModelModuleItem<>(p, "small", "small", 4.75f));
    public static final RegistrySupplier<RocketModelModuleItem<?>> BIG_MODEL = item("big_model", p -> new RocketModelModuleItem<>(p, "big", "big", 4.25f));

    /** Space Suit Items */
    public static final RegistrySupplier<Item> SPACE_SUIT_HELMET = item("space_suit_helmet",  SpaceSuitHelmet::new);
    public static final RegistrySupplier<Item> SPACE_SUIT_CHESTPLATE = item("space_suit_chestplate", SpaceSuitChestplate::new);
    public static final RegistrySupplier<Item> SPACE_SUIT_LEGGINGS = item("space_suit_leggings", SpaceSuitLeggings::new);
    public static final RegistrySupplier<Item> SPACE_SUIT_BOOTS = item("space_suit_boots", SpaceSuitBoots::new);

    // Space Suit Modules
    // Oxygen Modules
    public static final RegistrySupplier<OxygenModuleItem> SPACE_SUIT_OXYGEN_MODULE_T1 = item("space_suit_oxygen_module_tier_1", p -> new OxygenModuleItem(p, 1200));
    public static final RegistrySupplier<OxygenModuleItem> SPACE_SUIT_OXYGEN_MODULE_T2 = item("space_suit_oxygen_module_tier_2", p -> new OxygenModuleItem(p, 3600));
    public static final RegistrySupplier<OxygenModuleItem> SPACE_SUIT_OXYGEN_MODULE_T3 = item("space_suit_oxygen_module_tier_3", p -> new OxygenModuleItem(p, 14400));

    // Oil Finder Modules
    public static final RegistrySupplier<OilFinderModuleItem> SPACE_SUIT_OIL_FINDER_MODULE_T1 = item("space_suit_oil_finder_module_tier_1", p -> new OilFinderModuleItem(p, 5));
    public static final RegistrySupplier<OilFinderModuleItem> SPACE_SUIT_OIL_FINDER_MODULE_T2 = item("space_suit_oil_finder_module_tier_2", p -> new OilFinderModuleItem(p, 3));
    public static final RegistrySupplier<OilFinderModuleItem> SPACE_SUIT_OIL_FINDER_MODULE_T3 = item("space_suit_oil_finder_module_tier_3", p -> new OilFinderModuleItem(p, 1));


    // Diesel Tank Modules
    public static final RegistrySupplier<TankModuleItem> SPACE_SUIT_DIESEL_TANK_MODULE_T1 = item("space_suit_diesel_tank_module_tier_1", p -> new DieselTankModuleItem(p, 1000));
    public static final RegistrySupplier<TankModuleItem> SPACE_SUIT_DIESEL_TANK_MODULE_T2 = item("space_suit_diesel_tank_module_tier_2", p -> new DieselTankModuleItem(p, 2000));
    public static final RegistrySupplier<TankModuleItem> SPACE_SUIT_DIESEL_TANK_MODULE_T3 = item("space_suit_diesel_tank_module_tier_3", p -> new DieselTankModuleItem(p, 3000));

    // Hydrogen Tank Modules
    public static final RegistrySupplier<TankModuleItem> SPACE_SUIT_HYDROGEN_TANK_MODULE_T1 = item("space_suit_hydrogen_tank_module_tier_1", p -> new HydrogenTankModuleItem(p, 1000));
    public static final RegistrySupplier<TankModuleItem> SPACE_SUIT_HYDROGEN_TANK_MODULE_T2 = item("space_suit_hydrogen_tank_module_tier_2", p -> new HydrogenTankModuleItem(p, 2000));
    public static final RegistrySupplier<TankModuleItem> SPACE_SUIT_HYDROGEN_TANK_MODULE_T3 = item("space_suit_hydrogen_tank_module_tier_3", p -> new HydrogenTankModuleItem(p, 3000));

    // Jet Modules
    public static final RegistrySupplier<JetModuleItem> SPACE_SUIT_JET_MODULE_T1 = item("space_suit_jet_module_tier_1", p -> new JetModuleItem(p, 10));
    public static final RegistrySupplier<JetModuleItem> SPACE_SUIT_JET_MODULE_T2 = item("space_suit_jet_module_tier_2", p -> new JetModuleItem(p, 6));
    public static final RegistrySupplier<JetModuleItem> SPACE_SUIT_JET_MODULE_T3 = item("space_suit_jet_module_tier_3", p -> new JetModuleItem(p, 2));

    // Damage Protection Modules
    public static final RegistrySupplier<DamageProtectionModuleItem> SPACE_SUIT_DAMAGE_PROTECTION_MODULE_T1 = item("space_suit_damage_protection_module_tier_1", p -> new DamageProtectionModuleItem(p, ArmorMaterials.IRON, "iron"));
    public static final RegistrySupplier<DamageProtectionModuleItem> SPACE_SUIT_DAMAGE_PROTECTION_MODULE_T2 = item("space_suit_damage_protection_module_tier_2", p -> new DamageProtectionModuleItem(p, ArmorMaterials.DIAMOND, "diamond"));
    public static final RegistrySupplier<DamageProtectionModuleItem> SPACE_SUIT_DAMAGE_PROTECTION_MODULE_T3 = item("space_suit_damage_protection_module_tier_3", p -> new DamageProtectionModuleItem(p, ArmorMaterials.NETHERITE, "netherite"));

    public static final RegistrySupplier<FriendsList> FRIENDS_LIST = item("friends_list", FriendsList::new);
    public static final RegistrySupplier<TooltipItem> SPACE_STATION_BLUEPRINT = item("space_station_blueprint", (p) -> new TooltipItem(p).addTooltip(SpaceStationRecipe::getComponent));


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

    // Foods
    public static final RegistrySupplier<Item> MOON_BERRIES = item("moon_berries", (p) -> new Item(p.food(Foods.GLOW_BERRIES).useItemDescriptionPrefix()));

    // Fluids
    public static final RegistrySupplier<ArchitecturyBucketItem> HYDROGEN_BUCKET = item("hydrogen_bucket", properties -> new ArchitecturyBucketItem(FluidsRegistry.HYDROGEN_STILL, properties));
    public static final RegistrySupplier<ArchitecturyBucketItem> OIL_BUCKET = item("oil_bucket", properties -> new ArchitecturyBucketItem(FluidsRegistry.OIL_STILL, properties));
    public static final RegistrySupplier<ArchitecturyBucketItem> FUEL_BUCKET = item("fuel_bucket", properties -> new ArchitecturyBucketItem(FluidsRegistry.FUEL_STILL, properties));
    public static final RegistrySupplier<ArchitecturyBucketItem> DIESEL_BUCKET = item("diesel_bucket", properties -> new ArchitecturyBucketItem(FluidsRegistry.DIESEL_STILL, properties));
    public static final RegistrySupplier<ArchitecturyBucketItem> BLUE_LIQUID_BUCKET = item("blue_liquid_bucket", properties -> new ArchitecturyBucketItem(FluidsRegistry.BLUE_LIQUID_STILL, properties));
    public static final RegistrySupplier<ArchitecturyBucketItem> ASTRUM_LIQUIDUS_BUCKET = item("astrum_liquidus_bucket", properties -> new ArchitecturyBucketItem(FluidsRegistry.ASTRUM_LIQUIDUS_STILL, properties));
    public static final RegistrySupplier<Item> FLUID_CELL = item("fluid_cell", properties -> new FluidCellItem(properties, 3000));

    // Crafting items
    public static final RegistrySupplier<Item> ENGINE_FAN = item("engine_fan");
    public static final RegistrySupplier<Item> ROCKET_ENGINE = item("rocket_engine");
    public static final RegistrySupplier<Item> ROCKET_FIN = item("rocket_fin");
    public static final RegistrySupplier<Item> ROCKET_NOSE_CONE = item("rocket_nose_cone");

    // Moon lore items
    public static final RegistrySupplier<ParasiteItem> PARASITE = item("parasite", ParasiteItem::new);
    public static final RegistrySupplier<Item> PATHOGEN_STORAGE_CELL = item("pathogen_storage_cell", PathogenStorageCellItem::new);
    public static final RegistrySupplier<Item> VACCINE = item("vaccine", VaccineItem::new);

    /** Mobs */
    public static final RegistrySupplier<SpawnEggItem> BLUE_FISH_SPAWN_EGG = item("blue_fish_spawn_egg", p -> new SpawnEggItem(p.spawnEgg(EntityTypesRegistry.BLUE_FISH.get())));
    public static final RegistrySupplier<SpawnEggItem> LUNAR_PARASITE_SPAWN_EGG = item("lunar_parasite_spawn_egg", p -> new SpawnEggItem(p.spawnEgg(EntityTypesRegistry.LUNAR_PARASITE.get())));
    public static final RegistrySupplier<SpawnEggItem> PARASITE_AFFECTED_VILLAGER_SPAWN_EGG = item("parasite_affected_villager_spawn_egg", p -> new SpawnEggItem(p.spawnEgg(EntityTypesRegistry.PARASITE_AFFECTED_VILLAGER.get())));
    public static final RegistrySupplier<SpawnEggItem> PARASITE_AFFECTED_VILLAGER_EVOLVED_SPAWN_EGG = item("parasite_affected_villager_evolved_spawn_egg", p -> new SpawnEggItem(p.spawnEgg(EntityTypesRegistry.PARASITE_AFFECTED_VILLAGER_EVOLVED.get())));
    public static final RegistrySupplier<SpawnEggItem> LUNA_SHADOW_SPAWN_EGG = item("luna_shadow_spawn_egg", p -> new SpawnEggItem(p.spawnEgg(EntityTypesRegistry.LUNA_SHADOW.get())));

    /** Lunar Forest Items */
    public static final RegistrySupplier<Item> LUNAR_BOAT = item("lunar_boat", p -> new BoatItem((EntityType<? extends AbstractBoat>) EntityTypesRegistry.LUNAR_BOAT.get(), p));
    public static final RegistrySupplier<Item> LUNAR_CHEST_BOAT = item("lunar_chest_boat", p -> new BoatItem((EntityType<? extends AbstractBoat>) EntityTypesRegistry.LUNAR_CHEST_BOAT.get(), p));

    public static final RegistrySupplier<Item> LUNAR_SIGN = ItemsRegistry.item("lunar_sign", p -> new SignItem(BlocksRegistry.LUNAR_SIGN.get(), BlocksRegistry.LUNAR_WALL_SIGN.get(), p));
    public static final RegistrySupplier<Item> LUNAR_HANGING_SIGN = ItemsRegistry.item("lunar_hanging_sign", p -> new HangingSignItem(BlocksRegistry.LUNAR_HANGING_SIGN.get(), BlocksRegistry.LUNAR_WALL_HANGING_SIGN.get(), p));

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
}

