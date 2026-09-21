package org.exodusstudio.stellaris.common.compats.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.compats.jei.categories.BlenderCategory;
import org.exodusstudio.stellaris.common.compats.jei.categories.ElectrolyzerCategory;
import org.exodusstudio.stellaris.common.compats.jei.categories.FuelRefineryCategory;
import org.exodusstudio.stellaris.common.compats.jei.categories.RocketStationCategory;
import org.exodusstudio.stellaris.common.compats.jei.categories.SpaceStationCategory;
import org.exodusstudio.stellaris.common.compats.recipe_cache.BlenderRecipeCache;
import org.exodusstudio.stellaris.common.compats.recipe_cache.ElectrolyzerRecipeCache;
import org.exodusstudio.stellaris.common.compats.recipe_cache.FuelRefineryRecipeCache;
import org.exodusstudio.stellaris.common.compats.recipe_cache.RocketStationRecipeCache;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationData;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
                ElectrolyzerCategory.create(guiHelper),
                BlenderCategory.create(guiHelper),
                SpaceStationCategory.create(guiHelper)
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
            runtime.getRecipeManager().addRecipes(BlenderCategory.RECIPE, BlenderRecipeCache.get());
            runtime.getRecipeManager().addRecipes(SpaceStationCategory.RECIPE, List.copyOf(SpaceStationData.SPACE_STATION_RECIPES));
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Station recipes arrive from the server, so this seeds whatever is already known and
        // reloadRecipes() refreshes the list once the sync lands.
        registration.addRecipes(SpaceStationCategory.RECIPE, List.copyOf(SpaceStationData.SPACE_STATION_RECIPES));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addCraftingStation(RocketStationCategory.RECIPE, BlocksRegistry.ENGINEERING_STATION.item().get().getDefaultInstance());
        registry.addCraftingStation(FuelRefineryCategory.RECIPE, BlocksRegistry.FUEL_REFINERY.item().get().getDefaultInstance());
        registry.addCraftingStation(ElectrolyzerCategory.RECIPE, BlocksRegistry.ELECTROLYZER.item().get().getDefaultInstance());
        registry.addCraftingStation(BlenderCategory.RECIPE, BlocksRegistry.BLENDER.item().get().getDefaultInstance());
        registry.addCraftingStation(SpaceStationCategory.RECIPE, BlocksRegistry.ENGINEERING_STATION.item().get().getDefaultInstance());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        for (Class<? extends AbstractContainerScreen<?>> screenClass : ApplicationRegistry.applications_menus) {
            registration.addGenericGuiContainerHandler(screenClass, new IGuiContainerHandler<>() {
                @Override
                public List<Rect2i> getGuiExtraAreas(AbstractContainerScreen<?> containerScreen) {
                    return List.of(new Rect2i(0, 0, containerScreen.width, containerScreen.height));
                }
            });
        }
    }
}
