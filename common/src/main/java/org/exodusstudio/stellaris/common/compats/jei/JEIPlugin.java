package org.exodusstudio.stellaris.common.compats.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.compats.jei.categories.ElectrolyzerCategory;
import org.exodusstudio.stellaris.common.compats.jei.categories.FuelRefineryCategory;
import org.exodusstudio.stellaris.common.compats.jei.categories.RocketStationCategory;
import org.exodusstudio.stellaris.common.compats.jei.recipe_cache.ElectrolyzerRecipeCache;
import org.exodusstudio.stellaris.common.compats.jei.recipe_cache.FuelRefineryRecipeCache;
import org.exodusstudio.stellaris.common.compats.jei.recipe_cache.RocketStationRecipeCache;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    public static final Identifier ID = IdentifierUtils.id("jei");

    private static IJeiRuntime runtime;

    public JEIPlugin() {
    }

    @Override
    public @NotNull Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(
                RocketStationCategory.create(guiHelper),
                FuelRefineryCategory.create(guiHelper),
                ElectrolyzerCategory.create(guiHelper)
        );
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
    }

    public static void reloadRecipes() {
        if (runtime != null) {
            runtime.getRecipeManager().addRecipes(RocketStationCategory.RECIPE, RocketStationRecipeCache.get());
            runtime.getRecipeManager().addRecipes(FuelRefineryCategory.RECIPE, FuelRefineryRecipeCache.get());
            runtime.getRecipeManager().addRecipes(ElectrolyzerCategory.RECIPE, ElectrolyzerRecipeCache.get());
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addCraftingStation(RocketStationCategory.RECIPE, BlocksRegistry.ENGINEERING_STATION.item().get().getDefaultInstance());
        registry.addCraftingStation(FuelRefineryCategory.RECIPE, BlocksRegistry.FUEL_REFINERY.item().get().getDefaultInstance());
        registry.addCraftingStation(ElectrolyzerCategory.RECIPE, BlocksRegistry.ELECTROLYZER.item().get().getDefaultInstance());
    }
}
