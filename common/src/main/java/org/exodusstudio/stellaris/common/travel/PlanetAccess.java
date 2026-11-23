package org.exodusstudio.stellaris.common.travel;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.modules.rocket.RocketStats;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlanetAccess {
    public static final int FUEL_PER_DISTANCE = 60;
    private static final int MAX_ORBIT_DEPTH = 16;

    public static List<Component> checkRequirements(Player player, Modules<RocketModule> modules, Planet destination) {
        List<Component> failures = new ArrayList<>();

        for (PlanetRequirement requirement : destination.getRequirements()) {
            if (!requirement.test(player, modules)) {
                failures.add(requirement.description());
            }
        }

        return failures;
    }

    public static List<Component> check(Player player, Modules<RocketModule> modules, int fuel, @Nullable Planet origin, Planet destination) {
        List<Component> failures = checkRequirements(player, modules, destination);
        int cost = getFuelCost(modules, origin, destination);
        int capacity = RocketStats.of(modules).tankCapacity();

        if (cost > capacity) {
            failures.add(Component.translatable("message.stellaris.planet_requirement.tank", cost, capacity));
        } else if (cost > fuel) {
            failures.add(Component.translatable("message.stellaris.planet_requirement.fuel", cost, fuel));
        }

        return failures;
    }

    public static boolean canReach(Player player, Modules<RocketModule> modules, int fuel, @Nullable Planet origin, Planet destination) {
        return player.isCreative() || check(player, modules, fuel, origin, destination).isEmpty();
    }

    public static int getFuelCost(Modules<RocketModule> modules, @Nullable Planet origin, Planet destination) {
        return (int) Math.round(getDistance(origin, destination) * FUEL_PER_DISTANCE * RocketStats.of(modules).fuelCostMultiplier());
    }

    public static int getCheapestFuelCost(Modules<RocketModule> modules, @Nullable Planet origin) {
        int cheapest = Integer.MAX_VALUE;
        for (Planet planet : PlanetsData.PLANETS) {
            if (!planet.equals(origin)) {
                cheapest = Math.min(cheapest, getFuelCost(modules, origin, planet));
            }
        }

        return cheapest == Integer.MAX_VALUE ? 0 : cheapest;
    }

    public static List<Planet> getReachablePlanets(Player player, Modules<RocketModule> modules, int fuel, @Nullable Planet origin) {
        List<Planet> reachable = new ArrayList<>();
        for (Planet planet : PlanetsData.PLANETS) {
            if (!planet.equals(origin) && check(player, modules, fuel, origin, planet).isEmpty()) {
                reachable.add(planet);
            }
        }

        return reachable;
    }

    public static List<Planet> getPlanetsRequiring(Identifier module) {
        List<Planet> planets = new ArrayList<>();
        for (Planet planet : PlanetsData.PLANETS) {
            for (PlanetRequirement requirement : planet.getRequirements()) {
                if (requirement instanceof ModuleRequirement moduleRequirement && moduleRequirement.module().equals(module)) {
                    planets.add(planet);
                    break;
                }
            }
        }

        return planets;
    }

    public static Component getPlanetNames(List<Planet> planets) {
        MutableComponent names = Component.empty();
        for (int i = 0; i < planets.size(); i++) {
            if (i > 0) {
                names.append(", ");
            }
            names.append(Component.translatable(planets.get(i).translationKey()));
        }

        return names;
    }

    public static double getDistance(@Nullable Planet from, Planet to) {
        if (from == null || from.equals(to)) {
            return 0;
        }

        List<Identifier> fromChain = getOrbitChain(from);
        List<Identifier> toChain = getOrbitChain(to);

        for (int i = 0; i < fromChain.size(); i++) {
            int j = toChain.indexOf(fromChain.get(i));
            if (j >= 0) {
                return climb(fromChain, i) + climb(toChain, j) + gap(fromChain, i, toChain, j);
            }
        }

        return 0;
    }

    private static List<Identifier> getOrbitChain(Planet planet) {
        List<Identifier> chain = new ArrayList<>();
        chain.add(planet.dimension());

        Planet current = planet;
        while (current != null && current.orbit().isPresent() && chain.size() < MAX_ORBIT_DEPTH) {
            Identifier around = current.orbit().get().around();
            chain.add(around);
            current = getPlanet(around);
        }

        return chain;
    }

    private static double climb(List<Identifier> chain, int ancestorIndex) {
        double total = 0;
        for (int k = 0; k < ancestorIndex - 1; k++) {
            total += getOrbitDistance(chain.get(k));
        }

        return total;
    }

    private static double gap(List<Identifier> fromChain, int i, List<Identifier> toChain, int j) {
        if (i == 0) {
            return getOrbitDistance(toChain.get(j - 1));
        }

        if (j == 0) {
            return getOrbitDistance(fromChain.get(i - 1));
        }

        return Math.abs(getOrbitDistance(fromChain.get(i - 1)) - getOrbitDistance(toChain.get(j - 1)));
    }

    private static double getOrbitDistance(Identifier body) {
        Planet planet = getPlanet(body);
        return planet != null && planet.orbit().isPresent() ? planet.orbit().get().distance() : 0;
    }

    private static @Nullable Planet getPlanet(Identifier dimension) {
        return PlanetsData.getPlanet(ResourceKey.create(Registries.DIMENSION, dimension));
    }
}
