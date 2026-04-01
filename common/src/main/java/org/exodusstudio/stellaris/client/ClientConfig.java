package org.exodusstudio.stellaris.client;

import fr.tathan.exoconfig.common.infos.ConfigInfos;
import fr.tathan.exoconfig.common.infos.ScreenInfos;
import fr.tathan.exoconfig.common.utils.Side;

@ConfigInfos(modDisplayName = "Stellaris Client", name = "stellaris_client", side= Side.CLIENT)
public class ClientConfig {

    @ScreenInfos.InnerConfig
    @ScreenInfos.Description(value = "config.stellaris.fluidOutputConfig.desc")
    public FluidOutputConfig fluidOutputConfig = new FluidOutputConfig();


    public static class FluidOutputConfig {
        @ScreenInfos.Description(value = "config.stellaris.fluidOutputConfig.showNeighborsBlock.desc")
        public boolean showNeighborsBlock = false;

        @ScreenInfos.Description(value = "config.stellaris.fluidOutputConfig.fluidsColors.desc")
        public String[] fluidsColors = new String[]{"red", "lime", "blue", "yellow", "cyan", "magenta"};
    }


}
