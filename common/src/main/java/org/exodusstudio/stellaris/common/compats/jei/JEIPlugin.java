package org.exodusstudio.stellaris.common.compats.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.data.recipes.RocketStationRecipe;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    public static final Identifier ID = IdentifierUtils.id("jei");

    private static IJeiRuntime runtime;

    private final List<RocketStationRecipe> rocketStationRecipes = new ArrayList<>();

    public JEIPlugin () {
    }

    @Override
    public @NotNull Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(RocketStationCategory.create(registry.getJeiHelpers().getGuiHelper()));
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
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addCraftingStation(RocketStationCategory.RECIPE, BlocksRegistry.ENGINEERING_STATION.item().get().getDefaultInstance());
    }


}
