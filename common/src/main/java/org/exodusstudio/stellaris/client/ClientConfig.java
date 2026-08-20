package org.exodusstudio.stellaris.client;

import fr.tathan.exoconfig.common.infos.ConfigInfos;
import fr.tathan.exoconfig.common.infos.ScreenInfos;
import fr.tathan.exoconfig.common.utils.Side;

@ConfigInfos(modDisplayName = "Stellaris Client", name = "stellaris_client", side= Side.CLIENT)
public class ClientConfig {

    @ScreenInfos.Description(value = "config.stellaris.showOxygenDebug.desc")
    public boolean showOxygenDebug = false;

    @ScreenInfos.Description(value = "config.stellaris.emissiveTextures.desc")
    public boolean emissiveTextures = true;

}
