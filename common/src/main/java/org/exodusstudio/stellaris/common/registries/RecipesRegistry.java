package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.data.recipe.ElectrolyzeRecipe;

public class RecipesRegistry {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Stellaris.MOD_ID, Registries.RECIPE_TYPE);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Stellaris.MOD_ID, Registries.RECIPE_SERIALIZER);


    public static final RegistrySupplier<RecipeType<ElectrolyzeRecipe>> ELECTROLYZE_TYPE = RECIPE_TYPES.register("electrolyze",
            () -> new Type<>("electrolyze"));



    public static final RegistrySupplier<RecipeSerializer<ElectrolyzeRecipe>> ELECTROLYZE_SERIALIZER = RECIPE_SERIALIZERS.register(
            "electrolyze",
            ElectrolyzeRecipe.Serializer::new
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
