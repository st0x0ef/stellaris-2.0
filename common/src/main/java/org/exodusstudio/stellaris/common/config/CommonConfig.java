package org.exodusstudio.stellaris.common.config;

import fr.tathan.exoconfig.common.infos.ConfigInfos;
import fr.tathan.exoconfig.common.infos.ScreenInfos;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

@ConfigInfos(modDisplayName = "Stellaris", name = "stellaris")
public class CommonConfig {

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
    }

    @ScreenInfos.InnerConfig
    @ScreenInfos.Description("config.stellaris.spaceSuitConfig.desc")
    public SpaceSuitConfig spaceSuitConfig = new SpaceSuitConfig();

    public static class SpaceSuitConfig {
        @ScreenInfos.Description(value = "config.stellaris.spaceSuitConfig.jetFuelConsumptionInterval.desc")
        public int jetFuelConsumptionInterval = 20;

        @ScreenInfos.Description(value = "config.stellaris.spaceSuitConfig.maxJetUpwardSpeed.desc")
        public double maxJetUpwardSpeed = 0.5;
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
    }

    @ScreenInfos.InnerConfig
    @ScreenInfos.Description("config.stellaris.assistantConfig.desc")
    public AssistantConfig assistantConfig = new AssistantConfig();

    public static class AssistantConfig {
        @ScreenInfos.Description(value = "config.stellaris.assistantConfig.enableAssistant.desc")
        public boolean enableAssistant = true;
    }

    @ScreenInfos.InnerConfig
    @ScreenInfos.Description(value = "The admin part of the config. Beware...", translate = false)
    public Admin admin = new Admin();

    public static class Admin {
        @ScreenInfos.Description(value = "config.stellaris.regenDimension.desc")
        public boolean regenDimension = false;
        public Identifier[] dimensionsToRegen = new Identifier[]{IdentifierUtils.id("moon")};

    }

}
