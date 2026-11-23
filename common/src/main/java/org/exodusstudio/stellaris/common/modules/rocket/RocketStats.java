package org.exodusstudio.stellaris.common.modules.rocket;

import org.exodusstudio.stellaris.common.modules.Modules;

public record RocketStats(int tankCapacity, float fuelCostMultiplier) {
    public static final int BASE_TANK_CAPACITY = 3000;

    public static String formatMultiplier(float multiplier) {
        return String.valueOf(Math.round(multiplier * 100) / 100.0);
    }

    public static RocketStats of(Modules<RocketModule> modules) {
        Builder builder = new Builder();
        for (RocketModule module : modules) {
            module.applyStats(builder);
        }

        return builder.build();
    }

    public static class Builder {
        private int tankCapacity = BASE_TANK_CAPACITY;
        private float fuelCostMultiplier = 1f;

        public Builder addTankCapacity(int amount) {
            this.tankCapacity += amount;
            return this;
        }

        public Builder multiplyFuelCost(float multiplier) {
            this.fuelCostMultiplier *= multiplier;
            return this;
        }

        public RocketStats build() {
            return new RocketStats(this.tankCapacity, this.fuelCostMultiplier);
        }
    }
}
