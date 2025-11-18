package org.exodusstudio.stellaris.common.config;

import fr.tathan.exoconfig.common.infos.ConfigInfos;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

@ConfigInfos( name = "stellaris")
public class CommonConfig {
    public boolean debugMode = false;
    public boolean regenWorld = false;
    public ResourceLocation[] dimensionsToRegen = new ResourceLocation[]{ResourceLocationUtils.id("moon")};
}
