package org.exodusstudio.stellaris.common.config;

import fr.tathan.exoconfig.common.infos.ConfigInfos;
import fr.tathan.exoconfig.common.infos.ScreenInfos;

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
}
