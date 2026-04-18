package org.exodusstudio.stellaris.common.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.Stellaris;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanetsData extends SimpleJsonResourceReloadListener<Planet> {
    public static final String ID = "planets";

    public static final List<Planet> PLANETS = new ArrayList<>();
    public static final Map<Identifier, ResourceKey<Level>> PLANETS_LEVEL = new HashMap<>();

    public PlanetsData() {
        super(Planet.CODEC, FileToIdConverter.json(ID));
    }

    @Override
    protected void apply(Map<Identifier, Planet> planetMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        PLANETS.clear();
        PLANETS.addAll(planetMap.values());

        //TODO : sort them by distance

        PLANETS_LEVEL.clear();
        for (Planet planet : PLANETS) {
            PLANETS_LEVEL.put(planet.dimension(), ResourceKey.create(Registries.DIMENSION, planet.dimension()));
        }
    }

    public static ServerLevel getPlanetLevel(MinecraftServer server, Planet planet) {
        ResourceKey<Level> levelKey = PLANETS_LEVEL.get(planet.dimension());
        if (levelKey == null) {
            return null;
        }
        return server.getLevel(levelKey);
    }

    public static Planet getPlanet(ResourceKey<Level> level) {
        for (Planet planet : PLANETS) {
            if (planet.is(level)) {
                return planet;
            }
        }
        return null;
    }
}
