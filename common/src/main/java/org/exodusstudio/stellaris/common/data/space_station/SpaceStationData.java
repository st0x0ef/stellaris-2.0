package org.exodusstudio.stellaris.common.data.space_station;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpaceStationData extends SimpleJsonResourceReloadListener<SpaceStationRecipe> {

    public static final List<SpaceStationRecipe> SPACE_STATION_RECIPES = new ArrayList<>();


    public SpaceStationData() {
        super(SpaceStationRecipe.CODEC, FileToIdConverter.json("space_stations"));
    }

    @Override
    protected void apply(Map<Identifier, SpaceStationRecipe> object, ResourceManager resourceManager, ProfilerFiller profiler) {

        SpaceStationRecipe recipe = new SpaceStationRecipe(List.of(
                new SpaceStationRecipe.IngredientWithCount(
                        Either.left(ResourceKey.create(Registries.ITEM, IdentifierUtils.id("rocket"))), 10))
                , IdentifierUtils.id("space_station_small"), new Vec3i(0, 1, 0));
        JsonElement element = SpaceStationRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe).getOrThrow();

        Stellaris.LOG.info(element.toString());

        SPACE_STATION_RECIPES.clear();
        SPACE_STATION_RECIPES.addAll(object.values());
    }
}
