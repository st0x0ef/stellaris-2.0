package org.exodusstudio.stellaris.common.data.space_station;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpaceStationData extends SimpleJsonResourceReloadListener<SpaceStationRecipe> {

    public static volatile List<SpaceStationRecipe> SPACE_STATION_RECIPES = new ArrayList<>();
    public static final String ID = "space_stations";

    public SpaceStationData() {
        super(SpaceStationRecipe.CODEC, FileToIdConverter.json(ID));
    }

    @Override
    protected void apply(Map<Identifier, SpaceStationRecipe> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        SPACE_STATION_RECIPES = new ArrayList<>(object.values());
    }
}
