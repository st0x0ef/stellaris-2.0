package org.exodusstudio.stellaris.common.config;

import fr.tathan.exoconfig.common.infos.ConfigInfos;
import fr.tathan.exoconfig.common.infos.ScreenInfos;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

@ConfigInfos(modDisplayName = "Stellaris", name = "stellaris")
public class CommonConfig {

    public boolean debugMode = false;

    @ScreenInfos.InnerConfig
    public OilConfig oilConfig = new OilConfig();


    public static class OilConfig {
        public int chunkOilChance = 16;

        public int minOil = 10;
        public int maxOil = 50000;

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
    @ScreenInfos.Description(value = "The admin part of the config. Beware...", translate = false)
    public Admin admin = new Admin();

    public static class Admin {

        @ScreenInfos.Description(value = "config.stellaris.debugMode.desc")
        public boolean debugMode = false;

        @ScreenInfos.Description(value = "config.stellaris.regenDimension.desc")
        public boolean regenDimension = false;
        public Identifier[] dimensionsToRegen = new Identifier[]{IdentifierUtils.id("moon")};

    }

}
