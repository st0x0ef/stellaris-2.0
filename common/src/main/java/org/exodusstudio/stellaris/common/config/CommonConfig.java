package org.exodusstudio.stellaris.common.config;

import fr.tathan.exoconfig.common.infos.ConfigInfos;
import fr.tathan.exoconfig.common.infos.ScreenInfos;
import fr.tathan.exoconfig.common.post_validation.PostValidation;
import org.exodusstudio.stellaris.Stellaris;

@ConfigInfos(modDisplayName = "Stellaris", name = "stellaris")
public class CommonConfig implements PostValidation {

    @ScreenInfos.InnerConfig
    public OilConfig oilConfig = new OilConfig();

    public static class OilConfig {
        public int chunkOilChance = 32;

        public int minOil = 10;
        public int maxOil = 50000;

        public int oilExtractionPerTick = 5;
    }

    @ScreenInfos.InnerConfig
    @ScreenInfos.Description(value = "config.stellaris.gravityConfig.desc")
    public GravityConfig gravityConfig = new GravityConfig();

    public static class GravityConfig {
        @ScreenInfos.Description(value = "config.stellaris.gravityConfig.enableGravityEffects.desc")
        public boolean enableGravityEffects = true;

        @ScreenInfos.Description(value = "config.stellaris.gravityConfig.gravityUpdateInterval.desc")
        public int gravityUpdateInterval = 20;

        @ScreenInfos.Description(value = "config.stellaris.gravityConfig.minGravityManipulatorValue.desc")
        public double minGravityManipulatorValue = 0.5;

        @ScreenInfos.Description(value = "config.stellaris.gravityConfig.maxGravityManipulatorValue.desc")
        public double maxGravityManipulatorValue = 20.0;

        @ScreenInfos.Description(value = "config.stellaris.gravityConfig.gravityManipulatorEnergyPerTick.desc")
        public int gravityManipulatorEnergyPerTick = 1;
    }

    @ScreenInfos.InnerConfig
    @ScreenInfos.Description(value = "config.stellaris.oxygenConfig.desc")
    public OxygenConfig oxygenConfig = new OxygenConfig();

    public static class OxygenConfig {
        @ScreenInfos.Description(value = "config.stellaris.oxygenConfig.enableOxygenSystem.desc")
        public boolean enableOxygenSystem = true;

        @ScreenInfos.Description(value = "config.stellaris.oxygenConfig.oxygenUpdateInterval.desc")
        public int oxygenUpdateInterval = 20;

        @ScreenInfos.Description(value = "config.stellaris.oxygenConfig.oxygenDamageInterval.desc")
        public int oxygenDamageInterval = 20;

        @ScreenInfos.Description(value = "config.stellaris.oxygenConfig.noOxygenDamage.desc")
        public float noOxygenDamage = 0.5f;

        @ScreenInfos.Description(value = "config.stellaris.oxygenConfig.baseOxygenDrain.desc")
        public int baseOxygenDrain = 1;

        @ScreenInfos.Description(value = "config.stellaris.oxygenConfig.sprintOxygenDrainMultiplier.desc")
        public int sprintOxygenDrainMultiplier = 2;

        @ScreenInfos.Description(value = "config.stellaris.oxygenConfig.jetOxygenDrainMultiplier.desc")
        public int jetOxygenDrainMultiplier = 4;
    }

    @ScreenInfos.InnerConfig
    @ScreenInfos.Description("config.stellaris.parasiteConfig.desc")
    public ParasiteConfig parasiteConfig = new ParasiteConfig();

    public static class ParasiteConfig {
        public boolean enableParasiteDrop = true;
        public int minDropIntervalTicks = 100;
        public int randomDropIntervalMaxTicks = 1100;

        public int researchDelay = 600; // 30s
    }

    @ScreenInfos.InnerConfig
    @ScreenInfos.Description("config.stellaris.alienConfig.desc")
    public AlienConfig alienConfig = new AlienConfig();

    public static class AlienConfig {
        @ScreenInfos.Description(value = "config.stellaris.alienConfig.enableAlienSpawn.desc")
        public boolean enableAlienSpawn = true;
    }

    @ScreenInfos.InnerConfig
    @ScreenInfos.Description("config.stellaris.vehicleConfig.desc")
    public VehicleConfig vehicleConfig = new VehicleConfig();

    public static class VehicleConfig {
        public int rocketTpHeight = 400;
        public boolean shouldLanderExplode = true;
        public int cargoUnloadingRadius = 5;

        public int orbitTeleportationYCoord = 62;
    }

    @ScreenInfos.InnerConfig
    @ScreenInfos.Description("config.stellaris.effectsConfig.desc")
    public EffectsConfig effectsConfig = new EffectsConfig();

    public static class EffectsConfig {
        @ScreenInfos.Description(value = "config.stellaris.effectsConfig.infectionTickChance.desc")
        public int infectionTickChance = 100;

        @ScreenInfos.Description(value = "config.stellaris.effectsConfig.infectionDamage.desc")
        public float infectionDamage = 1.0f;

        @ScreenInfos.Description(value = "config.stellaris.effectsConfig.corrosionTickInterval.desc")
        public int corrosionTickInterval = 20;

        @ScreenInfos.Description(value = "config.stellaris.effectsConfig.corrosionDamage.desc")
        public float corrosionDamage = 1.0f;
    }

    @ScreenInfos.InnerConfig
    @ScreenInfos.Description("config.stellaris.machineConfig.desc")
    public MachineConfig machineConfig = new MachineConfig();

    public static class MachineConfig {
        @ScreenInfos.Description(value = "config.stellaris.machineConfig.vacuumatorEnergyPerCraft.desc")
        public int vacuumatorEnergyPerCraft = 100;

        @ScreenInfos.Description(value = "config.stellaris.machineConfig.vacuumatorDurationMultiplier.desc")
        public int vacuumatorDurationMultiplier = 20;

        @ScreenInfos.Description(value = "config.stellaris.machineConfig.vacuumatorWaterPerCraft.desc")
        public int vacuumatorWaterPerCraft = 100;

        @ScreenInfos.Description(value = "config.stellaris.machineConfig.blenderEnergyPerCraft.desc")
        public int blenderEnergyPerCraft = 150;

        @ScreenInfos.Description(value = "config.stellaris.machineConfig.blenderTicksPerCraft.desc")
        public int blenderTicksPerCraft = 60;
    }

    @ScreenInfos.InnerConfig
    @ScreenInfos.Description("config.stellaris.assistantConfig.desc")
    public AssistantConfig assistantConfig = new AssistantConfig();

    public static class AssistantConfig {
        @ScreenInfos.Description(value = "config.stellaris.assistantConfig.enableAssistant.desc")
        public boolean enableAssistant = true;
    }

    @Override
    public void postValidation() {
        oilConfig.chunkOilChance = atLeast("oilConfig.chunkOilChance", oilConfig.chunkOilChance, 1);
        oilConfig.minOil = atLeast("oilConfig.minOil", oilConfig.minOil, 0);
        oilConfig.maxOil = atLeast("oilConfig.maxOil", oilConfig.maxOil, oilConfig.minOil);
        oilConfig.oilExtractionPerTick = atLeast("oilConfig.oilExtractionPerTick", oilConfig.oilExtractionPerTick, 0);

        gravityConfig.gravityUpdateInterval = atLeast("gravityConfig.gravityUpdateInterval", gravityConfig.gravityUpdateInterval, 1);
        gravityConfig.minGravityManipulatorValue = atLeast("gravityConfig.minGravityManipulatorValue", gravityConfig.minGravityManipulatorValue, 0.0);
        gravityConfig.maxGravityManipulatorValue = atLeast("gravityConfig.maxGravityManipulatorValue", gravityConfig.maxGravityManipulatorValue, gravityConfig.minGravityManipulatorValue);
        gravityConfig.gravityManipulatorEnergyPerTick = atLeast("gravityConfig.gravityManipulatorEnergyPerTick", gravityConfig.gravityManipulatorEnergyPerTick, 0);

        oxygenConfig.oxygenUpdateInterval = atLeast("oxygenConfig.oxygenUpdateInterval", oxygenConfig.oxygenUpdateInterval, 1);
        oxygenConfig.oxygenDamageInterval = atLeast("oxygenConfig.oxygenDamageInterval", oxygenConfig.oxygenDamageInterval, 1);
        oxygenConfig.noOxygenDamage = atLeast("oxygenConfig.noOxygenDamage", oxygenConfig.noOxygenDamage, 0F);
        oxygenConfig.baseOxygenDrain = atLeast("oxygenConfig.baseOxygenDrain", oxygenConfig.baseOxygenDrain, 1);
        oxygenConfig.sprintOxygenDrainMultiplier = atLeast("oxygenConfig.sprintOxygenDrainMultiplier", oxygenConfig.sprintOxygenDrainMultiplier, 1);
        oxygenConfig.jetOxygenDrainMultiplier = atLeast("oxygenConfig.jetOxygenDrainMultiplier", oxygenConfig.jetOxygenDrainMultiplier, 1);

        parasiteConfig.minDropIntervalTicks = atLeast("parasiteConfig.minDropIntervalTicks", parasiteConfig.minDropIntervalTicks, 1);
        parasiteConfig.randomDropIntervalMaxTicks = atLeast("parasiteConfig.randomDropIntervalMaxTicks", parasiteConfig.randomDropIntervalMaxTicks, 0);
        parasiteConfig.researchDelay = atLeast("parasiteConfig.researchDelay", parasiteConfig.researchDelay, 1);

        vehicleConfig.cargoUnloadingRadius = atLeast("vehicleConfig.cargoUnloadingRadius", vehicleConfig.cargoUnloadingRadius, 0);

        effectsConfig.infectionTickChance = atLeast("effectsConfig.infectionTickChance", effectsConfig.infectionTickChance, 0);
        effectsConfig.infectionDamage = atLeast("effectsConfig.infectionDamage", effectsConfig.infectionDamage, 0F);
        effectsConfig.corrosionTickInterval = atLeast("effectsConfig.corrosionTickInterval", effectsConfig.corrosionTickInterval, 0);
        effectsConfig.corrosionDamage = atLeast("effectsConfig.corrosionDamage", effectsConfig.corrosionDamage, 0F);

        machineConfig.vacuumatorEnergyPerCraft = atLeast("machineConfig.vacuumatorEnergyPerCraft", machineConfig.vacuumatorEnergyPerCraft, 0);
        machineConfig.vacuumatorDurationMultiplier = atLeast("machineConfig.vacuumatorDurationMultiplier", machineConfig.vacuumatorDurationMultiplier, 1);
        machineConfig.vacuumatorWaterPerCraft = atLeast("machineConfig.vacuumatorWaterPerCraft", machineConfig.vacuumatorWaterPerCraft, 0);
        machineConfig.blenderEnergyPerCraft = atLeast("machineConfig.blenderEnergyPerCraft", machineConfig.blenderEnergyPerCraft, 0);
        machineConfig.blenderTicksPerCraft = atLeast("machineConfig.blenderTicksPerCraft", machineConfig.blenderTicksPerCraft, 1);
    }

    private static int atLeast(String name, int value, int min) {
        if (value >= min) {
            return value;
        }

        Stellaris.LOG.warn("Config value {} = {} is below its minimum of {}; using {}", name, value, min, min);
        return min;
    }

    private static float atLeast(String name, float value, float min) {
        if (value >= min && Float.isFinite(value)) {
            return value;
        }

        Stellaris.LOG.warn("Config value {} = {} is invalid (minimum {}); using {}", name, value, min, min);
        return min;
    }

    private static double atLeast(String name, double value, double min) {
        if (value >= min && Double.isFinite(value)) {
            return value;
        }

        Stellaris.LOG.warn("Config value {} = {} is invalid (minimum {}); using {}", name, value, min, min);
        return min;
    }
}
