package org.exodusstudio.stellaris.common.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.utils.GravityUtils;

import java.util.*;

public class PlanetsData extends SimpleJsonResourceReloadListener<Planet> {
    public static final String ID = "planets";

    public static volatile List<Planet> PLANETS = new ArrayList<>();
    public static volatile Map<Identifier, ResourceKey<Level>> PLANETS_LEVEL = new HashMap<>();

    public PlanetsData() {
        super(Planet.CODEC, FileToIdConverter.json(ID));
    }

    @Override
    protected void apply(Map<Identifier, Planet> planetMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        //TODO : sort them by distance
        setPlanets(planetMap.values());
    }

    /**
     * Replaces the current planet store and rebuilds the dimension->level-key map. Used both by
     * the server-data reload ({@link #apply}) and by the client-side sync packet handler, so
     * clients populate {@link #PLANETS} (they never run the reload listener on a dedicated server).
     */
    public static void setPlanets(Collection<Planet> planets) {
        GravityUtils.clearCaches();

        List<Planet> newPlanets = new ArrayList<>(planets);

        Map<Identifier, ResourceKey<Level>> newPlanetsLevel = new HashMap<>();
        for (Planet planet : newPlanets) {
            newPlanetsLevel.put(planet.dimension(), ResourceKey.create(Registries.DIMENSION, planet.dimension()));
        }

        PLANETS = newPlanets;
        PLANETS_LEVEL = newPlanetsLevel;
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
