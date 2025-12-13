package org.exodusstudio.stellaris.common.config;

import fr.tathan.exoconfig.common.infos.ConfigInfos;
import fr.tathan.exoconfig.common.infos.ScreenInfos;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

@ConfigInfos( name = "stellaris")
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
    @ScreenInfos.Description(value = "The admin part of the config. Beware...", translate = false)
    public Admin admin = new Admin();

    public static class Admin {

        @ScreenInfos.Description(value = "config.stellaris.debugMode.desc")
        public boolean debugMode = false;

        @ScreenInfos.Description(value = "config.stellaris.regenDimension.desc")
        public boolean regenDimension = false;
        public ResourceLocation[] dimensionsToRegen = new ResourceLocation[]{ResourceLocationUtils.id("moon")};

    }

}
