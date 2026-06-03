package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.data.recipes.ElectrolyzeRecipe;
import org.exodusstudio.stellaris.common.data.recipes.FuelRefineryRecipe;
import org.exodusstudio.stellaris.common.data.recipes.RocketStationRecipe;
import org.exodusstudio.stellaris.common.data.recipes.VaccineRecipe;

public class RecipesRegistry {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Stellaris.MOD_ID, Registries.RECIPE_TYPE);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Stellaris.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final RegistrySupplier<RecipeType<ElectrolyzeRecipe>> ELECTROLYZE_RECIPE_TYPE = RECIPE_TYPES.register("electrolyze", () -> new Type<>("electrolyze"));
    public static final RegistrySupplier<RecipeSerializer<ElectrolyzeRecipe>> ELECTROLYZE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("electrolyze", ElectrolyzeRecipe.Serializer::create);

    public static final RegistrySupplier<RecipeType<RocketStationRecipe>> ROCKET_STATION_TYPE = RECIPE_TYPES.register("rocket_station", () -> new Type<>("rocket_station"));
    public static final RegistrySupplier<RecipeSerializer<RocketStationRecipe>> ROCKET_STATION_SERIALIZER = RECIPE_SERIALIZERS.register("rocket_station", RocketStationRecipe.Serializer::create);

    public static final RegistrySupplier<RecipeType<VaccineRecipe>> VACCINE_TYPE = RECIPE_TYPES.register("vaccine", () -> new Type<>("vaccine"));
    public static final RegistrySupplier<RecipeSerializer<VaccineRecipe>> VACCINE_SERIALIZER = RECIPE_SERIALIZERS.register("vaccine", VaccineRecipe.Serializer::create);


    public static final RegistrySupplier<RecipeType<FuelRefineryRecipe>> FUEL_REFINERY_TYPE = RECIPE_TYPES.register("fuel_refinery",
            () -> new Type<>("fuel_refinery"));
    public static final RegistrySupplier<RecipeSerializer<FuelRefineryRecipe>> FUEL_REFINERY_SERIALIZER = RECIPE_SERIALIZERS.register(
            "fuel_refinery",
            FuelRefineryRecipe.Serializer::create
    );

    public static void register() {
        RECIPE_TYPES.register();
        RECIPE_SERIALIZERS.register();
    }

    public record Type<T extends Recipe<?>>(String id) implements RecipeType<T> {

        @Override
        public String toString() {
            return Stellaris.MOD_ID + ":" + id;
        }
    }

}
