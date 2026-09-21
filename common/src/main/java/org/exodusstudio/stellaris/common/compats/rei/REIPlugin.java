package org.exodusstudio.stellaris.common.compats.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRendererRegistry;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.compats.recipe_cache.BlenderRecipeCache;
import org.exodusstudio.stellaris.common.compats.recipe_cache.ElectrolyzerRecipeCache;
import org.exodusstudio.stellaris.common.compats.recipe_cache.FuelRefineryRecipeCache;
import org.exodusstudio.stellaris.common.compats.recipe_cache.RocketStationRecipeCache;
import org.exodusstudio.stellaris.common.compats.rei.categories.BlenderCategory;
import org.exodusstudio.stellaris.common.compats.rei.categories.ElectrolyzerCategory;
import org.exodusstudio.stellaris.common.compats.rei.categories.FuelRefineryCategory;
import org.exodusstudio.stellaris.common.compats.rei.categories.RocketStationCategory;
import org.exodusstudio.stellaris.common.compats.rei.categories.SpaceStationCategory;
import org.exodusstudio.stellaris.common.compats.rei.displays.BlendingDisplay;
import org.exodusstudio.stellaris.common.compats.rei.displays.ElectrolyzeDisplay;
import org.exodusstudio.stellaris.common.compats.rei.displays.FuelRefineryDisplay;
import org.exodusstudio.stellaris.common.compats.rei.displays.RocketStationDisplay;
import org.exodusstudio.stellaris.common.compats.rei.displays.SpaceStationDisplay;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationData;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;

import java.util.Collections;

public class REIPlugin implements REIClientPlugin {

    @Override
    public void registerEntryRenderers(EntryRendererRegistry registry) {
        registry.register(VanillaEntryTypes.FLUID, (entry, last) -> new FluidEntryRenderer(last));
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new RocketStationCategory());
        registry.add(new FuelRefineryCategory());
        registry.add(new ElectrolyzerCategory());
        registry.add(new BlenderCategory());
        registry.add(new SpaceStationCategory());

        registry.addWorkstations(RocketStationCategory.ID, EntryStacks.of(BlocksRegistry.ENGINEERING_STATION.item().get()));
        registry.addWorkstations(FuelRefineryCategory.ID, EntryStacks.of(BlocksRegistry.FUEL_REFINERY.item().get()));
        registry.addWorkstations(ElectrolyzerCategory.ID, EntryStacks.of(BlocksRegistry.ELECTROLYZER.item().get()));
        registry.addWorkstations(BlenderCategory.ID, EntryStacks.of(BlocksRegistry.BLENDER.item().get()));
        registry.addWorkstations(SpaceStationCategory.ID, EntryStacks.of(BlocksRegistry.ENGINEERING_STATION.item().get()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        int generation = REICompat.syncGeneration();

        RocketStationRecipeCache.get().forEach(recipe -> registry.add(new RocketStationDisplay(recipe)));
        FuelRefineryRecipeCache.get().forEach(recipe -> registry.add(new FuelRefineryDisplay(recipe)));
        ElectrolyzerRecipeCache.get().forEach(recipe -> registry.add(new ElectrolyzeDisplay(recipe)));
        BlenderRecipeCache.get().forEach(recipe -> registry.add(new BlendingDisplay(recipe)));
        SpaceStationData.SPACE_STATION_RECIPES.forEach(recipe -> registry.add(new SpaceStationDisplay(recipe)));

        REICompat.markDisplaysBuilt(generation);
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        for(Class<? extends AbstractContainerScreen<?>> screenClass : ApplicationRegistry.applications_menus) {
            zones.register(screenClass, (screen) -> Collections.singleton(new Rectangle(0, 0, screen.width, screen.height)));
        }

    }
}
